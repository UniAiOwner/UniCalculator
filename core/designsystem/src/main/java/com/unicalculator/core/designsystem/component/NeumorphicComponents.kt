package com.unicalculator.core.designsystem.component

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

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
fun NeumorphicPlate(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 4.dp,
    shape: NeumorphicShape = NeumorphicShape.CONVEX,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalNeumorphicColors.current

    Box(
        modifier = modifier
            .neumorphic(
                shape = shape,
                cornerRadius = cornerRadius,
                elevation = elevation,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.background
            )
            .padding(14.dp),
        content = content
    )
}

@Composable
fun NeumorphicGstPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    fontSize: Int = 13,
    horizontalPadding: Dp = 4.dp
) {
    val colors = LocalNeumorphicColors.current
    val activeColor = accentColor ?: colors.accentEmerald
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else if (isSelected) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "pillScale"
    )

    val currentShape = if (isSelected) NeumorphicShape.CONCAVE else NeumorphicShape.CONVEX

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neumorphic(
                shape = currentShape,
                cornerRadius = 14.dp,
                elevation = if (isSelected) 4.dp else 5.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.background,
                neonGlowColor = if (isSelected || isPressed) activeColor else null
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = horizontalPadding, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        val displayText = text
        Text(
            text = displayText,
            maxLines = 1,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = if (isSelected) (fontSize - 0.5).sp else fontSize.sp,
                color = if (isSelected) activeColor else colors.textPrimary
            )
        )
    }
}

class NeumorphicHapticEngine(private val context: Context) {
    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    fun playKeyClick(intensity: com.unicalculator.core.common.prefs.HapticIntensity = com.unicalculator.core.common.prefs.HapticIntensity.MEDIUM) {
        if (intensity == com.unicalculator.core.common.prefs.HapticIntensity.OFF) return
        try {
            val (durationMs, amplitude) = when (intensity) {
                com.unicalculator.core.common.prefs.HapticIntensity.OFF -> return
                com.unicalculator.core.common.prefs.HapticIntensity.SOFT -> 18L to 130
                com.unicalculator.core.common.prefs.HapticIntensity.MEDIUM -> 30L to 200
                com.unicalculator.core.common.prefs.HapticIntensity.STRONG -> 48L to 255
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(durationMs, amplitude)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val attrs = android.os.VibrationAttributes.Builder()
                        .setUsage(android.os.VibrationAttributes.USAGE_TOUCH)
                        .build()
                    vibrator?.vibrate(effect, attrs)
                } else {
                    @Suppress("DEPRECATION")
                    val attrs = android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(effect, attrs)
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun playOperatorTick(intensity: com.unicalculator.core.common.prefs.HapticIntensity = com.unicalculator.core.common.prefs.HapticIntensity.MEDIUM) {
        if (intensity == com.unicalculator.core.common.prefs.HapticIntensity.OFF) return
        try {
            val (durationMs, amplitude) = when (intensity) {
                com.unicalculator.core.common.prefs.HapticIntensity.OFF -> return
                com.unicalculator.core.common.prefs.HapticIntensity.SOFT -> 22L to 150
                com.unicalculator.core.common.prefs.HapticIntensity.MEDIUM -> 38L to 220
                com.unicalculator.core.common.prefs.HapticIntensity.STRONG -> 60L to 255
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(durationMs, amplitude)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val attrs = android.os.VibrationAttributes.Builder()
                        .setUsage(android.os.VibrationAttributes.USAGE_TOUCH)
                        .build()
                    vibrator?.vibrate(effect, attrs)
                } else {
                    @Suppress("DEPRECATION")
                    val attrs = android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(effect, attrs)
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private val audioManager: android.media.AudioManager? = try {
        context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
    } catch (_: Exception) {
        null
    }

    fun playSkateDetent(
        intensity: com.unicalculator.core.common.prefs.HapticIntensity = com.unicalculator.core.common.prefs.HapticIntensity.MEDIUM,
        playSound: Boolean = true
    ) {
        if (intensity != com.unicalculator.core.common.prefs.HapticIntensity.OFF) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val attrs = android.os.VibrationAttributes.Builder()
                            .setUsage(android.os.VibrationAttributes.USAGE_TOUCH)
                            .build()
                        vibrator?.vibrate(effect, attrs)
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(effect)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(14L, 160)
                    vibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(14L)
                }
            } catch (_: Exception) {}
        }

        if (playSound) {
            try {
                audioManager?.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD, 0.5f)
            } catch (_: Exception) {}
        }
    }
}

@Composable
fun NeumorphicSlideSwitch(
    leftLabel: String,
    rightLabel: String,
    isRightSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color? = null,
    leftActiveColor: Color? = null,
    rightActiveColor: Color? = null,
    height: Dp = 38.dp
) {
    val colors = LocalNeumorphicColors.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val hapticEngine = remember { NeumorphicHapticEngine(context) }
    val currentActiveColor = if (isRightSelected) (rightActiveColor ?: activeColor ?: colors.accentEmerald)
        else (leftActiveColor ?: activeColor ?: colors.accentEmerald)

    val slideProgress by animateFloatAsState(
        targetValue = if (isRightSelected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f),
        label = "slide_switch_progress"
    )

    // Outer Recessed Trench ("Concave Well")
    Box(
        modifier = modifier
            .height(height)
            .neumorphic(
                shape = NeumorphicShape.CONCAVE,
                cornerRadius = 12.dp,
                elevation = 3.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.background
            )
            .padding(3.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val thumbWidth = maxWidth / 2

            // Floating 3D Solid Active Thumb
            Box(
                modifier = Modifier
                    .offset(x = thumbWidth * slideProgress)
                    .width(thumbWidth)
                    .fillMaxHeight()
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 9.dp,
                        elevation = 2.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = currentActiveColor
                    )
            )

            // Clickable Label Overlay Row
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (isRightSelected) {
                                hapticEngine.playOperatorTick()
                                onToggle(false)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = leftLabel,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = if (!isRightSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp,
                            color = if (!isRightSelected) Color.White else colors.textSecondary
                        ),
                        maxLines = 1
                    )
                }

                // Right Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isRightSelected) {
                                hapticEngine.playOperatorTick()
                                onToggle(true)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rightLabel,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = if (isRightSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp,
                            color = if (isRightSelected) Color.White else colors.textSecondary
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun NeumorphicIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    cornerRadius: Dp = 12.dp,
    iconTint: Color? = null,
    iconSize: Dp = 20.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val colors = LocalNeumorphicColors.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "icon_btn_scale"
    )

    val currentShape = if (isPressed) NeumorphicShape.CONCAVE else NeumorphicShape.CONVEX

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neumorphic(
                shape = currentShape,
                cornerRadius = cornerRadius,
                elevation = if (isPressed) 2.dp else 4.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.background,
                neonGlowColor = if (isPressed) (iconTint ?: colors.accentEmerald) else null
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = iconTint ?: colors.textPrimary
        )
    }
}



