package com.unicalculator.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors

@Composable
fun NeumorphicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    textColor: Color? = null,
    fontSize: Int = 24,
    isAccent: Boolean = false,
    accentColor: Color? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val colors = LocalNeumorphicColors.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "button_scale"
    )

    val currentShape = if (isPressed) NeumorphicShape.CONCAVE else NeumorphicShape.CONVEX
    val activeNeonColor = textColor ?: if (isAccent) (accentColor ?: colors.accentEmerald) else colors.accentEmerald

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neumorphic(
                shape = currentShape,
                cornerRadius = cornerRadius,
                elevation = if (isPressed) 4.dp else 6.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.background,
                neonGlowColor = if (isPressed) activeNeonColor else null
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize.sp,
                color = textColor ?: if (isAccent) (accentColor ?: colors.accentEmerald) else colors.textPrimary
            )
        )
    }
}
