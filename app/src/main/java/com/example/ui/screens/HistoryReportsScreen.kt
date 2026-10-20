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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DiagnosticHistoryEntity
import com.example.data.model.BatteryInfo
import com.example.data.model.DiagnosticResult
import com.example.data.model.HardwareDiagnostics
import com.example.data.model.NetworkDiagnostics
import com.example.data.model.StorageDiagnostics
import com.example.engine.ReportEngine
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaPrimaryButton
import com.example.ui.components.WafaSecondaryButton
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryReportsScreen(
  historyList: List<DiagnosticHistoryEntity>,
  hardware: HardwareDiagnostics,
  battery: BatteryInfo,
  storage: StorageDiagnostics,
  network: NetworkDiagnostics,
  cameraResults: List<DiagnosticResult>,
  featureResults: List<DiagnosticResult>,
  reportEngine: ReportEngine,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current
  val scope = rememberCoroutineScope()

  val allDiagnostics = cameraResults + featureResults

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("history_reports_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "SMART DIAGNOSTIC REPORTS",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Unified timeline and technical report generation in JSON, CSV and Text formats",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Export Action Cards
    item {
      WafaSectionHeader(title = "Export Diagnostic Documents")

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
            text = "Generate & Share Technical Reports",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Share verified technical diagnostics locally via Android's native share sheet without third-party servers.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = colors.textMuted
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            WafaPrimaryButton(
              text = "Export JSON",
              icon = WafaIcons.Share,
              onClick = {
                val json = reportEngine.generateJsonReport(
                  hardware, battery, storage, network, allDiagnostics
                )
                reportEngine.shareReport(json, "wafa_report.json", "application/json")
              },
              modifier = Modifier.weight(1f)
            )

            WafaSecondaryButton(
              text = "Export CSV",
              icon = WafaIcons.Reports,
              onClick = {
                val csv = reportEngine.generateCsvReport(allDiagnostics)
                reportEngine.shareReport(csv, "wafa_report.csv", "text/csv")
              },
              modifier = Modifier.weight(1f)
            )
          }

          WafaSecondaryButton(
            text = "Share Printable Text Summary",
            icon = WafaIcons.DeviceInfo,
            onClick = {
              val txt = reportEngine.generateTextSummary(hardware, battery, allDiagnostics)
              reportEngine.shareReport(txt, "wafa_summary.txt", "text/plain")
            }
          )
        }
      }
    }

    // 2. Unified Timeline & Diagnostic Run History
    item {
      WafaSectionHeader(title = "Diagnostic Session Timeline (${historyList.size})")
    }

    if (historyList.isEmpty()) {
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
            Text(
              text = "No Previous Sessions Logged",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Run the One-Tap Full Device Check to automatically record timestamped diagnostic runs.",
              style = MaterialTheme.typography.bodyMedium,
              color = colors.textMuted
            )
          }
        }
      }
    } else {
      items(historyList) { item ->
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.US)
        val timeStr = dateFormat.format(Date(item.sessionTimestamp))

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
                text = "Full Device Check • ${item.overallStatus}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = timeStr,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted
              )
              Text(
                text = "${item.passedCount} Passed • ${item.warningCount} Warnings • ${item.failedCount} Unsupported",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.accentBlue
              )
            }
            Text(
              text = "${item.totalTests} Tests",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
