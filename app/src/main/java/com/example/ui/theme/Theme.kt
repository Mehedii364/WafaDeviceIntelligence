package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class WafaThemeMode(val displayName: String, val description: String) {
  OBSIDIAN_AURORA("Obsidian Aurora", "Deep obsidian with champagne gold & electric blue (Default)"),
  GRAPHITE_PRO("Graphite Pro", "Technical laboratory appearance with neutral silver & cool blue"),
  LIGHT_TITANIUM("Light Titanium", "Soft titanium white with high contrast dark graphite"),
  MIDNIGHT_VIOLET("Midnight Violet", "Deep navy-black with restrained electric violet accents")
}

@Immutable
data class WafaExtendedColors(
  val elevatedSurface: Color,
  val cardBorder: Color,
  val accentGold: Color,
  val accentBlue: Color,
  val statusSuccess: Color,
  val statusWarning: Color,
  val statusError: Color,
  val textMuted: Color
)

val LocalWafaColors = staticCompositionLocalOf {
  WafaExtendedColors(
    elevatedSurface = ObsidianElevated,
    cardBorder = ObsidianCardBorder,
    accentGold = ChampagneGold,
    accentBlue = ElectricBlue,
    statusSuccess = StatusSuccess,
    statusWarning = StatusWarning,
    statusError = StatusError,
    textMuted = TextMutedDark
  )
}

// 1. Obsidian Aurora Color Schemes
val ObsidianAuroraColorScheme = darkColorScheme(
  primary = ChampagneGold,
  onPrimary = ObsidianBackground,
  primaryContainer = ObsidianElevated,
  onPrimaryContainer = ChampagneGold,
  secondary = ElectricBlue,
  onSecondary = ObsidianBackground,
  secondaryContainer = ObsidianElevated,
  onSecondaryContainer = ElectricBlue,
  tertiary = StatusSuccess,
  background = ObsidianBackground,
  onBackground = TextPrimaryDark,
  surface = ObsidianSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = ObsidianElevated,
  onSurfaceVariant = TextSecondaryDark,
  outline = ObsidianCardBorder,
  error = StatusError,
  onError = ObsidianBackground
)

// 2. Graphite Pro Color Scheme
val GraphiteProColorScheme = darkColorScheme(
  primary = CoolBlue,
  onPrimary = GraphiteBackground,
  primaryContainer = GraphiteElevated,
  onPrimaryContainer = GraphiteSilver,
  secondary = GraphiteSilver,
  onSecondary = GraphiteBackground,
  secondaryContainer = GraphiteElevated,
  onSecondaryContainer = CoolBlue,
  tertiary = StatusSuccess,
  background = GraphiteBackground,
  onBackground = TextPrimaryDark,
  surface = GraphiteSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = GraphiteElevated,
  onSurfaceVariant = TextSecondaryDark,
  outline = GraphiteCardBorder,
  error = StatusError,
  onError = GraphiteBackground
)

// 3. Light Titanium Color Scheme
val LightTitaniumColorScheme = lightColorScheme(
  primary = ChampagneGoldVariant,
  onPrimary = Color.White,
  primaryContainer = TitaniumElevated,
  onPrimaryContainer = TitaniumTextPrimary,
  secondary = ElectricBlueVariant,
  onSecondary = Color.White,
  secondaryContainer = TitaniumElevated,
  onSecondaryContainer = TitaniumTextPrimary,
  tertiary = StatusSuccess,
  background = TitaniumBackground,
  onBackground = TitaniumTextPrimary,
  surface = TitaniumSurface,
  onSurface = TitaniumTextPrimary,
  surfaceVariant = TitaniumElevated,
  onSurfaceVariant = TitaniumTextSecondary,
  outline = TitaniumCardBorder,
  error = StatusError,
  onError = Color.White
)

// 4. Midnight Violet Color Scheme
val MidnightVioletColorScheme = darkColorScheme(
  primary = MidnightViolet,
  onPrimary = MidnightBackground,
  primaryContainer = MidnightElevated,
  onPrimaryContainer = MidnightViolet,
  secondary = ElectricBlue,
  onSecondary = MidnightBackground,
  secondaryContainer = MidnightElevated,
  onSecondaryContainer = ElectricBlue,
  tertiary = StatusSuccess,
  background = MidnightBackground,
  onBackground = TextPrimaryDark,
  surface = MidnightSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = MidnightElevated,
  onSurfaceVariant = TextSecondaryDark,
  outline = MidnightCardBorder,
  error = StatusError,
  onError = MidnightBackground
)

@Composable
fun MyApplicationTheme(
  themeMode: WafaThemeMode = WafaThemeMode.OBSIDIAN_AURORA,
  content: @Composable () -> Unit
) {
  val colorScheme: ColorScheme
  val extendedColors: WafaExtendedColors

  when (themeMode) {
    WafaThemeMode.OBSIDIAN_AURORA -> {
      colorScheme = ObsidianAuroraColorScheme
      extendedColors = WafaExtendedColors(
        elevatedSurface = ObsidianElevated,
        cardBorder = ObsidianCardBorder,
        accentGold = ChampagneGold,
        accentBlue = ElectricBlue,
        statusSuccess = StatusSuccess,
        statusWarning = StatusWarning,
        statusError = StatusError,
        textMuted = TextMutedDark
      )
    }
    WafaThemeMode.GRAPHITE_PRO -> {
      colorScheme = GraphiteProColorScheme
      extendedColors = WafaExtendedColors(
        elevatedSurface = GraphiteElevated,
        cardBorder = GraphiteCardBorder,
        accentGold = GraphiteSilver,
        accentBlue = CoolBlue,
        statusSuccess = StatusSuccess,
        statusWarning = StatusWarning,
        statusError = StatusError,
        textMuted = TextMutedDark
      )
    }
    WafaThemeMode.LIGHT_TITANIUM -> {
      colorScheme = LightTitaniumColorScheme
      extendedColors = WafaExtendedColors(
        elevatedSurface = TitaniumElevated,
        cardBorder = TitaniumCardBorder,
        accentGold = ChampagneGoldVariant,
        accentBlue = ElectricBlueVariant,
        statusSuccess = StatusSuccess,
        statusWarning = StatusWarning,
        statusError = StatusError,
        textMuted = TitaniumTextSecondary
      )
    }
    WafaThemeMode.MIDNIGHT_VIOLET -> {
      colorScheme = MidnightVioletColorScheme
      extendedColors = WafaExtendedColors(
        elevatedSurface = MidnightElevated,
        cardBorder = MidnightCardBorder,
        accentGold = MidnightViolet,
        accentBlue = ElectricBlue,
        statusSuccess = StatusSuccess,
        statusWarning = StatusWarning,
        statusError = StatusError,
        textMuted = TextMutedDark
      )
    }
  }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.background.toArgb()
      window.navigationBarColor = colorScheme.background.toArgb()
      val isLight = themeMode == WafaThemeMode.LIGHT_TITANIUM
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLight
      WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isLight
    }
  }

  CompositionLocalProvider(LocalWafaColors provides extendedColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
