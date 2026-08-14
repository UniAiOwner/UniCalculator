package com.unicalculator.core.designsystem.modifier

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
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
    backgroundColor: Color? = null
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
            // Background surface with subtle cavity depression shading
            if (backgroundColor != null) {
                drawRoundRect(
                    color = backgroundColor,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
                // Subtle cavity ambient darkening (sunken well depth)
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.04f),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }
            // Inner Inset Shadows
            drawIntoCanvas { canvas ->
                canvas.save()
                val clipPath = Path().apply {
                    addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(cornerRadiusPx)))
                }
                canvas.clipPath(clipPath)

                // Top-Left Deep Inset Shadow
                val innerDarkPaint = Paint().apply {
                    color = darkShadowColor.copy(alpha = 0.95f)
                    style = PaintingStyle.Stroke
                    strokeWidth = elevationPx * 2.5f
                    asFrameworkPaint().maskFilter = BlurMaskFilter(elevationPx * 1.4f, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawRoundRect(
                    left = -elevationPx * 0.8f,
                    top = -elevationPx * 0.8f,
                    right = size.width + elevationPx * 0.8f,
                    bottom = size.height + elevationPx * 0.8f,
                    radiusX = cornerRadiusPx,
                    radiusY = cornerRadiusPx,
                    paint = innerDarkPaint
                )

                // Bottom-Right Crisp Inset Light Rim
                val innerLightPaint = Paint().apply {
                    color = lightShadowColor.copy(alpha = 0.9f)
                    style = PaintingStyle.Stroke
                    strokeWidth = elevationPx * 2.5f
                    asFrameworkPaint().maskFilter = BlurMaskFilter(elevationPx * 1.4f, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawRoundRect(
                    left = elevationPx * 0.8f,
                    top = elevationPx * 0.8f,
                    right = size.width + elevationPx * 1.8f,
                    bottom = size.height + elevationPx * 1.8f,
                    radiusX = cornerRadiusPx,
                    radiusY = cornerRadiusPx,
                    paint = innerLightPaint
                )
                canvas.restore()
            }
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
}
