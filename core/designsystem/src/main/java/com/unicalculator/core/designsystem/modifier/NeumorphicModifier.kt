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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class NeumorphicShape {
    CONVEX,     // Raised / 3D extruded button
    CONCAVE,    // Inset / Recessed well (pressed button, LCD display well)
    FLAT        // Subtle plate
}

private object NeumorphicShaderPool {
    private val filterCache = androidx.collection.LruCache<Int, BlurMaskFilter>(64)

    fun getBlurFilter(radiusPx: Float): BlurMaskFilter {
        val key = (radiusPx * 10f).toInt().coerceAtLeast(1)
        return filterCache.get(key) ?: BlurMaskFilter(radiusPx.coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL).also {
            filterCache.put(key, it)
        }
    }

    val paintA = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val paintB = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val clipPath = android.graphics.Path()
    val outerPath = android.graphics.Path()
    val innerHole = android.graphics.Path()
    val diffPath = android.graphics.Path()
    val rectF = android.graphics.RectF()
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
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                val filter = NeumorphicShaderPool.getBlurFilter(elevationPx)

                // Top-Left Light Highlight Shadow
                val lightPaint = NeumorphicShaderPool.paintA.apply {
                    reset()
                    isAntiAlias = true
                    color = lightShadowColor.toArgb()
                    maskFilter = filter
                }
                NeumorphicShaderPool.rectF.set(
                    -elevationPx * 0.8f,
                    -elevationPx * 0.8f,
                    size.width - elevationPx * 0.8f,
                    size.height - elevationPx * 0.8f
                )
                nativeCanvas.drawRoundRect(NeumorphicShaderPool.rectF, cornerRadiusPx, cornerRadiusPx, lightPaint)

                // Bottom-Right Dark Cast Shadow
                val darkPaint = NeumorphicShaderPool.paintB.apply {
                    reset()
                    isAntiAlias = true
                    color = darkShadowColor.toArgb()
                    maskFilter = filter
                }
                NeumorphicShaderPool.rectF.set(
                    elevationPx * 0.8f,
                    elevationPx * 0.8f,
                    size.width + elevationPx * 0.8f,
                    size.height + elevationPx * 0.8f
                )
                nativeCanvas.drawRoundRect(NeumorphicShaderPool.rectF, cornerRadiusPx, cornerRadiusPx, darkPaint)
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

            // 2. Precision Directional Inner Inset Shadows via Zero-Allocation Skia Native Pipeline
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                val count = nativeCanvas.save()

                val clipP = NeumorphicShaderPool.clipPath.apply {
                    reset()
                    NeumorphicShaderPool.rectF.set(0f, 0f, width, height)
                    addRoundRect(NeumorphicShaderPool.rectF, cornerRadiusPx, cornerRadiusPx, android.graphics.Path.Direction.CW)
                }
                nativeCanvas.clipPath(clipP)

                val margin = elevationPx * 3f
                val darkShadowOffset = elevationPx * 0.75f

                // --- PASS 1: Top-Left Dark Inset Shadow ---
                val outerP = NeumorphicShaderPool.outerPath.apply {
                    reset()
                    addRect(-margin, -margin, width + margin, height + margin, android.graphics.Path.Direction.CW)
                }
                val innerH = NeumorphicShaderPool.innerHole.apply {
                    reset()
                    NeumorphicShaderPool.rectF.set(
                        darkShadowOffset,
                        darkShadowOffset,
                        width + darkShadowOffset,
                        height + darkShadowOffset
                    )
                    addRoundRect(NeumorphicShaderPool.rectF, cornerRadiusPx, cornerRadiusPx, android.graphics.Path.Direction.CW)
                }
                val darkDiff = NeumorphicShaderPool.diffPath.apply {
                    reset()
                    op(outerP, innerH, android.graphics.Path.Op.DIFFERENCE)
                }

                val darkFilter = NeumorphicShaderPool.getBlurFilter(elevationPx * 0.85f)
                val innerDarkPaint = NeumorphicShaderPool.paintA.apply {
                    reset()
                    isAntiAlias = true
                    color = darkShadowColor.copy(alpha = 0.95f).toArgb()
                    maskFilter = darkFilter
                }
                nativeCanvas.drawPath(darkDiff, innerDarkPaint)

                // --- PASS 2: Bottom-Right Light Highlight Inset Rim ---
                val lightHighlightOffset = elevationPx * 0.75f
                outerP.apply {
                    reset()
                    addRect(-margin, -margin, width + margin, height + margin, android.graphics.Path.Direction.CW)
                }
                innerH.apply {
                    reset()
                    NeumorphicShaderPool.rectF.set(
                        -lightHighlightOffset,
                        -lightHighlightOffset,
                        width - lightHighlightOffset,
                        height - lightHighlightOffset
                    )
                    addRoundRect(NeumorphicShaderPool.rectF, cornerRadiusPx, cornerRadiusPx, android.graphics.Path.Direction.CW)
                }
                val lightDiff = NeumorphicShaderPool.diffPath.apply {
                    reset()
                    op(outerP, innerH, android.graphics.Path.Op.DIFFERENCE)
                }

                val lightFilter = NeumorphicShaderPool.getBlurFilter(elevationPx * 0.75f)
                val innerLightPaint = NeumorphicShaderPool.paintB.apply {
                    reset()
                    isAntiAlias = true
                    color = lightShadowColor.copy(alpha = 0.95f).toArgb()
                    maskFilter = lightFilter
                }
                nativeCanvas.drawPath(lightDiff, innerLightPaint)

                nativeCanvas.restoreToCount(count)
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

    // Glowing Perimeter Neon Ring on Active/Pressed State
    if (neonGlowColor != null) {
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas

            // Pass 1: Soft Outer Halo Glow
            val glowFilter = NeumorphicShaderPool.getBlurFilter(2.5.dp.toPx())
            val glowPaint = NeumorphicShaderPool.paintA.apply {
                reset()
                isAntiAlias = true
                color = neonGlowColor.copy(alpha = 0.35f).toArgb()
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 3.5.dp.toPx()
                maskFilter = glowFilter
            }
            NeumorphicShaderPool.rectF.set(1f, 1f, size.width - 1f, size.height - 1f)
            nativeCanvas.drawRoundRect(NeumorphicShaderPool.rectF, cornerRadiusPx, cornerRadiusPx, glowPaint)

            // Pass 2: Crisp Core Laser Rim
            val corePaint = NeumorphicShaderPool.paintB.apply {
                reset()
                isAntiAlias = true
                color = neonGlowColor.copy(alpha = 0.95f).toArgb()
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 1.6.dp.toPx()
            }
            NeumorphicShaderPool.rectF.set(0.8f, 0.8f, size.width - 0.8f, size.height - 0.8f)
            nativeCanvas.drawRoundRect(NeumorphicShaderPool.rectF, cornerRadiusPx, cornerRadiusPx, corePaint)
        }
    }
}

