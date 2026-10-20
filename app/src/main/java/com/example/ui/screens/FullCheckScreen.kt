package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WafaDatabase
import com.example.data.local.entity.DiagnosticHistoryEntity
import com.example.data.model.DiagnosticResult
import com.example.data.model.DiagnosticStatus
import com.example.engine.BatteryEngine
import com.example.engine.HardwareEngine
import com.example.engine.NetworkEngine
import com.example.engine.SensorEngine
import com.example.engine.ThermalEngine
import com.example.ui.components.WafaDiagnosticResultCard
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaPrimaryButton
import com.example.ui.components.WafaSecondaryButton
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FullCheckScreen(
  hardwareEngine: HardwareEngine,
  batteryEngine: BatteryEngine,
  networkEngine: NetworkEngine,
  sensorEngine: SensorEngine,
  thermalEngine: ThermalEngine,
  database: WafaDatabase,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current
  val scope = rememberCoroutineScope()

  var isRunning by remember { mutableStateOf(false) }
  var progress by remember { mutableFloatStateOf(0f) }
  var currentStepName by remember { mutableStateOf("Ready to initiate check") }
  val results = remember { mutableStateListOf<DiagnosticResult>() }
  var runnerJob by remember { mutableStateOf<Job?>(null) }

  val passedCount = results.count { it.status == DiagnosticStatus.PASSED || it.status == DiagnosticStatus.AVAILABLE }
  val warningCount = results.count { it.status == DiagnosticStatus.WARNING || it.status == DiagnosticStatus.LIMITED }
  val failedCount = results.count { it.status == DiagnosticStatus.FAILED || it.status == DiagnosticStatus.UNSUPPORTED }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("full_check_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "ONE-TAP FULL DEVICE CHECK",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Automated non-destructive scan of all supported device subsystems",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Control & Progress Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(
            listOf(colors.accentGold, colors.cardBorder)
          )
        )
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (isRunning) "Diagnostic in Progress" else if (results.isNotEmpty()) "Scan Complete" else "Ready",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = currentStepName,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = colors.accentBlue
              )
            }
            Text(
              text = "${(progress * 100).toInt()}%",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = colors.accentGold
            )
          }

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp)),
            color = colors.accentGold,
            trackColor = colors.elevatedSurface
          )

          if (!isRunning) {
            WafaPrimaryButton(
              text = if (results.isEmpty()) "Start Full Diagnostics" else "Run Diagnostics Again",
              icon = WafaIcons.RunCheck,
              onClick = {
                runnerJob = scope.launch {
                  isRunning = true
                  results.clear()
                  progress = 0.05f

                  // Step 1: OS & Build Check
                  currentStepName = "Inspecting Android OS & Security Patch..."
                  delay(150)
                  val hw = hardwareEngine.getHardwareDiagnostics()
                  results.add(
                    DiagnosticResult(
                      id = "chk_os",
                      name = "Operating System Integrity",
                      category = "System",
                      status = if (hw.sdkInt >= 26) DiagnosticStatus.PASSED else DiagnosticStatus.WARNING,
                      measurementSource = "Build.VERSION",
                      summary = "Android ${hw.androidRelease} (API ${hw.sdkInt}) • Patch: ${hw.securityPatch}"
                    )
                  )
                  progress = 0.15f

                  // Step 2: CPU & Core Architecture
                  currentStepName = "Evaluating CPU Multi-Core Architecture..."
                  delay(150)
                  results.add(
                    DiagnosticResult(
                      id = "chk_cpu",
                      name = "CPU Cores & Architecture",
                      category = "Processor",
                      status = if (hw.cpuCores >= 4) DiagnosticStatus.PASSED else DiagnosticStatus.LIMITED,
                      measurementSource = "Runtime.availableProcessors",
                      summary = "${hw.cpuCores} CPU Cores active (${hw.supportedAbis.firstOrNull() ?: "Unknown ABI"})"
                    )
                  )
                  progress = 0.30f

                  // Step 3: RAM Memory Check
                  currentStepName = "Checking RAM Capacity & Threshold..."
                  delay(150)
                  val availRamGb = hw.availableRamBytes / 1_073_741_824.0
                  results.add(
                    DiagnosticResult(
                      id = "chk_ram",
                      name = "System RAM Capacity",
                      category = "Memory",
                      status = if (!hw.isLowRamDevice && availRamGb > 0.5) DiagnosticStatus.PASSED else DiagnosticStatus.WARNING,
                      measurementSource = "ActivityManager.MemoryInfo",
                      summary = "Available: %.2f GB (Low RAM flag: ${hw.isLowRamDevice})".format(availRamGb)
                    )
                  )
                  progress = 0.45f

                  // Step 4: Storage Free Space
                  currentStepName = "Checking Internal Storage Volumes..."
                  delay(150)
                  val st = hardwareEngine.getStorageDiagnostics()
                  val freeGb = st.internalAvailableBytes / 1_073_741_824.0
                  results.add(
                    DiagnosticResult(
                      id = "chk_storage",
                      name = "Internal Storage Volume",
                      category = "Storage",
                      status = if (freeGb > 1.0) DiagnosticStatus.PASSED else DiagnosticStatus.WARNING,
                      measurementSource = "StatFs",
                      summary = "%.2f GB free out of %.2f GB total partition".format(freeGb, st.internalTotalBytes / 1_073_741_824.0)
                    )
                  )
                  progress = 0.60f

                  // Step 5: Battery & Thermal
                  currentStepName = "Reading Battery Thermistor & Charging Subsystem..."
                  delay(150)
                  val bat = batteryEngine.batteryInfo.value
                  val therm = thermalEngine.getCurrentStatus()
                  results.add(
                    DiagnosticResult(
                      id = "chk_battery",
                      name = "Battery & Thermal State",
                      category = "Power",
                      status = if ((bat.temperatureC ?: 25f) < 45f) DiagnosticStatus.PASSED else DiagnosticStatus.WARNING,
                      measurementSource = "BatteryManager & PowerManager",
                      summary = "${bat.percentage}% (${bat.state.label}) • Temp: ${bat.temperatureC?.let { "%.1f°C".format(it) } ?: "N/A"} • Thermal: $therm"
                    )
                  )
                  progress = 0.75f

                  // Step 6: Network Connectivity & DNS
                  currentStepName = "Testing Network Connectivity & DNS Lookup..."
                  val net = networkEngine.getNetworkState()
                  val dnsTime = networkEngine.runDnsLatencyTest("dns.google")
                  results.add(
                    DiagnosticResult(
                      id = "chk_network",
                      name = "Network & DNS Resolution",
                      category = "Connectivity",
                      status = if (net.isConnected && dnsTime != null) DiagnosticStatus.PASSED else DiagnosticStatus.WARNING,
                      measurementSource = "ConnectivityManager & InetAddress",
                      summary = "${net.transport} • Online: ${net.isConnected} • DNS Time: ${dnsTime?.let { "${it}ms" } ?: "Timeout/Offline"}"
                    )
                  )
                  progress = 0.90f

                  // Step 7: Sensor Inventory
                  currentStepName = "Enumerating Hardware Sensor List..."
                  delay(150)
                  val allSensors = sensorEngine.getAllSensors()
                  results.add(
                    DiagnosticResult(
                      id = "chk_sensors",
                      name = "Hardware Sensors Roster",
                      category = "Sensors",
                      status = if (allSensors.size >= 3) DiagnosticStatus.PASSED else DiagnosticStatus.LIMITED,
                      measurementSource = "SensorManager",
                      summary = "${allSensors.size} physical and virtual sensors recognized"
                    )
                  )
                  progress = 1.0f
                  currentStepName = "Scan Complete"
                  isRunning = false

                  // Save session to Room database
                  database.diagnosticHistoryDao().insertHistory(
                    DiagnosticHistoryEntity(
                      sessionTimestamp = System.currentTimeMillis(),
                      overallStatus = if (failedCount == 0) "PASSED" else "WITH_WARNINGS",
                      totalTests = results.size,
                      passedCount = passedCount,
                      warningCount = warningCount,
                      failedCount = failedCount,
                      limitedCount = results.count { it.status == DiagnosticStatus.LIMITED },
                      unsupportedCount = results.count { it.status == DiagnosticStatus.UNSUPPORTED },
                      summaryDetailsJson = "One-Tap Full Check: ${results.size} tests finished"
                    )
                  )
                }
              }
            )
          } else {
            WafaSecondaryButton(
              text = "Cancel Test",
              onClick = {
                runnerJob?.cancel()
                isRunning = false
                currentStepName = "Scan cancelled by user"
              }
            )
          }
        }
      }
    }

    // 2. Scorecard Summary if completed
    if (results.isNotEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = colors.elevatedSurface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "PASSED", style = MaterialTheme.typography.labelSmall, color = colors.statusSuccess)
              Text(text = "$passedCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = colors.statusSuccess)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "WARNING", style = MaterialTheme.typography.labelSmall, color = colors.statusWarning)
              Text(text = "$warningCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = colors.statusWarning)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "UNSUPPORTED", style = MaterialTheme.typography.labelSmall, color = colors.statusError)
              Text(text = "$failedCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = colors.statusError)
            }
          }
        }
      }

      item {
        WafaSectionHeader(title = "Diagnostic Results Breakdown")
      }

      items(results) { item ->
        WafaDiagnosticResultCard(result = item)
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
