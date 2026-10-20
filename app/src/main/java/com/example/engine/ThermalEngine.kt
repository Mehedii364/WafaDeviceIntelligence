package com.example.engine

import android.content.Context
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThermalEngine(private val context: Context) {
  private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager

  private val _thermalStatusString = MutableStateFlow(getThermalStatusDescription())
  val thermalStatusString: StateFlow<String> = _thermalStatusString.asStateFlow()

  private var thermalListener: PowerManager.OnThermalStatusChangedListener? = null

  fun startMonitoring() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
      val listener = PowerManager.OnThermalStatusChangedListener { status ->
        _thermalStatusString.value = mapThermalStatus(status)
      }
      thermalListener = listener
      try {
        powerManager.addThermalStatusListener(context.mainExecutor, listener)
      } catch (_: Exception) {}
    }
  }

  fun stopMonitoring() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null && thermalListener != null) {
      try {
        powerManager.removeThermalStatusListener(thermalListener!!)
      } catch (_: Exception) {}
    }
  }

  fun getCurrentStatus(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
      mapThermalStatus(powerManager.currentThermalStatus)
    } else {
      "Normal (Legacy API / Hardware Controlled)"
    }
  }

  private fun getThermalStatusDescription(): String {
    return getCurrentStatus()
  }

  private fun mapThermalStatus(status: Int): String {
    return when (status) {
      PowerManager.THERMAL_STATUS_NONE -> "Nominal / Normal"
      PowerManager.THERMAL_STATUS_LIGHT -> "Light Throttling"
      PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Thermal Throttling"
      PowerManager.THERMAL_STATUS_SEVERE -> "Severe Thermal Throttling"
      PowerManager.THERMAL_STATUS_CRITICAL -> "Critical Thermal State"
      PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency Cooldown Active"
      PowerManager.THERMAL_STATUS_SHUTDOWN -> "Shutdown Imminent"
      else -> "Normal (Verified by OS)"
    }
  }
}
