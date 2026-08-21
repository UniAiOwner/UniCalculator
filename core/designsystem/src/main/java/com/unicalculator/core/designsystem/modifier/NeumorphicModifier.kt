package com.unicalculator.core.designsystem.modifier

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class NeumorphicShape {
    CONVEX,     // Raised / 3D extruded button
    CONCAVE,    // Inset / Recessed well (pressed button, LCD display well)
    FLAT        // Subtle plate
}

fun Modifier.neumorphic(
    shape: NeumorphicShape = NeumorphicShape.CONVEX,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
    lightShadowColor: Color = Color.White.copy(alpha = 0.8f),
    darkShadowColor: Color = Color(0xFFC7C2B6).copy(alpha = 0.9f),
    backgroundColor: Color? = null,
    neonGlowColor: Color? = null
): Modifier = this.drawBehind {
    val cornerRadiusPx = cornerRadius.toPx()
    val elevationPx = elevation.toPx()

    when (shape) {
        NeumorphicShape.CONVEX -> {
            // Top-Left Light Highlight Shadow
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = lightShadowColor
                    asFrameworkPaint().maskFilter = BlurMaskFilter(elevationPx, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawRoundRect(
                    left = -elevationPx * 0.8f,
                    top = -elevationPx * 0.8f,
                    right = size.width - elevationPx * 0.8f,
                    bottom = size.height - elevationPx * 0.8f,
                    radiusX = cornerRadiusPx,
                    radiusY = cornerRadiusPx,
                    paint = paint
                )
            }
            // Bottom-Right Dark Cast Shadow
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = darkShadowColor
                    asFrameworkPaint().maskFilter = BlurMaskFilter(elevationPx, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawRoundRect(
                    left = elevationPx * 0.8f,
                    top = elevationPx * 0.8f,
                    right = size.width + elevationPx * 0.8f,
                    bottom = size.height + elevationPx * 0.8f,
                    radiusX = cornerRadiusPx,
                    radiusY = cornerRadiusPx,
                    paint = paint
                )
            }
            // Base surface
            if (backgroundColor != null) {
                drawRoundRect(
                    color = backgroundColor,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }
        }
        NeumorphicShape.CONCAVE -> {
            val width = size.width
            val height = size.height

            // 1. Cavity Floor with Directional Light Falloff Gradient
            if (backgroundColor != null) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            darkShadowColor.copy(alpha = 0.20f),
                            backgroundColor,
                            backgroundColor,
                            lightShadowColor.copy(alpha = 0.12f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height)
                    ),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }

            // 2. Precision Directional Inner Inset Shadows via Skia Path Difference
            drawIntoCanvas { canvas ->
                canvas.save()

                val innerRect = RoundRect(0f, 0f, width, height, CornerRadius(cornerRadiusPx))
                val clipPath = Path().apply { addRoundRect(innerRect) }
                canvas.clipPath(clipPath)

                val margin = elevationPx * 3f

                // --- PASS 1: Top-Left Dark Inset Shadow (Directional Cutout) ---
                val darkShadowOffset = elevationPx * 0.75f
                val outerPathDark = Path().apply {
                    addRect(Rect(-margin, -margin, width + margin, height + margin))
                }
                val innerHoleDark = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = darkShadowOffset,
                            top = darkShadowOffset,
                            right = width + darkShadowOffset,
                            bottom = height + darkShadowOffset,
                            cornerRadius = CornerRadius(cornerRadiusPx)
                        )
                    )
                }
                val darkDifference = Path().apply {
                    op(outerPathDark, innerHoleDark, PathOperation.Difference)
                }

                val innerDarkPaint = Paint().apply {
                    color = darkShadowColor.copy(alpha = 0.95f)
                    asFrameworkPaint().maskFilter = BlurMaskFilter(
                        elevationPx * 0.85f,
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
                canvas.drawPath(darkDifference, innerDarkPaint)

                // --- PASS 2: Bottom-Right Light Highlight Inset Rim ---
                val lightHighlightOffset = elevationPx * 0.75f
                val outerPathLight = Path().apply {
                    addRect(Rect(-margin, -margin, width + margin, height + margin))
                }
                val innerHoleLight = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = -lightHighlightOffset,
                            top = -lightHighlightOffset,
                            right = width - lightHighlightOffset,
                            bottom = height - lightHighlightOffset,
                            cornerRadius = CornerRadius(cornerRadiusPx)
                        )
                    )
                }
                val lightDifference = Path().apply {
                    op(outerPathLight, innerHoleLight, PathOperation.Difference)
                }

                val innerLightPaint = Paint().apply {
                    color = lightShadowColor.copy(alpha = 0.95f)
                    asFrameworkPaint().maskFilter = BlurMaskFilter(
                        elevationPx * 0.75f,
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
                canvas.drawPath(lightDifference, innerLightPaint)

                canvas.restore()
            }

            // 3. Micro Edge Crease (1.2dp Crisp Inner Lip Bevel)
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        darkShadowColor.copy(alpha = 0.65f),
                        darkShadowColor.copy(alpha = 0.25f),
                        lightShadowColor.copy(alpha = 0.40f),
                        lightShadowColor.copy(alpha = 0.90f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(width, height)
                ),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
        NeumorphicShape.FLAT -> {
            if (backgroundColor != null) {
                drawRoundRect(
                    color = backgroundColor,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }
        }
    }

    // Option 2: Glowing Perimeter Neon Ring on Active/Pressed State
    if (neonGlowColor != null) {
        drawIntoCanvas { canvas ->
            // Pass 1: Soft Outer Halo Glow
            val glowPaint = Paint().apply {
                color = neonGlowColor.copy(alpha = 0.35f)
                style = PaintingStyle.Stroke
                strokeWidth = 3.5.dp.toPx()
                asFrameworkPaint().maskFilter = BlurMaskFilter(2.5.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawRoundRect(
                left = 1f,
                top = 1f,
                right = size.width - 1f,
                bottom = size.height - 1f,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = glowPaint
            )

            // Pass 2: Crisp Core Laser Rim
            val corePaint = Paint().apply {
                color = neonGlowColor.copy(alpha = 0.95f)
                style = PaintingStyle.Stroke
                strokeWidth = 1.6.dp.toPx()
            }
            canvas.drawRoundRect(
                left = 0.8f,
                top = 0.8f,
                right = size.width - 0.8f,
                bottom = size.height - 0.8f,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = corePaint
            )
        }
    }
}

