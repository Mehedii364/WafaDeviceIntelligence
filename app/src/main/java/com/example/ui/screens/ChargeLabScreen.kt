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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.local.entity.ChargingSessionEntity
import com.example.data.model.BatteryChargingState
import com.example.data.model.BatteryInfo
import com.example.ui.components.WafaBatteryGauge
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaMetricCard
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChargeLabScreen(
  battery: BatteryInfo,
  sessions: List<ChargingSessionEntity>,
  onToggleMonitoringService: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current
  var isServiceEnabled by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("chargelab_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Module Header & Subtitle
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "WAFA CHARGELAB",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Battery & Charging Behavior Analytics Subsystem",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 2. Circular Battery Gauge
    item {
      WafaBatteryGauge(batteryInfo = battery)
    }

    // 3. Live Metrics Grid (Temperature, Voltage, Current, Rate)
    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        WafaMetricCard(
          title = "Temperature",
          value = battery.temperatureC?.let { "%.1f°C".format(it) } ?: "Unavailable",
          subvalue = "Battery Thermistor",
          icon = WafaIcons.Thermal,
          accentColor = colors.accentGold,
          modifier = Modifier.weight(1f)
        )
        WafaMetricCard(
          title = "Voltage",
          value = battery.voltageV?.let { "%.2f V".format(it) } ?: "Unavailable",
          subvalue = "Live Terminal Voltage",
          icon = WafaIcons.BatteryIntelligence,
          accentColor = colors.accentBlue,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        val currentStr = if (battery.currentNowMa != null) {
          "${battery.currentNowMa} mA"
        } else {
          "Unsupported by OS"
        }

        WafaMetricCard(
          title = "Electric Current",
          value = currentStr,
          subvalue = "BATTERY_PROPERTY_CURRENT",
          icon = WafaIcons.ChargeLab,
          accentColor = colors.statusSuccess,
          modifier = Modifier.weight(1f)
        )

        val rateStr = if (battery.chargeRatePerHour != null) {
          "+%.1f%% / hr".format(battery.chargeRatePerHour)
        } else {
          if (battery.state == BatteryChargingState.CHARGING) "Calculating..." else "Discharging"
        }

        val etaStr = if (battery.estimatedMinutesToFull != null) {
          "~${battery.estimatedMinutesToFull} min (± 5m)"
        } else {
          "N/A"
        }

        WafaMetricCard(
          title = "Charge Rate & ETA",
          value = rateStr,
          subvalue = "Full ETA: $etaStr",
          icon = WafaIcons.Performance,
          accentColor = colors.statusWarning,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 4. Background Monitoring Foreground Service Toggle
    item {
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
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Continuous Charging Monitor",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Runs an explicit foreground service notification while charging",
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
              color = colors.textMuted
            )
          }
          Switch(
            checked = isServiceEnabled,
            onCheckedChange = {
              isServiceEnabled = it
              onToggleMonitoringService(it)
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = colors.accentGold,
              checkedTrackColor = colors.accentGold.copy(alpha = 0.4f)
            )
          )
        }
      }
    }

    // 5. Battery Integrity & Health Honesty Notice
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.elevatedSurface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(
            listOf(colors.accentGold.copy(alpha = 0.4f), Color.Transparent)
          )
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "HARDWARE INTEGRITY PRINCIPLE",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp
            ),
            color = colors.accentGold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Health Status: ${battery.healthString} (Reported by BatteryManager)\n" +
              "Android does not provide unprivileged apps access to physical battery cycle counts, original mAh capacity, or chemical degradation percentages. Wafa ChargeLab displays real observed measurements only and never fabricates health statistics.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 6. Charging Session History List
    item {
      WafaSectionHeader(title = "Recorded Charging Sessions (${sessions.size})")
    }

    if (sessions.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = WafaIcons.ChargeLab,
              contentDescription = null,
              tint = colors.textMuted,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No Charging Sessions Recorded Yet",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Connect your device to a charger to automatically start logging charging duration, temperature variations, and rates.",
              style = MaterialTheme.typography.bodyMedium,
              color = colors.textMuted,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    } else {
      items(sessions) { session ->
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
        val startTimeStr = dateFormat.format(Date(session.startTime))
        val durationMins = (session.durationMs / 60000L).coerceAtLeast(1L)
        val gainPct = (session.currentPercentage - session.initialPercentage).coerceAtLeast(0)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("session_item_${session.id}"),
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
                text = "$startTimeStr • ${session.pluggedSource}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${session.initialPercentage}% → ${session.currentPercentage}% (+$gainPct%) in ${durationMins}m",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.accentBlue
              )
              Text(
                text = "Temp range: ${session.minTemperatureC?.let { "%.1f".format(it) } ?: "-"}°C to ${session.maxTemperatureC?.let { "%.1f".format(it) } ?: "-"}°C",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (session.isCompleted) colors.statusSuccess.copy(alpha = 0.15f) else colors.accentGold.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (session.isCompleted) "COMPLETED" else "ACTIVE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (session.isCompleted) colors.statusSuccess else colors.accentGold
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
