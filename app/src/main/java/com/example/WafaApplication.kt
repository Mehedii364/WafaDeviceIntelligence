package com.example

import android.app.Application
import com.example.data.local.ThemePreferences
import com.example.data.local.WafaDatabase
import com.example.engine.BatteryEngine
import com.example.engine.HardwareEngine
import com.example.engine.HardwareTestLabEngine
import com.example.engine.NetworkEngine
import com.example.engine.PerformanceEngine
import com.example.engine.ReportEngine
import com.example.engine.SensorEngine
import com.example.engine.ThermalEngine

class WafaApplication : Application() {
  lateinit var database: WafaDatabase private set
  lateinit var themePreferences: ThemePreferences private set
  lateinit var batteryEngine: BatteryEngine private set
  lateinit var hardwareEngine: HardwareEngine private set
  lateinit var sensorEngine: SensorEngine private set
  lateinit var networkEngine: NetworkEngine private set
  lateinit var thermalEngine: ThermalEngine private set
  lateinit var performanceEngine: PerformanceEngine private set
  lateinit var hardwareTestLabEngine: HardwareTestLabEngine private set
  lateinit var reportEngine: ReportEngine private set

  override fun onCreate() {
    super.onCreate()
    database = WafaDatabase.getDatabase(this)
    themePreferences = ThemePreferences(this)
    batteryEngine = BatteryEngine(this)
    hardwareEngine = HardwareEngine(this)
    sensorEngine = SensorEngine(this)
    networkEngine = NetworkEngine(this)
    thermalEngine = ThermalEngine(this)
    performanceEngine = PerformanceEngine(this, thermalEngine, batteryEngine)
    hardwareTestLabEngine = HardwareTestLabEngine(this)
    reportEngine = ReportEngine(this)

    batteryEngine.startMonitoring()
    sensorEngine.registerEssentialSensors()
    thermalEngine.startMonitoring()
  }

  override fun onTerminate() {
    super.onTerminate()
    batteryEngine.stopMonitoring()
    sensorEngine.unregisterSensors()
    thermalEngine.stopMonitoring()
  }
}
