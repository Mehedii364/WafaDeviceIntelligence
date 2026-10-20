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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StorageDiagnostics
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaMetricCard
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors

@Composable
fun StorageScreen(
  storage: StorageDiagnostics,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current

  val totalGb = storage.internalTotalBytes / 1_073_741_824.0
  val availGb = storage.internalAvailableBytes / 1_073_741_824.0
  val usedGb = totalGb - availGb
  val usedPct = if (totalGb > 0) ((usedGb / totalGb) * 100).toInt() else 0

  val appCacheMb = storage.appCacheBytes / 1_048_576.0
  val appFilesMb = storage.appFilesBytes / 1_048_576.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("storage_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "STORAGE INTELLIGENCE",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "StatFs partition metrics and app directory storage analysis",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Primary Storage Card with Progress Bar
    item {
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
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Internal User Data Volume",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )

          LinearProgressIndicator(
            progress = { (usedPct / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(12.dp)
              .clip(RoundedCornerShape(6.dp)),
            color = colors.accentGold,
            trackColor = colors.elevatedSurface
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Used: %.1f GB ($usedPct%)".format(usedGb),
              style = MaterialTheme.typography.bodyMedium,
              color = colors.accentGold
            )
            Text(
              text = "Free: %.1f GB".format(availGb),
              style = MaterialTheme.typography.bodyMedium,
              color = colors.statusSuccess
            )
          }

          Text(
            text = "Total Partition Capacity: %.2f GB".format(totalGb),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted
          )
        }
      }
    }

    // 2. Application Isolated Storage Breakdown
    item {
      WafaSectionHeader(title = "App-Isolated Storage Usage")

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        WafaMetricCard(
          title = "App Cache Directory",
          value = "%.2f MB".format(appCacheMb),
          subvalue = "Internal & External Cache",
          icon = WafaIcons.Storage,
          accentColor = colors.accentBlue,
          modifier = Modifier.weight(1f)
        )

        WafaMetricCard(
          title = "App Data Files",
          value = "%.2f MB".format(appFilesMb),
          subvalue = "Internal App Database & Files",
          icon = WafaIcons.Storage,
          accentColor = colors.accentGold,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Storage Health & Wear Transparency
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
            text = "PHYSICAL FLASH WEAR HONESTY NOTE",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp
            ),
            color = colors.accentGold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Android security architecture strictly restricts raw eMMC / UFS flash memory controller wear logs (such as bad blocks or remaining write endurance) from third-party applications.\n" +
              "Wafa Device Intelligence reports real, verified partition volumes and available byte counts via StatFs rather than fabricating wear indicators.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
