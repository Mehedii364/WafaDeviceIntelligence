package com.example.data.model

enum class BatteryChargingState(val label: String) {
  CHARGING("Charging"),
  DISCHARGING("Discharging"),
  FULL("Full"),
  NOT_CHARGING("Not Charging"),
  UNKNOWN("Unknown")
}

enum class PluggedSource(val label: String) {
  AC("AC Adapter"),
  USB("USB Port"),
  WIRELESS("Wireless Charger"),
  DOCK("Dock"),
  NONE("Unplugged")
}

data class BatteryInfo(
  val percentage: Int = 0,
  val state: BatteryChargingState = BatteryChargingState.UNKNOWN,
  val plugged: PluggedSource = PluggedSource.NONE,
  val temperatureC: Float? = null,
  val voltageV: Float? = null,
  val currentNowMa: Long? = null,
  val isPowerSaveMode: Boolean = false,
  val healthString: String = "Good",
  val technology: String = "Li-ion",
  val chargeRatePerHour: Float? = null,
  val estimatedMinutesToFull: Int? = null,
  val timestamp: Long = System.currentTimeMillis()
)

data class ChargingSession(
  val id: Long = 0,
  val startTime: Long,
  val endTime: Long? = null,
  val initialPercentage: Int,
  val currentPercentage: Int,
  val finalPercentage: Int? = null,
  val durationMs: Long = 0,
  val minTemperatureC: Float? = null,
  val maxTemperatureC: Float? = null,
  val minVoltageV: Float? = null,
  val maxVoltageV: Float? = null,
  val pluggedSource: PluggedSource = PluggedSource.NONE,
  val isCompleted: Boolean = false,
  val completionReason: String = "In Progress"
)
