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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiagnosticResult
import com.example.data.model.HardwareDiagnostics
import com.example.ui.components.WafaDiagnosticResultCard
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors

@Composable
fun DiagnosticsScreen(
  hardware: HardwareDiagnostics,
  displayInfo: Map<String, String>,
  cameraResults: List<DiagnosticResult>,
  featureResults: List<DiagnosticResult>,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("diagnostics_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "HARDWARE & SYSTEM CAPABILITY",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Detailed inspection of processor, display, cameras, and system features",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Processor & OS Details Card
    item {
      WafaSectionHeader(title = "Processor & Core Architecture")

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
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          DetailRow("SoC Board", hardware.board)
          DetailRow("Hardware Chipset", hardware.hardware)
          DetailRow("CPU Cores Count", "${hardware.cpuCores} Cores Available")
          DetailRow("Supported ABIs", hardware.supportedAbis.joinToString())
          DetailRow("Total System RAM", "%.2f GB".format(hardware.totalRamBytes / 1_073_741_824.0))
          DetailRow("Security Patch", hardware.securityPatch)
        }
      }
    }

    // 2. Display Metrics Card
    item {
      WafaSectionHeader(title = "Display & Screen Capabilities")

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
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          displayInfo.forEach { (key, value) ->
            DetailRow(key, value)
          }
        }
      }
    }

    // 3. Camera Characteristics Inventory
    item {
      WafaSectionHeader(title = "Camera Hardware Inventory (${cameraResults.size})")
    }

    items(cameraResults) { cam ->
      WafaDiagnosticResultCard(result = cam)
    }

    // 4. System Hardware Features Matrix
    item {
      WafaSectionHeader(title = "System Hardware Feature Matrix (${featureResults.size})")
    }

    items(featureResults) { feat ->
      WafaDiagnosticResultCard(result = feat)
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun DetailRow(title: String, value: String) {
  val colors = LocalWafaColors.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium,
      color = colors.textMuted
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
