package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.WafaThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "wafa_settings")

class ThemePreferences(private val context: Context) {
  companion object {
    private val THEME_KEY = stringPreferencesKey("wafa_theme_mode")
  }

  val themeMode: Flow<WafaThemeMode> = context.dataStore.data.map { preferences ->
    val themeName = preferences[THEME_KEY] ?: WafaThemeMode.OBSIDIAN_AURORA.name
    try {
      WafaThemeMode.valueOf(themeName)
    } catch (_: Exception) {
      WafaThemeMode.OBSIDIAN_AURORA
    }
  }

  suspend fun setThemeMode(mode: WafaThemeMode) {
    context.dataStore.edit { preferences ->
      preferences[THEME_KEY] = mode.name
    }
  }
}
