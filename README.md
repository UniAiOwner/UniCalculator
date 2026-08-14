# 📱 UniCalculator — Bharat's Pro 100% Neumorphic Financial & GST Calculator
### *The Ultimate Tactile, 100% Offline-First Calculator Engineered for Indian Commerce*

<p align="center">
  <img src="UI_MOCKUPS/01_main_calculator_gst_pro_screen.jpg" alt="UniCalculator Main Screen" width="300"/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="UI_MOCKUPS/02_cash_denomination_tally_screen.jpg" alt="UniCalculator Cash Tally" width="300"/>
</p>

<p align="center">
  <a href="#-key-features"><img src="https://img.shields.io/badge/UI_Style-100%25_Neumorphism_3D-00875A?style=for-the-badge" alt="Neumorphic UI"/></a>
  <a href="#-architecture"><img src="https://img.shields.io/badge/Architecture-Clean_Hexagonal_DDD-blue?style=for-the-badge" alt="Clean DDD"/></a>
  <a href="#-precision"><img src="https://img.shields.io/badge/Math_Engine-Exact_BigDecimal-FF9933?style=for-the-badge" alt="BigDecimal"/></a>
  <a href="#-privacy"><img src="https://img.shields.io/badge/Offline_First-100%25_Private-success?style=for-the-badge" alt="Offline-First"/></a>
  <a href="#-platform"><img src="https://img.shields.io/badge/Platform-Android_Jetpack_Compose-3DDC84?style=for-the-badge&logo=android" alt="Android Compose"/></a>
</p>

---

## 🌟 Executive Overview

**UniCalculator** is a flagship Android calculator engineered specifically for the Indian economic landscape — Kirana store owners, wholesale traders, Chartered Accountants (CAs), tax practitioners, small business merchants, and daily shoppers.

Combining the satisfying physical keystroke feel of classic Japanese & Citizen desktop calculators with a **Pure Advanced Neumorphism UI (Soft 3D Tactile Design)**, UniCalculator delivers:
1. **Instant Indian GST Slabs & Reverse GST Engine** (Extract base price from MRP in 1 tap).
2. **Intra-State (CGST + SGST) vs. Inter-State (IGST) Live Breakdown Plates**.
3. **Cash Denomination Ledger (रोकड़ खाता)** from **₹2000 down to ₹1 notes + coins** with instant English & Hindi **Number-to-Words translation in Lakhs & Crores**.
4. **100% Offline-First Privacy**: Zero tracking, zero server costs, zero ads, sub-60ms instant cold launch.

---

## 📸 High-Definition Visual Mockups

| 📱 Screen 1: Main & GST Pro Dual Engine | 💵 Screen 2: Cash Denomination Ledger (Rokad) |
| :---: | :---: |
| <img src="UI_MOCKUPS/01_main_calculator_gst_pro_screen.jpg" width="360" alt="Main Screen"/> | <img src="UI_MOCKUPS/02_cash_denomination_tally_screen.jpg" width="360" alt="Cash Tally Screen"/> |
| *Recessed LCD Well + Glowing GST Slabs + 3D Squircle Keys* | *Master Summary + ₹2000 to ₹1 Steppers + WhatsApp Slip* |

---

## ✨ Key Features & Bharat Superpowers

### 1. ⚡ Instant Indian GST Calculation Super-Engine
- **1-Tap Slabs**: Dedicated tactile buttons for `+5%`, `+12%`, `+18%`, `+28%` and `-5%`, `-12%`, `-18%`, `-28%` (Reverse GST).
- **Reverse GST Extraction**: Eliminates the complex `Base = Total / (1 + Rate/100)` manual formula. Instantly splits any MRP bill into **Net Base Price + Tax**.
- **Jurisdiction Live Split**:
  - **Intra-State (Default)**: 50% CGST + 50% SGST.
  - **Inter-State**: 100% IGST.
- **Special Bharat Slabs**: Preset chips for **3% (Gold & Jewellery)** and **0.25% (Cut & Polished Diamonds)**.
- **Cess Engine**: Percentage-based or flat amount-based compensation cess.

### 2. 💰 Cash Denomination Ledger (रोकड़ खाता / Cash Tally)
- **Full RBI Currency Spectrum**:
  - High Value: **₹2000, ₹500**
  - Medium / Standard: **₹200, ₹100, ₹50**
  - Small Denominations: **₹20, ₹10, ₹5, ₹2, ₹1**
  - **Granular Coin Counter**: ₹20, ₹10, ₹5, ₹2, ₹1 Coins.
