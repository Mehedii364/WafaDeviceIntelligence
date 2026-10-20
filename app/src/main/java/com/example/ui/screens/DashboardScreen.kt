package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatteryInfo
import com.example.data.model.HardwareDiagnostics
import com.example.data.model.NetworkDiagnostics
import com.example.data.model.StorageDiagnostics
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaMetricCard
import com.example.ui.components.WafaPrimaryButton
import com.example.ui.components.WafaSectionHeader
import com.example.ui.navigation.WafaScreen
import com.example.ui.theme.LocalWafaColors

@Composable
fun DashboardScreen(
  hardware: HardwareDiagnostics,
  battery: BatteryInfo,
  storage: StorageDiagnostics,
  network: NetworkDiagnostics,
  thermalStatus: String,
  sensorsCount: Int,
  onNavigate: (WafaScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Device Identity Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .testTag("device_identity_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(
            listOf(colors.accentGold, colors.cardBorder)
          )
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${hardware.manufacturer} ${hardware.model}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Android ${hardware.androidRelease} (API ${hardware.sdkInt}) • ${hardware.board}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted
              )
            }
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.accentGold.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = WafaIcons.Cpu,
                contentDescription = null,
                tint = colors.accentGold,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Hero Action: Run One-Tap Full Device Check
          WafaPrimaryButton(
            text = "Run One-Tap Full Device Check",
            icon = WafaIcons.RunCheck,
            onClick = { onNavigate(WafaScreen.FULL_CHECK) }
          )
        }
      }
    }

    // 2. Technical Command Center Overview Grid
    item {
      WafaSectionHeader(title = "Live System Telemetry")

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // Battery Metric
        WafaMetricCard(
          title = "Battery",
          value = "${battery.percentage}%",
          subvalue = "${battery.state.label} • ${battery.temperatureC?.let { "%.1f°C".format(it) } ?: "N/A"}",
          icon = WafaIcons.ChargeLab,
          accentColor = colors.accentBlue,
          onClick = { onNavigate(WafaScreen.CHARGELAB) },
          modifier = Modifier.weight(1f)
        )

        // Thermal Metric
        WafaMetricCard(
          title = "Thermal",
          value = thermalStatus.substringBefore(" /"),
          subvalue = "OS PowerManager Status",
          icon = WafaIcons.Thermal,
          accentColor = colors.accentGold,
          onClick = { onNavigate(WafaScreen.THERMAL_PERF) },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // RAM Metric
        val totalRamGb = hardware.totalRamBytes / 1_073_741_824.0
        val availRamGb = hardware.availableRamBytes / 1_073_741_824.0
        val usedRamGb = totalRamGb - availRamGb
        val ramPct = if (totalRamGb > 0) ((usedRamGb / totalRamGb) * 100).toInt() else 0

        WafaMetricCard(
          title = "RAM Memory",
          value = "%.1f / %.1f GB".format(availRamGb, totalRamGb),
          subvalue = "$ramPct% used (${if (hardware.isLowRamDevice) "Low RAM" else "Normal"})",
          icon = WafaIcons.Ram,
          accentColor = colors.statusSuccess,
          onClick = { onNavigate(WafaScreen.DIAGNOSTICS) },
          modifier = Modifier.weight(1f)
        )

        // Storage Metric
        val totalStorageGb = storage.internalTotalBytes / 1_073_741_824.0
        val availStorageGb = storage.internalAvailableBytes / 1_073_741_824.0
        val storagePct = if (totalStorageGb > 0) (((totalStorageGb - availStorageGb) / totalStorageGb) * 100).toInt() else 0

        WafaMetricCard(
          title = "Internal Storage",
          value = "%.1f GB Free".format(availStorageGb),
          subvalue = "$storagePct% used of %.1f GB".format(totalStorageGb),
          icon = WafaIcons.Storage,
          accentColor = colors.statusWarning,
          onClick = { onNavigate(WafaScreen.STORAGE) },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // Network Metric
        WafaMetricCard(
          title = "Connectivity",
          value = network.transport,
          subvalue = if (network.isConnected) "Online (Validated: ${network.isValidated})" else "Disconnected",
          icon = WafaIcons.Network,
          accentColor = colors.accentBlue,
          onClick = { onNavigate(WafaScreen.NETWORK) },
          modifier = Modifier.weight(1f)
        )

        // Sensors Metric
        WafaMetricCard(
          title = "Sensors",
          value = "$sensorsCount Discovered",
          subvalue = "Active SensorManager List",
          icon = WafaIcons.Sensors,
          accentColor = colors.accentGold,
          onClick = { onNavigate(WafaScreen.SENSORS) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Specialized Diagnostic Labs Quick Access
    item {
      WafaSectionHeader(title = "Specialized Diagnostic Labs")

      val labItems = listOf(
        Triple("Hardware Test Lab", "Interactive screen, audio, mic, vibration & torch", WafaScreen.HARDWARE_LAB),
        Triple("Sensor Calibration Lab", "Real-time accelerometer, gyroscope & compass", WafaScreen.SENSORS),
        Triple("Network Quality Lab", "DNS latency, HTTP ping & jitter measurements", WafaScreen.NETWORK),
        Triple("Thermal & Performance", "Bounded CPU sieve and memory throughput tests", WafaScreen.THERMAL_PERF),
        Triple("Storage Monitor", "Partition statistics and app cache breakdown", WafaScreen.STORAGE)
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labItems.forEach { (title, subtitle, screen) ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigate(screen) }
              .testTag("lab_item_${screen.route}"),
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
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = title,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = subtitle,
                  style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                  color = colors.textMuted
                )
              }
              Icon(
                imageVector = screen.icon,
                contentDescription = title,
                tint = colors.accentGold,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
