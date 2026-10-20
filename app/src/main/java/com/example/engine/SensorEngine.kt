package com.example.engine

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.data.model.SensorItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorEngine(private val context: Context) : SensorEventListener {
  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

  private val _activeSensorReadings = MutableStateFlow<Map<Int, FloatArray>>(emptyMap())
  val activeSensorReadings: StateFlow<Map<Int, FloatArray>> = _activeSensorReadings.asStateFlow()

  // Track sample counts for anomaly / freeze detection
  private val lastUpdateTimes = mutableMapOf<Int, Long>()
  private val updateCounts = mutableMapOf<Int, Int>()

  fun getAllSensors(): List<SensorItem> {
    val rawList = sensorManager.getSensorList(Sensor.TYPE_ALL)
    return rawList.map { s ->
      val (typeName, unit) = mapSensorType(s.type)
      SensorItem(
        type = s.type,
        typeName = typeName,
        name = s.name,
        vendor = s.vendor,
        version = s.version,
        powerMa = s.power,
        resolution = s.resolution,
        maximumRange = s.maximumRange,
        minDelayUs = s.minDelay,
        isWakeUp = s.isWakeUpSensor,
        isAvailable = true,
        unit = unit
      )
    }
  }

  fun registerEssentialSensors() {
    val types = intArrayOf(
      Sensor.TYPE_ACCELEROMETER,
      Sensor.TYPE_GYROSCOPE,
      Sensor.TYPE_MAGNETIC_FIELD,
      Sensor.TYPE_LIGHT,
      Sensor.TYPE_PROXIMITY,
      Sensor.TYPE_PRESSURE
    )
    for (t in types) {
      val sensor = sensorManager.getDefaultSensor(t)
      if (sensor != null) {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
      }
    }
  }

  fun unregisterSensors() {
    try {
      sensorManager.unregisterListener(this)
    } catch (_: Exception) {}
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null) return
    val current = _activeSensorReadings.value.toMutableMap()
    current[event.sensor.type] = event.values.clone()
    _activeSensorReadings.value = current

    val now = System.currentTimeMillis()
    lastUpdateTimes[event.sensor.type] = now
    updateCounts[event.sensor.type] = (updateCounts[event.sensor.type] ?: 0) + 1
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

  fun isSensorFrozen(sensorType: Int): Boolean {
    val count = updateCounts[sensorType] ?: 0
    val lastUpdate = lastUpdateTimes[sensorType] ?: 0L
    // If registered more than 3 seconds ago and has 0 updates
    return count == 0 && (System.currentTimeMillis() - lastUpdate > 3000)
  }

  private fun mapSensorType(type: Int): Pair<String, String> {
    return when (type) {
      Sensor.TYPE_ACCELEROMETER -> Pair("Accelerometer", "m/s²")
      Sensor.TYPE_GYROSCOPE -> Pair("Gyroscope", "rad/s")
      Sensor.TYPE_MAGNETIC_FIELD -> Pair("Magnetometer", "µT")
      Sensor.TYPE_LIGHT -> Pair("Ambient Light", "lx")
      Sensor.TYPE_PROXIMITY -> Pair("Proximity", "cm")
      Sensor.TYPE_PRESSURE -> Pair("Barometer / Pressure", "hPa")
      Sensor.TYPE_GRAVITY -> Pair("Gravity", "m/s²")
      Sensor.TYPE_LINEAR_ACCELERATION -> Pair("Linear Acceleration", "m/s²")
      Sensor.TYPE_ROTATION_VECTOR -> Pair("Rotation Vector", "unit")
      Sensor.TYPE_STEP_COUNTER -> Pair("Step Counter", "steps")
      Sensor.TYPE_STEP_DETECTOR -> Pair("Step Detector", "events")
      else -> Pair("Sensor ($type)", "")
    }
  }
}
