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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.components.WafaPrimaryButton
import com.example.ui.components.WafaSecondaryButton
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors
import com.example.ui.theme.WafaThemeMode
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
  currentTheme: WafaThemeMode,
  onThemeSelected: (WafaThemeMode) -> Unit,
  database: WafaDatabase,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current
  val scope = rememberCoroutineScope()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("settings_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "PREFERENCES & BRANDING",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Theme personalization, hardware privacy, and developer attribution",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Theme Selection
    item {
      WafaSectionHeader(title = "Diagnostic Theme Palettes")

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
          WafaThemeMode.values().forEach { mode ->
            val isSelected = currentTheme == mode
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onThemeSelected(mode) }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { onThemeSelected(mode) },
                colors = RadioButtonDefaults.colors(selectedColor = colors.accentGold)
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = mode.displayName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = if (isSelected) colors.accentGold else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = mode.description,
                  style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                  color = colors.textMuted
                )
              }
            }
          }
        }
      }
    }

    // 2. Developer & Brand Attribution
    item {
      WafaSectionHeader(title = "Developer & Brand Attribution")

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(
            listOf(colors.accentGold, colors.cardBorder)
          )
        )
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Wafa Device Intelligence",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.accentGold
          )
          Text(
            text = "Integrated Module: Wafa ChargeLab",
            style = MaterialTheme.typography.titleMedium,
            color = colors.accentBlue
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Developer: Mehedi364 (Md. Mehedi Hasan)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Brand: Wafa Zone",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Package: site.wafazone.deviceintelligence",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "\"Real device data. Honest diagnostics. No fabricated results.\"",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            ),
            color = colors.accentGold
          )
        }
      }
    }

    // 3. Database & History Cleanup
    item {
      WafaSectionHeader(title = "Local Diagnostic Storage")

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
          Text(
            text = "Reset Local Database",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Clears all stored charging sessions, benchmark results, and diagnostic logs from Room.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = colors.textMuted
          )
          WafaSecondaryButton(
            text = "Clear Database History",
            onClick = {
              scope.launch {
                database.chargingSessionDao().deleteAll()
                database.diagnosticHistoryDao().deleteAll()
                database.benchmarkHistoryDao().deleteAll()
              }
            }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
