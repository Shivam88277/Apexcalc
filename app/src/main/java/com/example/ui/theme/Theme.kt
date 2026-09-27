package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.data.preferences.AppThemePreset

fun getThemePalette(preset: AppThemePreset): CalculatorThemeColors {
    return when (preset) {
        AppThemePreset.AMOLED_VOID -> CalculatorThemeColors(
            background = Color(0xFF000000),
            surface = Color(0xFF0A0A0A),
            surfaceVariant = Color(0xFF141414),
            displayBackground = Color(0xFF050505),
            displayText = Color(0xFFFFFFFF),
            displaySecondaryText = Color(0xFF00E5FF),
            numberKeyBg = Color(0xFF161616),
            numberKeyText = Color(0xFFFFFFFF),
            operatorKeyBg = Color(0xFF1E2836),
            operatorKeyText = Color(0xFF38BDF8),
            functionKeyBg = Color(0xFF181528),
            functionKeyText = Color(0xFFA78BFA),
            equalsKeyBg = Color(0xFF00E5FF),
            equalsKeyText = Color(0xFF000000),
            clearKeyBg = Color(0xFF33141E),
            clearKeyText = Color(0xFFFF5252),
            memoryKeyBg = Color(0xFF13231D),
            memoryKeyText = Color(0xFF34D399),
            accent = Color(0xFF00E5FF),
            isDark = true
        )
        AppThemePreset.MIDNIGHT_SLATE -> CalculatorThemeColors(
            background = Color(0xFF0D1117),
            surface = Color(0xFF161B22),
            surfaceVariant = Color(0xFF21262D),
            displayBackground = Color(0xFF090D12),
            displayText = Color(0xFFE6EDF3),
            displaySecondaryText = Color(0xFF2DD4BF),
            numberKeyBg = Color(0xFF21262D),
            numberKeyText = Color(0xFFF0F6FC),
            operatorKeyBg = Color(0xFF1F2E3D),
            operatorKeyText = Color(0xFF58A6FF),
            functionKeyBg = Color(0xFF1D2230),
            functionKeyText = Color(0xFF2DD4BF),
            equalsKeyBg = Color(0xFF2DD4BF),
            equalsKeyText = Color(0xFF0D1117),
            clearKeyBg = Color(0xFF361822),
            clearKeyText = Color(0xFFF87171),
            memoryKeyBg = Color(0xFF152A24),
            memoryKeyText = Color(0xFF3FB950),
            accent = Color(0xFF2DD4BF),
            isDark = true
        )
        AppThemePreset.CYBERPUNK_NEON -> CalculatorThemeColors(
            background = Color(0xFF0A0915),
            surface = Color(0xFF141226),
            surfaceVariant = Color(0xFF1F1C38),
            displayBackground = Color(0xFF07060F),
            displayText = Color(0xFFFDFEFE),
            displaySecondaryText = Color(0xFF00F0FF),
            numberKeyBg = Color(0xFF1A182F),
            numberKeyText = Color(0xFFFFFFFF),
            operatorKeyBg = Color(0xFF281335),
            operatorKeyText = Color(0xFFFF2E93),
            functionKeyBg = Color(0xFF10283B),
            functionKeyText = Color(0xFF00F0FF),
            equalsKeyBg = Color(0xFFFF2E93),
            equalsKeyText = Color(0xFF0A0915),
            clearKeyBg = Color(0xFF3F1325),
            clearKeyText = Color(0xFFFF4081),
            memoryKeyBg = Color(0xFF1F2212),
            memoryKeyText = Color(0xFFFFE600),
            accent = Color(0xFFFF2E93),
            isDark = true
        )
        AppThemePreset.EMERALD_MATRIX -> CalculatorThemeColors(
            background = Color(0xFF06120C),
            surface = Color(0xFF0C2216),
            surfaceVariant = Color(0xFF143021),
            displayBackground = Color(0xFF040C08),
            displayText = Color(0xFFE8F5E9),
            displaySecondaryText = Color(0xFF34D399),
            numberKeyBg = Color(0xFF122C1E),
            numberKeyText = Color(0xFFF0FDF4),
            operatorKeyBg = Color(0xFF183D2A),
            operatorKeyText = Color(0xFF10B981),
            functionKeyBg = Color(0xFF0F3124),
            functionKeyText = Color(0xFF6EE7B7),
            equalsKeyBg = Color(0xFF10B981),
            equalsKeyText = Color(0xFF06120C),
            clearKeyBg = Color(0xFF381A22),
            clearKeyText = Color(0xFFF87171),
            memoryKeyBg = Color(0xFF242F13),
            memoryKeyText = Color(0xFFA3E635),
            accent = Color(0xFF10B981),
            isDark = true
        )
        AppThemePreset.COSMIC_AMETHYST -> CalculatorThemeColors(
            background = Color(0xFF0B0817),
            surface = Color(0xFF15102A),
            surfaceVariant = Color(0xFF211A42),
            displayBackground = Color(0xFF070510),
            displayText = Color(0xFFF3E8FF),
            displaySecondaryText = Color(0xFFC084FC),
            numberKeyBg = Color(0xFF1D173A),
            numberKeyText = Color(0xFFFAF5FF),
            operatorKeyBg = Color(0xFF2C1D4D),
            operatorKeyText = Color(0xFFA855F7),
            functionKeyBg = Color(0xFF25183E),
            functionKeyText = Color(0xFFE879F9),
            equalsKeyBg = Color(0xFFA855F7),
            equalsKeyText = Color(0xFF0B0817),
            clearKeyBg = Color(0xFF3B152B),
            clearKeyText = Color(0xFFFB7185),
            memoryKeyBg = Color(0xFF17253B),
            memoryKeyText = Color(0xFF60A5FA),
            accent = Color(0xFFA855F7),
            isDark = true
        )
        AppThemePreset.SOLARIZED_DARK -> CalculatorThemeColors(
            background = Color(0xFF002B36),
            surface = Color(0xFF073642),
            surfaceVariant = Color(0xFF0D4554),
            displayBackground = Color(0xFF001E26),
            displayText = Color(0xFFFDF6E3),
            displaySecondaryText = Color(0xFF2AA198),
            numberKeyBg = Color(0xFF073642),
            numberKeyText = Color(0xFF93A1A1),
            operatorKeyBg = Color(0xFF0C4656),
            operatorKeyText = Color(0xFF268BD2),
            functionKeyBg = Color(0xFF093E4C),
            functionKeyText = Color(0xFF2AA198),
            equalsKeyBg = Color(0xFFB58900),
            equalsKeyText = Color(0xFF002B36),
            clearKeyBg = Color(0xFF382329),
            clearKeyText = Color(0xFFDC322F),
            memoryKeyBg = Color(0xFF1C3A35),
            memoryKeyText = Color(0xFF859900),
            accent = Color(0xFF268BD2),
            isDark = true
        )
        AppThemePreset.NORDIC_FROST -> CalculatorThemeColors(
            background = Color(0xFF12151B),
            surface = Color(0xFF1A1F29),
            surfaceVariant = Color(0xFF242C3A),
            displayBackground = Color(0xFF0D1016),
            displayText = Color(0xFFF1F5F9),
            displaySecondaryText = Color(0xFF38BDF8),
            numberKeyBg = Color(0xFF222938),
            numberKeyText = Color(0xFFF8FAFC),
            operatorKeyBg = Color(0xFF1E324B),
            operatorKeyText = Color(0xFF38BDF8),
            functionKeyBg = Color(0xFF1E2838),
            functionKeyText = Color(0xFF818CF8),
            equalsKeyBg = Color(0xFF38BDF8),
            equalsKeyText = Color(0xFF0B132B),
            clearKeyBg = Color(0xFF381D26),
            clearKeyText = Color(0xFFFB7185),
            memoryKeyBg = Color(0xFF16322A),
            memoryKeyText = Color(0xFF4ADE80),
            accent = Color(0xFF38BDF8),
            isDark = true
        )
        AppThemePreset.SOLAR_LIGHT -> CalculatorThemeColors(
            background = Color(0xFFF1F5F9),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE2E8F0),
            displayBackground = Color(0xFFF8FAFC),
            displayText = Color(0xFF0F172A),
            displaySecondaryText = Color(0xFF2563EB),
            numberKeyBg = Color(0xFFFFFFFF),
            numberKeyText = Color(0xFF1E293B),
            operatorKeyBg = Color(0xFFDBEAFE),
            operatorKeyText = Color(0xFF1D4ED8),
            functionKeyBg = Color(0xFFEDE9FE),
            functionKeyText = Color(0xFF6D28D9),
            equalsKeyBg = Color(0xFF2563EB),
            equalsKeyText = Color(0xFFFFFFFF),
            clearKeyBg = Color(0xFFFFE4E6),
            clearKeyText = Color(0xFFE11D48),
            memoryKeyBg = Color(0xFFD1FAE5),
            memoryKeyText = Color(0xFF047857),
            accent = Color(0xFF2563EB),
            isDark = false
        )
    }
}

@Composable
fun MyApplicationTheme(
    preset: AppThemePreset = AppThemePreset.AMOLED_VOID,
    content: @Composable () -> Unit
) {
    val calcColors = getThemePalette(preset)

    val colorScheme = if (preset.isDark) {
        darkColorScheme(
            primary = calcColors.accent,
            onPrimary = calcColors.equalsKeyText,
            secondary = calcColors.operatorKeyText,
            background = calcColors.background,
            onBackground = calcColors.displayText,
            surface = calcColors.surface,
            onSurface = calcColors.displayText,
            surfaceVariant = calcColors.surfaceVariant,
            onSurfaceVariant = calcColors.displayText.copy(alpha = 0.8f)
        )
    } else {
        lightColorScheme(
            primary = calcColors.accent,
            onPrimary = calcColors.equalsKeyText,
            secondary = calcColors.operatorKeyText,
            background = calcColors.background,
            onBackground = calcColors.displayText,
            surface = calcColors.surface,
            onSurface = calcColors.displayText,
            surfaceVariant = calcColors.surfaceVariant,
            onSurfaceVariant = calcColors.displayText.copy(alpha = 0.8f)
        )
    }

    CompositionLocalProvider(LocalCalculatorColors provides calcColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
