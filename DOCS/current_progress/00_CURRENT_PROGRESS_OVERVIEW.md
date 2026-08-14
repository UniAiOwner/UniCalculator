# 00. Current Progress & Engineering Overview

## 📌 Executive Summary
**UniCalculator** is an ultra-tactile Neumorphic commercial computation platform designed specifically for Indian businesses, retail shopkeepers, CA professionals, and enterprise traders. Built on Kotlin Multi-Module Clean Architecture with 100% Jetpack Compose.

---

## 📱 Live Screen Status & Visual Matrix

| Module | Screen Key | Primary Utility | Live Hardware Verification |
|---|---|---|---|
| **Standard** | `Tab 0` | Everyday arithmetic & high-speed cashier entry | ![Standard Calc](visuals/01_screen_standard_calc.png) |
| **GST Pro (+GST)** | `Tab 1` | Forward statutory tax computation & multi-qty math | ![GST Pro Forward](visuals/02_screen_gst_pro_forward.png) |
| **GST Pro (−GST)** | `Tab 1` | Reverse tax deconstruction & Inter-state IGST | ![GST Pro Reverse](visuals/03_screen_gst_pro_reverse_interstate.png) |
| **Cash Tally** | `Tab 2` | Bank denomination counter & cash closing summary | ![Cash Tally](visuals/04_screen_cash_tally.png) |
| **Business Tools**| `Tab 3` | Retail profit margin, markup & EMI calculations | ![Business Tools](visuals/05_screen_business_tools.png) |
| **History Tape** | `Tab 4` | Audit roll, transaction logs & invoice export | ![History Tape](visuals/06_screen_history_tape.png) |

---

## 🏛️ System Architecture

```mermaid
graph TD
    App[":app Application"] --> FeatCalc[":feature:calculator"]
    App --> FeatCash[":feature:cash-tally"]
    App --> FeatTools[":feature:business-tools"]
    App --> FeatTape[":feature:history-tape"]
    
    FeatCalc --> CoreDesign[":core:designsystem"]
    FeatCalc --> CoreMath[":core:math-engine"]
    FeatCalc --> CoreDB[":core:database"]
    
    CoreDesign --> NeumorphicModifier["Neumorphic 3D & Option 2 Neon Shader"]
    CoreMath --> VedicEngine["Indian Vedic Numbering & Tax Calculator"]
```

---

## ⚡ Key Milestone Achievements (v1.0.0-rc12)
1. **Option 2 Perimeter Neon Ring**: Dual-pass hardware-accelerated laser underglow rim on active buttons and slab pills.
2. **Dual Neumorphic Slidable Switches**: Spring-animated 2-state toggle switches (`[+GST ⇄ −GST]` and `[CGST+SGST ⇄ IGST]`).
3. **Master Receipt Display at Top**: Relocated unified receipt/display card to top viewport position with grand typography.
4. **Multi-Line In-Words Micro-Plate**: Formal banking transcription (`IN WORDS: ...`) wrapped cleanly across 2 lines without truncation.
5. **Inline Quantity & Unit Rate Arithmetic**: Live expression evaluator for `÷` and `×` keys on GST Pro numpad.
