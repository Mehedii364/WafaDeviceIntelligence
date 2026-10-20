package com.example.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.ui.components.WafaIcons
import com.example.ui.theme.LocalWafaColors

enum class WafaScreen(val route: String, val title: String, val icon: ImageVector) {
  DASHBOARD("dashboard", "Overview", WafaIcons.DeviceIntelligence),
  CHARGELAB("chargelab", "ChargeLab", WafaIcons.ChargeLab),
  HARDWARE_LAB("hardware_lab", "Lab Tests", WafaIcons.DisplayTouch),
  DIAGNOSTICS("diagnostics", "Diagnostics", WafaIcons.Cpu),
  SENSORS("sensors", "Sensors", WafaIcons.Sensors),
  NETWORK("network", "Network", WafaIcons.Network),
  THERMAL_PERF("thermal_perf", "Thermal/Perf", WafaIcons.Performance),
  STORAGE("storage", "Storage", WafaIcons.Storage),
  FULL_CHECK("full_check", "Full Check", WafaIcons.RunCheck),
  HISTORY_REPORTS("history_reports", "Reports", WafaIcons.Reports),
  SETTINGS("settings", "Settings", WafaIcons.Settings)
}

val BottomNavScreens = listOf(
  WafaScreen.DASHBOARD,
  WafaScreen.CHARGELAB,
  WafaScreen.HARDWARE_LAB,
  WafaScreen.DIAGNOSTICS,
  WafaScreen.HISTORY_REPORTS
)

@Composable
fun WafaBottomNav(
  currentScreen: WafaScreen,
  onScreenSelected: (WafaScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.testTag("wafa_bottom_navigation"),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = androidx.compose.ui.unit.Dp(8f)
  ) {
    BottomNavScreens.forEach { screen ->
      val selected = currentScreen == screen
      NavigationBarItem(
        modifier = Modifier.testTag("nav_item_${screen.route}"),
        selected = selected,
        onClick = { onScreenSelected(screen) },
        icon = {
          Icon(
            imageVector = screen.icon,
            contentDescription = screen.title
          )
        },
        label = {
          Text(
            text = screen.title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = LocalWafaColors.current.accentGold,
          selectedTextColor = LocalWafaColors.current.accentGold,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
          indicatorColor = LocalWafaColors.current.elevatedSurface
        )
      )
    }
  }
}
