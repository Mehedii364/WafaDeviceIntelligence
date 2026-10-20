package com.example.ui.screens

import android.hardware.Sensor
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensorItem
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors

@Composable
fun SensorsScreen(
  sensors: List<SensorItem>,
  liveReadings: Map<Int, FloatArray>,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("sensors_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "SENSOR CALIBRATION ASSISTANT",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Live telemetry streaming from hardware SensorManager (${sensors.size} sensors discovered)",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Live Interactive Sensor Telemetry Cards
    item {
      WafaSectionHeader(title = "Live Streaming Sensor Readings")

      val keySensors = listOf(
        Pair(Sensor.TYPE_ACCELEROMETER, "Accelerometer (Motion / Gravity)"),
        Pair(Sensor.TYPE_GYROSCOPE, "Gyroscope (Angular Velocity)"),
        Pair(Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer (Compass Heading)"),
        Pair(Sensor.TYPE_LIGHT, "Ambient Light (Illuminance)"),
        Pair(Sensor.TYPE_PROXIMITY, "Proximity Sensor"),
        Pair(Sensor.TYPE_PRESSURE, "Barometer (Atmospheric Pressure)")
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        keySensors.forEach { (type, title) ->
          val values = liveReadings[type]
          val sensorMeta = sensors.find { it.type == type }

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
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = title,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (sensorMeta != null) "Present" else "Not Detected",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (sensorMeta != null) colors.statusSuccess else colors.statusError
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              if (values != null && values.isNotEmpty()) {
                val valStr = values.mapIndexed { idx, v ->
                  val axis = when (idx) {
                    0 -> "X"
                    1 -> "Y"
                    2 -> "Z"
                    else -> "V$idx"
                  }
                  "$axis: %.3f".format(v)
                }.joinToString("   ")

                Text(
                  text = "$valStr  ${sensorMeta?.unit ?: ""}",
                  style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  ),
                  color = colors.accentBlue
                )
              } else {
                Text(
                  text = if (sensorMeta != null) "Awaiting sensor event..." else "Sensor hardware not present on device",
                  style = MaterialTheme.typography.bodyMedium,
                  color = colors.textMuted
                )
              }
            }
          }
        }
      }
    }

    // 2. All Discovered Sensors Inventory
    item {
      WafaSectionHeader(title = "All Discovered Hardware Sensors (${sensors.size})")
    }

    items(sensors) { item ->
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
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${item.typeName} • Vendor: ${item.vendor} (v${item.version})",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = colors.textMuted
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Max Range: %.1f %s".format(item.maximumRange, item.unit),
              style = MaterialTheme.typography.labelSmall,
              color = colors.accentGold
            )
            Text(
              text = "Power: %.2f mA".format(item.powerMa),
              style = MaterialTheme.typography.labelSmall,
              color = colors.accentBlue
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