- **Indian Vedic Number-to-Words Engine**:
  - English: *"Rupees One Lakh Eighty-Four Thousand Six Hundred Fifty Rupees Only"*
  - Hindi: *"एक लाख चौरासी हज़ार छह सौ पचास रुपये मात्र"*
- **1-Tap WhatsApp Closing Slip**: Instantly format and share daily cash closing summaries with business partners.

### 3. 🎨 100% Pure Advanced Neumorphism UI
- **Light Physics Model**: $-45^\circ$ top-left light source simulating soft white highlights (`#FFFFFF` 75%) and deep slate shadows (`#000000` 20%).
- **Convex-to-Concave Press Transitions**: Buttons physically depress into the surface with smooth spring bounce (`spring(dampingRatio = 0.75f, stiffness = 500f)`).
- **Recessed LCD Screen Well**: Inset titanium texture container with dynamic font auto-scaling.
- **Crisp Mechanical Switch Haptics**: 4ms micro-click feedback powered by `VibrationEffect.Composition`.

### 4. 🧮 Zero-Loss Financial Math Integrity
- Zero floating-point drift (`0.1 + 0.2` never equals `0.30000000000000004`).
- Strict `java.math.BigDecimal` pipeline with Banker's Rounding (`RoundingMode.HALF_EVEN`).
- Precision tested against official **CBIC (Central Board of Indirect Taxes and Customs)** sample invoices.

---

## 🗂️ Complete 23-Stage Lifecycle Documentation

Every architectural, design, and engineering decision is comprehensively documented:

| # | Document | Key Focus |
| :- | :--- | :--- |
| **01** | [01_APP_IDEA.md](DOCS/01_APP_IDEA.md) | Vision, Bharat Problem Statement, Value Proposition |
| **02** | [02_PRODUCT_STRATEGY.md](DOCS/02_PRODUCT_STRATEGY.md) | Market Positioning, Competitor Matrix (Citizen vs Google Calc), KPIs |
| **03** | [03_REQUIREMENTS.md](DOCS/03_REQUIREMENTS.md) | Functional (GST, Cash Tally, Citizen Math) & NFRs (BigDecimal) |
| **04** | [04_USER_RESEARCH.md](DOCS/04_USER_RESEARCH.md) | Kirana & Trader Behavioral Insights, Reverse GST pain points |
| **05** | [05_USER_PERSONAS.md](DOCS/05_USER_PERSONAS.md) | Ramesh (Kirana), Priya (CA Intern), Vikram (Gadget Trader) |
| **06** | [06_USER_JOURNEYS.md](DOCS/06_USER_JOURNEYS.md) | Evening Cash Closing, Reverse GST Verification, WhatsApp Quotes |
| **07** | [07_INFORMATION_ARCHITECTURE.md](DOCS/07_INFORMATION_ARCHITECTURE.md) | Component Taxonomy, Screen Hierarchy, History Tape System |
| **08** | [08_USER_FLOWS.md](DOCS/08_USER_FLOWS.md) | GST Slabs State Machine, Cash Tally Step Engine, Slip Exporter |
| **09** | [09_NAVIGATION_STRUCTURE.md](DOCS/09_NAVIGATION_STRUCTURE.md) | Type-Safe Navigation Suite, Phone Bottom Bar & Tablet Nav Rail |
| **10** | [10_CONTENT_STRATEGY.md](DOCS/10_CONTENT_STRATEGY.md) | Vedic Indian Numbering (Lakhs/Crores), Hindi/English Words Engine |
| **11** | [11_WIREFRAMES.md](DOCS/11_WIREFRAMES.md) | ASCII Layouts of Neumorphic LCD Well, Keypads & Cash Ledger |
| **12** | [12_DESIGN_SYSTEM.md](DOCS/12_DESIGN_SYSTEM.md) | **Advanced Neumorphism UI Architecture**, Dual Shadows, Convex/Concave States |
| **13** | [13_UI_DESIGN.md](DOCS/13_UI_DESIGN.md) | Keypad Dimensions, Squircle Radius, LCD Inset Well Typography |
| **14** | [14_INTERACTION_MOTION.md](DOCS/14_INTERACTION_MOTION.md) | Physics Spring Press Animations, Custom Android Mechanical Haptics |
| **15** | [15_RESPONSIVE_ACCESSIBILITY.md](DOCS/15_RESPONSIVE_ACCESSIBILITY.md) | Tablet Dual-Pane, TalkBack Semantics in Indian Languages |
| **16** | [16_TECHNICAL_DATA_DESIGN.md](DOCS/16_TECHNICAL_DATA_DESIGN.md) | Hexagonal Architecture, `BigDecimal` GST Engine, Room Database |
| **17** | [17_SECURITY_EDGE_CASES.md](DOCS/17_SECURITY_EDGE_CASES.md) | Banker's Rounding, Odd Half-Paisa Tax Splits, Offline-First Security |
| **18** | [18_PROTOTYPE.md](DOCS/18_PROTOTYPE.md) | Multi-Module Gradle Directory Skeleton & Version Catalog |
| **19** | [19_USER_VALIDATION.md](DOCS/19_USER_VALIDATION.md) | Official CBIC GST Benchmark Invoices & JUnit 5 Testing Suite |
| **20** | [20_DEVELOPMENT.md](DOCS/20_DEVELOPMENT.md) | 4-Phase Multi-Sprint Delivery Schedule |
| **21** | [21_QA.md](DOCS/21_QA.md) | Strict Zero-Suppress Detekt, Spotless & Perfetto 120 FPS Profiling |
| **22** | [22_ITERATION.md](DOCS/22_ITERATION.md) | OCR Barcode & Invoice Scanner, Offline Indian Voice Calculator |
| **23** | [23_BRANDING_LOGO_SPLASH.md](DOCS/23_BRANDING_LOGO_SPLASH.md) | Adaptive App Icon Vector, Wordmark Logo, Android 12+ Splash System |

