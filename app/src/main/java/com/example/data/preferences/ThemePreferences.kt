package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemePreset(
    val id: String,
    val displayName: String,
    val description: String,
    val previewPrimary: Long,
    val previewBackground: Long,
    val isDark: Boolean = true
) {
    AMOLED_VOID(
        id = "amoled_void",
        displayName = "AMOLED Pitch Black",
        description = "Pure black for OLED displays & high contrast",
        previewPrimary = 0xFF00E5FF,
        previewBackground = 0xFF000000,
        isDark = true
    ),
    MIDNIGHT_SLATE(
        id = "midnight_slate",
        displayName = "Midnight Slate",
        description = "Modern dark developer aesthetic with teal highlights",
        previewPrimary = 0xFF2DD4BF,
        previewBackground = 0xFF0D1117,
        isDark = true
    ),
    CYBERPUNK_NEON(
        id = "cyberpunk_neon",
        displayName = "Cyberpunk Neon",
        description = "High-octane neon magenta and electric cyan",
        previewPrimary = 0xFFFF2E93,
        previewBackground = 0xFF0A0915,
        isDark = true
    ),
    EMERALD_MATRIX(
        id = "emerald_matrix",
        displayName = "Emerald Matrix",
        description = "Deep forest night with vivid mint green accents",
        previewPrimary = 0xFF10B981,
        previewBackground = 0xFF08140E,
        isDark = true
    ),
    COSMIC_AMETHYST(
        id = "cosmic_amethyst",
        displayName = "Cosmic Amethyst",
        description = "Deep space violet with glowing purple accents",
        previewPrimary = 0xFFA855F7,
        previewBackground = 0xFF0F0C1B,
        isDark = true
    ),
    SOLARIZED_DARK(
        id = "solarized_dark",
        displayName = "Solarized Dark",
        description = "Harmonious deep teal with warm amber operators",
        previewPrimary = 0xFFD33682,
        previewBackground = 0xFF002B36,
        isDark = true
    ),
    NORDIC_FROST(
        id = "nordic_frost",
        displayName = "Nordic Frost",
        description = "Minimalist slate graphite with cool arctic blue",
        previewPrimary = 0xFF38BDF8,
        previewBackground = 0xFF18181B,
        isDark = true
    ),
    SOLAR_LIGHT(
        id = "solar_light",
        displayName = "Daylight Clean",
        description = "Crisp, airy high-contrast theme for bright sunlight",
        previewPrimary = 0xFF2563EB,
        previewBackground = 0xFFF8FAFC,
        isDark = false
    )
}

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("apex_calc_theme_prefs", Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(getInitialTheme())
    val currentTheme: StateFlow<AppThemePreset> = _currentTheme.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTICS, true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private fun getInitialTheme(): AppThemePreset {
        val savedId = prefs.getString(KEY_THEME_ID, AppThemePreset.AMOLED_VOID.id)
        return AppThemePreset.entries.firstOrNull { it.id == savedId } ?: AppThemePreset.AMOLED_VOID
    }

    fun setTheme(theme: AppThemePreset) {
        prefs.edit().putString(KEY_THEME_ID, theme.id).apply()
        _currentTheme.value = theme
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
        _hapticsEnabled.value = enabled
    }

    companion object {
        private const val KEY_THEME_ID = "selected_theme_id"
        private const val KEY_HAPTICS = "haptics_enabled"
    }
}
