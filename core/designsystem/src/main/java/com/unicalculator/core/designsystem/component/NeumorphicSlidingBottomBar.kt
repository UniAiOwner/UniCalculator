package com.unicalculator.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGlow
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import kotlin.math.roundToInt

data class NeumorphicTabItem(
    val title: String,
    val icon: ImageVector
)

/**
 * High-performance, tactile Neumorphic Sliding Bottom Navigation Bar.
 * Features a dedicated physical sliding active pill with liquid spring physics,
 * continuous finger drag scrubbing, audio-haptic detents, and scale pop arrivals.
 */
@Composable
fun NeumorphicSlidingBottomBar(
    tabs: List<NeumorphicTabItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onFractionalDrag: ((Float) -> Unit)? = null,
    onDragEnd: ((Int) -> Unit)? = null,
    fractionalPosition: Float? = null,
    height: Dp = 64.dp
) {
    val colors = LocalNeumorphicColors.current
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val hapticEngine = remember { NeumorphicHapticEngine(context) }

    val tabCount = tabs.size.coerceAtLeast(1)
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(selectedTab.toFloat()) }
    var lastDetentIndex by remember { mutableIntStateOf(selectedTab) }

    LaunchedEffect(selectedTab) {
        if (!isDragging) {
            dragFraction = selectedTab.toFloat()
            lastDetentIndex = selectedTab
        }
    }

    // Determine current effective visual position (scrubbing drag or external pager or static tab)
    val effectiveFraction = when {
        isDragging -> dragFraction
        fractionalPosition != null -> fractionalPosition.coerceIn(0f, (tabCount - 1).toFloat())
        else -> selectedTab.toFloat()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .neumorphic(
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 24.dp,
                elevation = 6.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.background
            )
            .pointerInput(tabCount) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val tabWidthPx = (size.width / tabCount).toFloat()
                        val fraction = (offset.x / tabWidthPx).coerceIn(0f, (tabCount - 1).toFloat())
                        dragFraction = fraction
                        onFractionalDrag?.invoke(fraction)
                        val rounded = fraction.roundToInt().coerceIn(0, tabCount - 1)
                        if (rounded != lastDetentIndex) {
                            hapticEngine.playSkateDetent()
                            lastDetentIndex = rounded
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        val targetTab = dragFraction.roundToInt().coerceIn(0, tabCount - 1)
                        if (onDragEnd != null) {
                            onDragEnd(targetTab)
                        } else {
                            onTabSelected(targetTab)
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        val targetTab = dragFraction.roundToInt().coerceIn(0, tabCount - 1)
                        if (onDragEnd != null) {
                            onDragEnd(targetTab)
                        } else {
                            onTabSelected(targetTab)
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val tabWidthPx = (size.width / tabCount).toFloat()
                        val deltaFraction = dragAmount / tabWidthPx
                        dragFraction = (dragFraction + deltaFraction).coerceIn(0f, (tabCount - 1).toFloat())
                        onFractionalDrag?.invoke(dragFraction)
                        val rounded = dragFraction.roundToInt().coerceIn(0, tabCount - 1)
                        if (rounded != lastDetentIndex) {
                            hapticEngine.playSkateDetent()
                            lastDetentIndex = rounded
                        }
                    }
                )
            }
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val density = androidx.compose.ui.platform.LocalDensity.current
            val tabWidth = maxWidth / tabCount
            val tabWidthPx = with(density) { tabWidth.toPx() }
            val pillPadding = 2.dp
            val pillPaddingPx = with(density) { pillPadding.toPx() }
            val actualPillWidth = tabWidth - (pillPadding * 2)

            val animatedFraction by animateFloatAsState(
                targetValue = selectedTab.toFloat(),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "PillFractionAnimation"
            )

            val currentFraction = if (isDragging || fractionalPosition != null) {
                effectiveFraction
            } else {
                animatedFraction
            }

            // 1. Physical Sliding Neumorphic Active Pill (GPU Draw-Phase translated)
            Box(
                modifier = Modifier
                    .width(actualPillWidth)
                    .fillMaxHeight()
                    .graphicsLayer {
                        translationX = (currentFraction * tabWidthPx) + pillPaddingPx
                    }
                    .neumorphic(
                        shape = NeumorphicShape.CONCAVE,
                        cornerRadius = 16.dp,
                        elevation = 4.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    )
            ) {
                // Subtle neon ambient glow lining on active pill
                if (colors.isDark) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        RupeeEmeraldGlow.copy(alpha = 0.14f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            // 2. Interactive Tab Items Layer (Evenly distributed over the track)
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val activeEmerald = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen

                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    val interactionSource = remember { MutableInteractionSource() }

                    // Continuous active factor (1.0 = fully active, 0.0 = inactive)
                    val activeFactor = if (isDragging || fractionalPosition != null) {
                        (1f - kotlin.math.abs(effectiveFraction - index)).coerceIn(0f, 1f)
                    } else {
                        if (isSelected) 1f else 0f
                    }

                    // Dynamically interpolate color between inactive textSecondary and activeEmerald
                    val tabColor = if (isDragging || fractionalPosition != null) {
                        androidx.compose.ui.graphics.lerp(colors.textSecondary, activeEmerald, activeFactor)
                    } else {
                        if (isSelected) activeEmerald else colors.textSecondary
                    }

                    val tabScale = 1.0f + (0.08f * activeFactor)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                if (selectedTab != index) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onTabSelected(index)
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onTabSelected(index)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.graphicsLayer {
                                scaleX = tabScale
                                scaleY = tabScale
                            },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = tabColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = tab.title,
                                fontSize = 9.5.sp,
                                fontWeight = if (activeFactor > 0.5f) FontWeight.Bold else FontWeight.Medium,
                                color = tabColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