---

## 🏗️ Multi-Module Architecture

```
UniCalculator/
├── core/
│   ├── common/           # Coroutines, Vedic Formatters, Number-to-Words Engine
│   ├── model/            # Domain Value Objects (TaxBreakdown, Denomination, History)
│   ├── math-engine/      # BigDecimal GST & Continuous Shunting-Yard Evaluator
│   ├── database/         # Room SQLite Database & DAOs (Encrypted Local Storage)
│   └── designsystem/     # 100% Neumorphic Modifiers, Tactile Keypads, Themes, Haptics
│
├── feature/
│   ├── calculator/       # Standard Dual-Line Neumorphic Calculator
│   ├── gst/              # GST Pro Engine, Forward/Reverse Slabs & Split Cards
│   ├── cash-tally/       # RBI Cash Denomination Ledger & WhatsApp Slip Exporter
│   ├── business-tools/   # Margin/Markup, Discount Stacking & Loan EMI Solvers
│   └── history-tape/     # Infinite Audit Tape & PDF/CSV Exporter
│
└── app/                  # Application Host, MainActivity, Adaptive Navigation Suite
```

---

## 🛠️ Technology Stack

- **Language**: Kotlin 2.1.0 (Strict null safety, value classes, Coroutines 1.10.1, Flow)
- **UI Toolkit**: Jetpack Compose (100% Declarative, Compose BOM 2025.02.00, Custom Neumorphic Canvas Shaders)
- **Architecture**: Hexagonal Architecture + Clean DDD + MVI StateFlow
- **Dependency Injection**: Dagger Hilt 2.55
- **Local Persistence**: Jetpack Room 2.6.1 (SQLite with Flow Observables)
- **Build System**: Gradle 8.8 KTS with Modern Version Catalog (`libs.versions.toml`)
- **Code Quality**: Detekt (Zero-Suppress Policy) + Spotless Formatting

---

## 🚀 WhatsApp Cash Tally Slip Output Preview

```text
========================================
   🧾 GUPTA GENERAL STORES — CASH TALLY
========================================
📅 Date: 14-Aug-2026 | ⏰ Time: 09:35 PM

DENOMINATION BREAKDOWN:
----------------------------------------
₹ 500  x  18  =  ₹ 9,000.00
₹ 200  x  45  =  ₹ 9,000.00
₹ 100  x 120  =  ₹ 12,000.00
₹  50  x  80  =  ₹ 4,000.00
₹  20  x  45  =  ₹ 900.00
₹  10  x  50  =  ₹ 500.00
Coins         =  ₹ 25.00
----------------------------------------
🔢 Total Notes : 358 Pcs
💰 GRAND TOTAL : ₹ 35,425.00
----------------------------------------
📝 IN WORDS:
Thirty-Five Thousand Four Hundred Twenty-Five Rupees Only
(पैंतीस हज़ार चार सौ पच्चीस रुपये मात्र)
========================================
✨ Generated via UniCalculator Bharat
```

---

## 📜 License & Integrity
Built with ❤️ for Indian MSMEs, Retailers, and Commerce.  
*All calculations validated against Government of India CBIC GST invoicing norms.*
