# 06. Neumorphic Design System V2 & Shaders

## 🎯 Core Philosophy: Soft-Physical Tactility
UniCalculator implements a bespoke, high-performance Neumorphic rendering engine designed specifically for Android with 60fps/120fps hardware acceleration.

---

## 🎨 Color Tokens & Harmonies

```kotlin
val NeumorphicBackground  = Color(0xFFF0EEE9) // Warm Off-White Cream
val NeumorphicLightShadow = Color(0xFFFFFFFF) // Pure Top-Left Sun Highlight
val NeumorphicDarkShadow  = Color(0xFFC7C2B6) // Bottom-Right Natural Ambient Shadow
val InsetCharcoalShadow   = Color(0xFF6E6A60) // Deep Well Inset Shadow

val RupeeEmeraldGreen     = Color(0xFF059669) // Standard GST & Profit
val GstSaffronAmber       = Color(0xFFD97706) // Reverse GST & 28% Luxury
val ElectricSapphireBlue  = Color(0xFF2563EB) // Invoices & IGST
val OperatorOrange        = Color(0xFFEA580C) // Math Operators (×, ÷, +, −)
val DeleteRed             = Color(0xFFDC2626) // Clear & Reset
```

---

## 🟢 Option 2: Perimeter Neon Ring Shader

### Dual-Pass Render Architecture
When a button is pressed or an active GST slab is selected:

1. **Surface Inset**: `NeumorphicShape.CONCAVE` with top-left charcoal cast shadow and `4%` ambient cavity dimming.
2. **Pass 1 (Soft Ambient Glow)**:
   ```kotlin
   val glowPaint = Paint().apply {
       color = neonGlowColor.copy(alpha = 0.35f)
       style = PaintingStyle.Stroke
       strokeWidth = 3.5.dp.toPx()
       asFrameworkPaint().maskFilter = BlurMaskFilter(2.5.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
   }
   ```
3. **Pass 2 (Crisp Laser Rim)**:
   ```kotlin
   val corePaint = Paint().apply {
       color = neonGlowColor.copy(alpha = 0.95f)
       style = PaintingStyle.Stroke
       strokeWidth = 1.6.dp.toPx()
   }
   ```
