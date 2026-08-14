package com.unicalculator.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class NeumorphicThemeColors(
    val background: Color,
    val lightHighlight: Color,
    val darkShadow: Color,
    val lcdWellBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentEmerald: Color,
    val accentAmber: Color,
    val isDark: Boolean
)

val LocalNeumorphicColors = staticCompositionLocalOf {
    NeumorphicThemeColors(
        background = NeumorphicBackgroundLight,
        lightHighlight = NeumorphicLightHighlightLight,
        darkShadow = NeumorphicDarkShadowLight,
        lcdWellBackground = NeumorphicLCDWellBgLight,
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        accentEmerald = RupeeEmeraldGreen,
        accentAmber = GstSaffronAmber,
        isDark = false
    )
}

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 48.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)

@Composable
fun UniCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val neumorphicColors = if (darkTheme) {
        NeumorphicThemeColors(
            background = NeumorphicBackgroundDark,
            lightHighlight = NeumorphicLightHighlightDark,
            darkShadow = NeumorphicDarkShadowDark,
            lcdWellBackground = NeumorphicLCDWellBgDark,
            textPrimary = TextPrimaryDark,
            textSecondary = TextSecondaryDark,
            accentEmerald = RupeeEmeraldGlow,
            accentAmber = GstAmberGlow,
            isDark = true
        )
    } else {
        NeumorphicThemeColors(
            background = NeumorphicBackgroundLight,
            lightHighlight = NeumorphicLightHighlightLight,
            darkShadow = NeumorphicDarkShadowLight,
            lcdWellBackground = NeumorphicLCDWellBgLight,
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            accentEmerald = RupeeEmeraldGreen,
            accentAmber = GstSaffronAmber,
            isDark = false
        )
    }

    CompositionLocalProvider(LocalNeumorphicColors provides neumorphicColors) {
        MaterialTheme(
            typography = AppTypography,
            content = content
        )
    }
}
