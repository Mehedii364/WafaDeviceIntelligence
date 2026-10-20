package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.service.BatteryMonitorService
import com.example.ui.navigation.WafaBottomNav
import com.example.ui.components.WafaTopBar
import com.example.ui.navigation.WafaScreen
import com.example.ui.screens.ChargeLabScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.FullCheckScreen
import com.example.ui.screens.HardwareLabScreen
import com.example.ui.screens.HistoryReportsScreen
import com.example.ui.screens.NetworkScreen
import com.example.ui.screens.SensorsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StorageScreen
import com.example.ui.screens.ThermalPerformanceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WafaThemeMode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val app = application as WafaApplication

    setContent {
      val currentTheme by app.themePreferences.themeMode.collectAsState(initial = WafaThemeMode.OBSIDIAN_AURORA)
      val scope = rememberCoroutineScope()

      MyApplicationTheme(themeMode = currentTheme) {
        var currentScreen by remember { mutableStateOf(WafaScreen.DASHBOARD) }

        // State flows
        val batteryInfo by app.batteryEngine.batteryInfo.collectAsState()
        val sessions by app.database.chargingSessionDao().getAllSessions().collectAsState(initial = emptyList())
        val historyList by app.database.diagnosticHistoryDao().getAllHistory().collectAsState(initial = emptyList())
        val benchmarks by app.database.benchmarkHistoryDao().getAllBenchmarks().collectAsState(initial = emptyList())
        val thermalStatus by app.thermalEngine.thermalStatusString.collectAsState()
        val liveSensorReadings by app.sensorEngine.activeSensorReadings.collectAsState()

        // Cached diagnostics
        val hardware = remember { app.hardwareEngine.getHardwareDiagnostics() }
        val storage = remember { app.hardwareEngine.getStorageDiagnostics() }
        val network = remember { app.networkEngine.getNetworkState() }
        val sensors = remember { app.sensorEngine.getAllSensors() }
        val displayInfo = remember { app.hardwareEngine.getDisplayDiagnostics() }
        val cameraResults = remember { app.hardwareEngine.getCameraCharacteristicsSummary() }
        val featureResults = remember { app.hardwareEngine.getSystemFeaturesMatrix() }

        BackHandler(enabled = currentScreen != WafaScreen.DASHBOARD) {
          currentScreen = WafaScreen.DASHBOARD
        }

        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
          topBar = {
            WafaTopBar(
              title = currentScreen.title,
              subtitle = if (currentScreen == WafaScreen.DASHBOARD) "Wafa Zone • Mehedi364" else null,
              onSettingsClick = { currentScreen = WafaScreen.SETTINGS },
              onRefreshClick = {
                // Trigger refresh of battery or active stats
              }
            )
          },
          bottomBar = {
            WafaBottomNav(
              currentScreen = currentScreen,
              onScreenSelected = { currentScreen = it }
            )
          }
        ) { innerPadding ->
          val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

          when (currentScreen) {
            WafaScreen.DASHBOARD -> {
              DashboardScreen(
                hardware = hardware,
                battery = batteryInfo,
                storage = storage,
                network = network,
                thermalStatus = thermalStatus,
                sensorsCount = sensors.size,
                onNavigate = { currentScreen = it },
                modifier = contentModifier
              )
            }
            WafaScreen.CHARGELAB -> {
              ChargeLabScreen(
                battery = batteryInfo,
                sessions = sessions,
                onToggleMonitoringService = { enabled ->
                  if (enabled) {
                    BatteryMonitorService.startService(this@MainActivity)
                  } else {
                    BatteryMonitorService.stopService(this@MainActivity)
                  }
                },
                modifier = contentModifier
              )
            }
            WafaScreen.HARDWARE_LAB -> {
              HardwareLabScreen(
                engine = app.hardwareTestLabEngine,
                modifier = contentModifier
              )
            }
            WafaScreen.DIAGNOSTICS -> {
              DiagnosticsScreen(
                hardware = hardware,
                displayInfo = displayInfo,
                cameraResults = cameraResults,
                featureResults = featureResults,
                modifier = contentModifier
              )
            }
            WafaScreen.SENSORS -> {
              SensorsScreen(
                sensors = sensors,
                liveReadings = liveSensorReadings,
                modifier = contentModifier
              )
            }
            WafaScreen.NETWORK -> {
              NetworkScreen(
                network = network,
                engine = app.networkEngine,
                modifier = contentModifier
              )
            }
            WafaScreen.THERMAL_PERF -> {
              ThermalPerformanceScreen(
                thermalStatus = thermalStatus,
                batteryTempC = batteryInfo.temperatureC,
                performanceEngine = app.performanceEngine,
                benchmarkHistory = benchmarks,
                modifier = contentModifier
              )
            }
            WafaScreen.STORAGE -> {
              StorageScreen(
                storage = storage,
                modifier = contentModifier
              )
            }
            WafaScreen.FULL_CHECK -> {
              FullCheckScreen(
                hardwareEngine = app.hardwareEngine,
                batteryEngine = app.batteryEngine,
                networkEngine = app.networkEngine,
                sensorEngine = app.sensorEngine,
                thermalEngine = app.thermalEngine,
                database = app.database,
                modifier = contentModifier
              )
            }
            WafaScreen.HISTORY_REPORTS -> {
              HistoryReportsScreen(
                historyList = historyList,
                hardware = hardware,
                battery = batteryInfo,
                storage = storage,
                network = network,
                cameraResults = cameraResults,
                featureResults = featureResults,
                reportEngine = app.reportEngine,
                modifier = contentModifier
              )
            }
            WafaScreen.SETTINGS -> {
              SettingsScreen(
                currentTheme = currentTheme,
                onThemeSelected = { mode ->
                  scope.launch {
                    app.themePreferences.setThemeMode(mode)
                  }
                },
                database = app.database,
                modifier = contentModifier
              )
            }
          }
        }
      }
    }
  }
}
