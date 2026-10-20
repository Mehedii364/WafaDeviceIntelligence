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

/**
 * Diagnostic laboratory theme modes for Wafa Device Intelligence.
 */
enum class WafaThemeMode(val displayName: String, val description: String) {
  OBSIDIAN_AURORA("Obsidian Aurora", "Adaptive obsidian with champagne gold & electric blue (Dark & Light)"),
  GRAPHITE_PRO("Graphite Pro", "Technical laboratory appearance with neutral silver & cool blue"),
  LIGHT_TITANIUM("Light Titanium", "Soft titanium white with high contrast dark graphite"),
  MIDNIGHT_VIOLET("Midnight Violet", "Deep navy-black with restrained electric violet accents")
}

/**
 * Extended semantic diagnostic tokens not covered by standard Material 3 color schemes.
 */
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

// =========================================================================
// 1. Obsidian Aurora Color Schemes (Material 3)
// =========================================================================

/**
 * Obsidian Aurora Dark Mode: Deep obsidian black base with champagne gold highlights
 * and electric blue active telemetry indicators.
 */
val ObsidianAuroraDarkColorScheme = darkColorScheme(
  primary = ChampagneGold,
  onPrimary = ObsidianBackground,
  primaryContainer = ObsidianElevated,
  onPrimaryContainer = ChampagneGold,
  secondary = ElectricBlue,
  onSecondary = ObsidianBackground,
  secondaryContainer = ObsidianElevated,
  onSecondaryContainer = ElectricBlue,
  tertiary = StatusSuccess,
  onTertiary = ObsidianBackground,
  background = ObsidianBackground,
  onBackground = TextPrimaryDark,
  surface = ObsidianSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = ObsidianElevated,
  onSurfaceVariant = TextSecondaryDark,
  outline = ObsidianCardBorder,
  outlineVariant = ObsidianCardBorder.copy(alpha = 0.5f),
  error = StatusError,
  onError = ObsidianBackground,
  errorContainer = StatusError.copy(alpha = 0.15f),
  onErrorContainer = StatusError
)

/**
 * Obsidian Aurora Light Mode: Clean off-white aurora surface with deep obsidian typography,
 * deep gold accents, and sharp electric blue interactive states.
 */
val ObsidianAuroraLightColorScheme = lightColorScheme(
  primary = AuroraLightGold,
  onPrimary = Color.White,
  primaryContainer = AuroraLightElevated,
  onPrimaryContainer = AuroraLightGoldVariant,
  secondary = AuroraLightBlue,
  onSecondary = Color.White,
  secondaryContainer = AuroraLightElevated,
  onSecondaryContainer = AuroraLightBlue,
  tertiary = AuroraLightSuccess,
  onTertiary = Color.White,
  background = AuroraLightBackground,
  onBackground = AuroraLightTextPrimary,
  surface = AuroraLightSurface,
  onSurface = AuroraLightTextPrimary,
  surfaceVariant = AuroraLightElevated,
  onSurfaceVariant = AuroraLightTextSecondary,
  outline = AuroraLightCardBorder,
  outlineVariant = AuroraLightCardBorder.copy(alpha = 0.5f),
  error = AuroraLightError,
  onError = Color.White,
  errorContainer = AuroraLightError.copy(alpha = 0.12f),
  onErrorContainer = AuroraLightError
)

// =========================================================================
// 2. Graphite Pro Theme (Dark Laboratory)
// =========================================================================
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

// =========================================================================
// 3. Light Titanium Theme (Light Technical)
// =========================================================================
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

// =========================================================================
// 4. Midnight Violet Theme (Deep Navy/Violet Dark)
// =========================================================================
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

/**
 * Main application theme wrapper supporting the Obsidian Aurora design system
 * along with dark and light mode adaptation.
 *
 * @param themeMode The selected diagnostic theme palette.
 * @param darkTheme Whether to render in dark mode (defaults to system setting).
 * @param content Composable application content.
 */
@Composable
fun MyApplicationTheme(
  themeMode: WafaThemeMode = WafaThemeMode.OBSIDIAN_AURORA,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme: ColorScheme
  val extendedColors: WafaExtendedColors
  val isDarkSurface: Boolean

  when (themeMode) {
    WafaThemeMode.OBSIDIAN_AURORA -> {
      if (darkTheme) {
        colorScheme = ObsidianAuroraDarkColorScheme
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
        isDarkSurface = true
      } else {
        colorScheme = ObsidianAuroraLightColorScheme
        extendedColors = WafaExtendedColors(
          elevatedSurface = AuroraLightElevated,
          cardBorder = AuroraLightCardBorder,
          accentGold = AuroraLightGold,
          accentBlue = AuroraLightBlue,
          statusSuccess = AuroraLightSuccess,
          statusWarning = AuroraLightWarning,
          statusError = AuroraLightError,
          textMuted = AuroraLightTextMuted
        )
        isDarkSurface = false
      }
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
      isDarkSurface = true
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
      isDarkSurface = false
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
      isDarkSurface = true
    }
  }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = !isDarkSurface
        controller.isAppearanceLightNavigationBars = !isDarkSurface
      }
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
