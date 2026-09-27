package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CalculatorThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val displayBackground: Color,
    val displayText: Color,
    val displaySecondaryText: Color,
    val numberKeyBg: Color,
    val numberKeyText: Color,
    val operatorKeyBg: Color,
    val operatorKeyText: Color,
    val functionKeyBg: Color,
    val functionKeyText: Color,
    val equalsKeyBg: Color,
    val equalsKeyText: Color,
    val clearKeyBg: Color,
    val clearKeyText: Color,
    val memoryKeyBg: Color,
    val memoryKeyText: Color,
    val accent: Color,
    val isDark: Boolean = true
)

val LocalCalculatorColors = staticCompositionLocalOf {
    CalculatorThemeColors(
        background = Color(0xFF000000),
        surface = Color(0xFF101010),
        surfaceVariant = Color(0xFF1A1A1A),
        displayBackground = Color(0xFF080808),
        displayText = Color(0xFFFFFFFF),
        displaySecondaryText = Color(0xFF00E5FF),
        numberKeyBg = Color(0xFF181818),
        numberKeyText = Color(0xFFF1F5F9),
        operatorKeyBg = Color(0xFF222B36),
        operatorKeyText = Color(0xFF38BDF8),
        functionKeyBg = Color(0xFF1F1D36),
        functionKeyText = Color(0xFFA78BFA),
        equalsKeyBg = Color(0xFF00E5FF),
        equalsKeyText = Color(0xFF000000),
        clearKeyBg = Color(0xFF3B1824),
        clearKeyText = Color(0xFFFF5252),
        memoryKeyBg = Color(0xFF1C221F),
        memoryKeyText = Color(0xFF34D399),
        accent = Color(0xFF00E5FF),
        isDark = true
    )
}
