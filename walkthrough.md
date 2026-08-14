# UniCalculator — Implementation & Verification Walkthrough

## 🌟 Executive Summary
The native Android codebase for **UniCalculator** has been architected and built matching the approved Neumorphic UI mockups. The project adheres to modern Clean Architecture across 10 subprojects, utilizing Jetpack Compose, Kotlin 2.0, and pure custom Canvas-rendered Neumorphic lighting mechanics.

---

## 🏛️ Multi-Module Architecture Overview

```
UniCalculator/
├── app/                      # Application Shell, Splash Screen, & Bottom Navigation Host
├── core/
│   ├── designsystem/         # Custom Neumorphic Engine (Convex, Concave, Flat, Haptic & Colors)
│   ├── math-engine/          # High-Precision Indian GST Engine, Shunting-Yard Evaluator, Margin/EMI Solvers
│   ├── model/                # Immutable Domain Models (TaxBreakdown, DenominationItem, CashTallyState)
│   ├── common/               # Indian Vedic Formatter & Bilingual (EN/HI) Currency Word Converters
│   └── database/             # Offline-First Calculation & Audit Tape Persistence
└── feature/
    ├── calculator/           # Standard Calculator + Direct GST Slab Pills (+5%, +12%, +18%, +28%)
    ├── cash-tally/           # High-Speed Denomination Cash Counter (₹2000 down to ₹1 + Coins + WhatsApp Slip)
    ├── business-tools/       # Dual Margin/Markup Solver & Loan EMI Calculator
    └── history-tape/         # Real-time Mathematical Audit Tape & Slip Exporter
```

---

## 🎨 Neumorphic Design System Highlights (`:core:designsystem`)

1. **Dual-Directional 3D Light Casting**:
   - `NeumorphicModifier.kt`: Custom Modifier using Compose Canvas, custom path operations, and `BlurMaskFilter` to render tactile convex surface elevations and recessed concave LCD wells without bitmap overhead.
2. **Interactive Physics-Driven Buttons**:
   - `NeumorphicButton.kt`: Dynamic surface morphing (convex $\to$ concave) with spring animation (`dampingRatio = 0.75f`) and mechanical vibration click feedback via `NeumorphicHapticEngine`.
3. **Recessed LCD Well**:
   - `NeumorphicLCDWell.kt`: Inset deep display well with auto-scaling dynamic typography (24sp to 42sp) and real-time Indian legal word sub-badges.

---

## 🧮 Indian GST & Financial Engines (`:core:math-engine`)

1. **Precision & Banker's Rounding**:
   - Built on `BigDecimal` with `HALF_EVEN` rounding to eliminate fractional paisa leakage.
   - Forward GST (+5%, +12%, +18%, +28%) with accurate splitting into CGST, SGST, or IGST.
   - Reverse GST (Tax Included) computing accurate Base Price from MRP.
2. **Denomination Matrix (`:feature:cash-tally`)**:
   - Supports ₹2000, ₹500, ₹200, ₹100, ₹50, ₹20, ₹10, ₹5, ₹2, ₹1 banknotes and Coins.
   - Instant WhatsApp closing summary slip generation.

---

## 🧪 Verification & Build Status

- **Unit Test Suite**:
  - Command: `./gradlew :core:math-engine:test`
  - Result: **100% Passed** (Verified 18% Forward GST, 18% Reverse GST on ₹1500, and Shunting-Yard expression parsing).
- **Assemble Debug APK**:
  - Command: `./gradlew :app:assembleDebug`
  - Result: **BUILD SUCCESSFUL** (Generated 17MB debug binary at `app/build/outputs/apk/debug/app-debug.apk`).
