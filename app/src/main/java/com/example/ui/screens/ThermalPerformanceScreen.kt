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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BenchmarkHistoryEntity
import com.example.data.model.BenchmarkResult
import com.example.engine.PerformanceEngine
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaMetricCard
import com.example.ui.components.WafaPrimaryButton
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThermalPerformanceScreen(
  thermalStatus: String,
  batteryTempC: Float?,
  performanceEngine: PerformanceEngine,
  benchmarkHistory: List<BenchmarkHistoryEntity>,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current
  val scope = rememberCoroutineScope()

  var isRunningCpu by remember { mutableStateOf(false) }
  var latestCpuResult by remember { mutableStateOf<BenchmarkResult?>(null) }

  var isRunningMem by remember { mutableStateOf(false) }
  var latestMemResult by remember { mutableStateOf<BenchmarkResult?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("thermal_perf_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "THERMAL & PERFORMANCE LAB",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Bounded, repeatable compute benchmarks correlated with Android thermal state",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Live Thermal Status Card
    item {
      WafaSectionHeader(title = "Real-Time Thermal State")

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        WafaMetricCard(
          title = "Thermal Status",
          value = thermalStatus,
          subvalue = "PowerManager Thermal Throttling",
          icon = WafaIcons.Thermal,
          accentColor = colors.accentGold,
          modifier = Modifier.weight(1f)
        )

        WafaMetricCard(
          title = "Battery Thermistor",
          value = batteryTempC?.let { "%.1f°C".format(it) } ?: "Unavailable",
          subvalue = "Hardware Sensor",
          icon = WafaIcons.BatteryIntelligence,
          accentColor = colors.accentBlue,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 2. CPU Prime Sieve Benchmark
    item {
      WafaSectionHeader(title = "CPU Micro-Benchmark")

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(
            listOf(colors.cardBorder, Color.Transparent)
          )
        )
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "CPU Prime Sieve Math Benchmark",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Runs 10 iterations of prime sieving across 120,000 integers to stress CPU cores and check for thermal throttling.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = colors.textMuted
          )

          if (latestCpuResult != null) {
            val res = latestCpuResult!!
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = colors.elevatedSurface)
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "CPU Score", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                  Text(text = "${res.score} pts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.accentGold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "Execution Duration", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                  Text(text = "${res.durationMs} ms", style = MaterialTheme.typography.labelSmall, color = colors.accentBlue)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "Thermal State Shift", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                  Text(text = "${res.initialThermalStatus} → ${res.finalThermalStatus}", style = MaterialTheme.typography.labelSmall, color = colors.statusSuccess)
                }
              }
            }
          }

          WafaPrimaryButton(
            text = if (isRunningCpu) "Running CPU Benchmark..." else "Execute CPU Benchmark",
            icon = WafaIcons.Cpu,
            enabled = !isRunningCpu,
            onClick = {
              scope.launch {
                isRunningCpu = true
                latestCpuResult = performanceEngine.runCpuBenchmark()
                isRunningCpu = false
              }
            }
          )
        }
      }
    }

    // 3. Memory Throughput Benchmark
    item {
      WafaSectionHeader(title = "RAM Memory Bandwidth Benchmark")

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(
            listOf(colors.cardBorder, Color.Transparent)
          )
        )
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Memory Buffer Manipulation & Bandwidth",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Allocates and transforms 2.5 million element Float buffers to measure sequential memory throughput.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = colors.textMuted
          )

          if (latestMemResult != null) {
            val res = latestMemResult!!
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = colors.elevatedSurface)
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "Memory Score", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                  Text(text = "${res.score} pts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.accentBlue)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "Execution Duration", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                  Text(text = "${res.durationMs} ms", style = MaterialTheme.typography.labelSmall, color = colors.accentGold)
                }
              }
            }
          }

          WafaPrimaryButton(
            text = if (isRunningMem) "Running Memory Benchmark..." else "Execute Memory Benchmark",
            icon = WafaIcons.Ram,
            enabled = !isRunningMem,
            onClick = {
              scope.launch {
                isRunningMem = true
                latestMemResult = performanceEngine.runMemoryBenchmark()
                isRunningMem = false
              }
            }
          )
        }
      }
    }

    // 4. Past Benchmark History
    item {
      WafaSectionHeader(title = "Benchmark History (${benchmarkHistory.size})")
    }

    if (benchmarkHistory.isEmpty()) {
      item {
        Text(
          text = "No benchmarks recorded yet. Run a test above to record repeatable results.",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    } else {
      items(benchmarkHistory) { item ->
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.US)
        val timeStr = dateFormat.format(Date(item.timestamp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
              listOf(colors.cardBorder, Color.Transparent)
            )
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = item.testType,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "$timeStr • Duration: ${item.durationMs}ms",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = colors.textMuted
              )
            }
            Text(
              text = "${item.score} pts",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = colors.accentGold
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
