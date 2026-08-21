package com.unicalculator.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle

/**
 * High-performance Neon Glow Style builder for Jetpack Compose.
 * Leverages Skia GPU text-blur pipeline with zero spatial displacement.
 */
fun TextStyle.withNeonGlow(
    glowColor: Color,
    blurRadius: Float = 14f,
    glowAlpha: Float = 0.65f
): TextStyle {
    return this.copy(
        shadow = Shadow(
            color = glowColor.copy(alpha = glowAlpha),
            offset = Offset.Zero,
            blurRadius = blurRadius
        )
    )
}

/**
 * Theme-aware Neon Style presets for UniCalculator displays.
 */
object NeonGlowStyles {

    @Composable
    @ReadOnlyComposable
    fun emerald(
        isDark: Boolean = LocalNeumorphicColors.current.isDark,
        baseStyle: TextStyle = TextStyle.Default
    ): TextStyle {
        val coreColor = if (isDark) Color(0xFF00FF9D) else Color(0xFF00754A)
        val glowColor = Color(0xFF00C781)
        val alpha = if (isDark) 0.85f else 0.45f
        val blur = if (isDark) 20f else 12f

        return baseStyle.copy(
            color = coreColor,
            shadow = Shadow(
                color = glowColor.copy(alpha = alpha),
                offset = Offset.Zero,
                blurRadius = blur
            )
        )
    }

    @Composable
    @ReadOnlyComposable
    fun saffronAmber(
        isDark: Boolean = LocalNeumorphicColors.current.isDark,
        baseStyle: TextStyle = TextStyle.Default
    ): TextStyle {
        val coreColor = if (isDark) Color(0xFFFF9933) else Color(0xFFD35400)
        val glowColor = Color(0xFFE67E22)
        val alpha = if (isDark) 0.85f else 0.45f
        val blur = if (isDark) 20f else 12f

        return baseStyle.copy(
            color = coreColor,
            shadow = Shadow(
                color = glowColor.copy(alpha = alpha),
                offset = Offset.Zero,
                blurRadius = blur
            )
        )
    }

    @Composable
    @ReadOnlyComposable
    fun cyanSapphire(
        isDark: Boolean = LocalNeumorphicColors.current.isDark,
        baseStyle: TextStyle = TextStyle.Default
    ): TextStyle {
        val coreColor = if (isDark) Color(0xFF00E5FF) else Color(0xFF007A87)
        val glowColor = Color(0xFF0284C7)
        val alpha = if (isDark) 0.80f else 0.45f
        val blur = if (isDark) 18f else 10f

        return baseStyle.copy(
            color = coreColor,
            shadow = Shadow(
                color = glowColor.copy(alpha = alpha),
                offset = Offset.Zero,
                blurRadius = blur
            )
        )
    }
}
