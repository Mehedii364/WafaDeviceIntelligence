package com.example.engine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import com.example.data.local.WafaDatabase
import com.example.data.local.entity.ChargingSessionEntity
import com.example.data.model.BatteryChargingState
import com.example.data.model.BatteryInfo
import com.example.data.model.ChargingSession
import com.example.data.model.PluggedSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class BatteryEngine(private val context: Context) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private val database = WafaDatabase.getDatabase(context)

  private val _batteryInfo = MutableStateFlow(BatteryInfo())
  val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()

  private var activeSessionId: Long? = null
  private var sessionStartTime: Long = 0L
  private var sessionStartPct: Int = 0
  private var sessionMinTemp: Float = Float.MAX_VALUE
  private var sessionMaxTemp: Float = Float.MIN_VALUE
  private var sessionMinVolt: Float = Float.MAX_VALUE
  private var sessionMaxVolt: Float = Float.MIN_VALUE

  private val batteryReceiver = object : BroadcastReceiver() {
    override fun onReceive(ctx: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
        parseBatteryIntent(intent)
      }
    }
  }

  fun startMonitoring() {
    try {
      val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
      val stickyIntent = androidx.core.content.ContextCompat.registerReceiver(
        context,
        batteryReceiver,
        filter,
        androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
      )
      if (stickyIntent != null) {
        parseBatteryIntent(stickyIntent)
      }
    } catch (_: Exception) {}
  }

  fun stopMonitoring() {
    try {
      context.unregisterReceiver(batteryReceiver)
    } catch (_: Exception) {}
  }

  private fun parseBatteryIntent(intent: Intent) {
    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    val percentage = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 0

    val statusInt = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    val chargingState = when (statusInt) {
      BatteryManager.BATTERY_STATUS_CHARGING -> BatteryChargingState.CHARGING
      BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryChargingState.DISCHARGING
      BatteryManager.BATTERY_STATUS_FULL -> BatteryChargingState.FULL
      BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryChargingState.NOT_CHARGING
      else -> BatteryChargingState.UNKNOWN
    }

    val pluggedInt = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
    val pluggedSource = when (pluggedInt) {
      BatteryManager.BATTERY_PLUGGED_AC -> PluggedSource.AC
      BatteryManager.BATTERY_PLUGGED_USB -> PluggedSource.USB
      BatteryManager.BATTERY_PLUGGED_WIRELESS -> PluggedSource.WIRELESS
      BatteryManager.BATTERY_PLUGGED_DOCK -> PluggedSource.DOCK
      else -> PluggedSource.NONE
    }

    val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
    val tempC = if (rawTemp > 0) rawTemp / 10.0f else null

    val rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
    val voltageV = if (rawVoltage > 0) {
      if (rawVoltage > 1000) rawVoltage / 1000.0f else rawVoltage.toFloat()
    } else null

    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    val currentNowMicroA = batteryManager?.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
    val currentNowMa = if (currentNowMicroA != null && currentNowMicroA != Long.MIN_VALUE && currentNowMicroA != 0L) {
      currentNowMicroA / 1000L
    } else null

    val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    val isPowerSave = powerManager?.isPowerSaveMode ?: false

    val healthInt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
    val healthStr = when (healthInt) {
      BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
      BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
      BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
      BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
      BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
      BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
      else -> "Normal (Verified by OS)"
    }

    val tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

    // Charging session management
    handleSessionTracking(chargingState, pluggedSource, percentage, tempC, voltageV)

    // Calculate charge rate & ETA
    val (ratePerHour, etaMinutes) = calculateRateAndEta(chargingState, percentage)

    _batteryInfo.value = BatteryInfo(
      percentage = percentage,
      state = chargingState,
      plugged = pluggedSource,
      temperatureC = tempC,
      voltageV = voltageV,
      currentNowMa = currentNowMa,
      isPowerSaveMode = isPowerSave,
      healthString = healthStr,
      technology = tech,
      chargeRatePerHour = ratePerHour,
      estimatedMinutesToFull = etaMinutes,
      timestamp = System.currentTimeMillis()
    )
  }

  private fun handleSessionTracking(
    state: BatteryChargingState,
    plugged: PluggedSource,
    percentage: Int,
    tempC: Float?,
    voltageV: Float?
  ) {
    val isCharging = state == BatteryChargingState.CHARGING || plugged != PluggedSource.NONE
    val now = System.currentTimeMillis()

    if (isCharging) {
      if (activeSessionId == null) {
        // Start new session
        sessionStartTime = now
        sessionStartPct = percentage
        sessionMinTemp = tempC ?: 25.0f
        sessionMaxTemp = tempC ?: 25.0f
        sessionMinVolt = voltageV ?: 4.0f
        sessionMaxVolt = voltageV ?: 4.0f

        scope.launch {
          val newId = database.chargingSessionDao().insertSession(
            ChargingSessionEntity(
              startTime = sessionStartTime,
              initialPercentage = sessionStartPct,
              currentPercentage = percentage,
              minTemperatureC = sessionMinTemp,
              maxTemperatureC = sessionMaxTemp,
              minVoltageV = sessionMinVolt,
              maxVoltageV = sessionMaxVolt,
              pluggedSource = plugged.label,
              isCompleted = false,
              completionReason = "Charging in progress"
            )
          )
          activeSessionId = newId
        }
      } else {
        // Update current active session
        if (tempC != null) {
          sessionMinTemp = min(sessionMinTemp, tempC)
          sessionMaxTemp = max(sessionMaxTemp, tempC)
        }
        if (voltageV != null) {
          sessionMinVolt = min(sessionMinVolt, voltageV)
          sessionMaxVolt = max(sessionMaxVolt, voltageV)
        }
        val duration = now - sessionStartTime

        scope.launch {
          val id = activeSessionId ?: return@launch
          database.chargingSessionDao().updateSession(
            ChargingSessionEntity(
              id = id,
              startTime = sessionStartTime,
              initialPercentage = sessionStartPct,
              currentPercentage = percentage,
              durationMs = duration,
              minTemperatureC = sessionMinTemp,
              maxTemperatureC = sessionMaxTemp,
              minVoltageV = sessionMinVolt,
              maxVoltageV = sessionMaxVolt,
              pluggedSource = plugged.label,
              isCompleted = false,
              completionReason = "Charging in progress"
            )
          )
        }
      }
    } else {
      // Not charging: if we had an active session, complete it
      if (activeSessionId != null) {
        val duration = now - sessionStartTime
        val id = activeSessionId!!
        activeSessionId = null

        scope.launch {
          database.chargingSessionDao().updateSession(
            ChargingSessionEntity(
              id = id,
              startTime = sessionStartTime,
              endTime = now,
              initialPercentage = sessionStartPct,
              currentPercentage = percentage,
              finalPercentage = percentage,
              durationMs = duration,
              minTemperatureC = sessionMinTemp,
              maxTemperatureC = sessionMaxTemp,
              minVoltageV = sessionMinVolt,
              maxVoltageV = sessionMaxVolt,
              pluggedSource = plugged.label,
              isCompleted = true,
              completionReason = if (percentage >= 100) "Full Charge Reached" else "Unplugged by User"
            )
          )
        }
      }
    }
  }

  private fun calculateRateAndEta(
    state: BatteryChargingState,
    percentage: Int
  ): Pair<Float?, Int?> {
    if (activeSessionId == null || sessionStartTime == 0L) return Pair(null, null)
    val elapsedMinutes = (System.currentTimeMillis() - sessionStartTime) / 60000.0f
    val pctDiff = percentage - sessionStartPct

    if (elapsedMinutes < 2.0f || pctDiff <= 0) return Pair(null, null)

    val ratePerHour = (pctDiff / elapsedMinutes) * 60.0f
    val remainingPct = 100 - percentage
    val etaMinutes = if (remainingPct > 0 && ratePerHour > 1.0f) {
      ((remainingPct / ratePerHour) * 60.0f).toInt()
    } else null

    return Pair(ratePerHour, etaMinutes)
  }
}
