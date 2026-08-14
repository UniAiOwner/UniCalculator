# 📋 03. SYSTEM & PRODUCT REQUIREMENTS
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. Functional Requirements (FR)

### FR-01: Standard Dual-Line Math Engine
- **FR-01.1**: Continuous real-time evaluation as the user types without pressing `=`.
- **FR-01.2**: Top expression line (scrollable horizontal viewport, max length 500 characters) + Bottom result line (dynamic auto-sizing font).
- **FR-01.3**: Support for basic operators: Addition `+`, Subtraction `-`, Multiplication `×`, Division `÷`, Percentage `%`, Square Root `√`, Powers `xʸ`, Parentheses `()`, Sign Inversion `+/-`.
- **FR-01.4**: Percentage calculation logic matching Indian commercial conventions:
  - `500 + 10% = 550`
  - `500 - 10% = 450`
  - `500 × 10% = 50`
- **FR-01.5**: Classic Citizen Memory operations:
  - `M+`: Adds current display value to persistent memory accumulator.
  - `M-`: Subtracts current display value from memory accumulator.
  - `MR`: Recalls memory value into active input.
  - `MC`: Clears memory accumulator.
  - `GT`: Grand Total accumulation key (sums all `=` calculation results since last clear).

### FR-02: Bharat GST Super-Engine
- **FR-02.1**: Preset slab rate quick-buttons:
  - Forward (Exclusive): `+0%`, `+5%`, `+12%`, `+18%`, `+28%`.
  - Reverse (Inclusive): `-0%`, `-5%`, `-12%`, `-18%`, `-28%`.
- **FR-02.2**: Dedicated Custom GST Chip: Allows setting any arbitrary rate (e.g., `3%`, `0.25%`, `7.5%`).
- **FR-02.3**: Compensation Cess Engine: Add percentage-based Cess (e.g. +12% Cess) or flat amount-based Cess (e.g. +₹4170).
- **FR-02.4**: Jurisdiction Split Mode:
  - **Intra-State (Default)**: Splits total tax equally into **CGST (Central GST)** 50% and **SGST (State GST)** 50%.
  - **Inter-State**: Assigns total tax to **IGST (Integrated GST)** 100%.
  - **UTGST (Union Territory GST)**: Switchable in settings for Andaman, Ladakh, Lakshadweep, etc.
- **FR-02.5**: Granular Result Cards displaying:
  - Net Base Amount
  - Total GST Amount
  - CGST Amount & Rate
  - SGST Amount & Rate
  - IGST Amount & Rate
  - Cess Amount
  - Final Gross Amount (MRP)

### FR-03: Cash Denomination Ledger (Rokad Khata / Tally)
- **FR-03.1**: Complete Denomination rows for all active RBI notes and coins:
  - `₹2000` (Legacy / High Value Note)
  - `₹500` (High Value Note)
  - `₹200` (Medium Note)
  - `₹100` (Standard Note)
  - `₹50` (Standard Note)
  - `₹20` (Paper Note & Coin)
  - `₹10` (Paper Note & Coin)
  - `₹5` (Paper Note & Coin)
  - `₹2` (Paper Note & Coin)
  - `₹1` (Paper Note & Coin)
  - `Coins Matrix`: Granular coin counter for ₹20, ₹10, ₹5, ₹2, ₹1 coins + Total lump-sum input.
- **FR-03.2**: Incremental quantity steppers (`-` and `+`) + Direct numeric keypad input for large note bundles.
- **FR-03.3**: Real-time subtotal per row (`Notes × Face Value = Row Subtotal`).
- **FR-03.4**: Global Summary Card showing:
  - Total Cash Amount (`₹`)
  - Total Physical Note Count (`Pcs`)
  - Amount in Words in **Indian Vedic System (Lakhs & Crores)** in English & Hindi.
- **FR-03.5**: Export Slip Formatter: Generates clean, copyable text or PDF slip for WhatsApp sharing with Date, Time, Shop Name, Denomination Table, and Total.

### FR-04: Commercial & Business Tools
- **FR-04.1 Margin & Markup Pro**: Calculate Cost Price, Selling Price, Gross Profit, Margin %, and Markup % dynamically when any 2 values are provided.
- **FR-04.2 Discount Stacking**: Calculate trade discount + cash discount chained sequentially (e.g., `MRP - 20% Trade Discount - 5% Cash Discount + 18% GST`).
- **FR-04.3 Indian Loan EMI Calculator**: Monthly EMI, Total Interest, Principal vs Interest pie breakdown, and annual amortization schedule.
- **FR-04.4 SIP / Mutual Fund & Compound Interest**: Expected returns for monthly SIP investments.

### FR-05: Infinite Audit History Tape
- **FR-05.1**: Every completed calculation is logged with ISO timestamp, calculation mode, formula expression, and result.
- **FR-05.2**: Tap-to-Restore: Tapping any past item injects its value directly back into the live calculator keypad.
- **FR-05.3**: Search & Filter by date range or category tag (GST, Math, Cash Tally).
- **FR-05.4**: Export options: CSV, Plain Text, or Formatted PDF Receipt.

---

## 2. Non-Functional Requirements (NFR)

- **NFR-01: Absolute Mathematical Exactness**:
  - Zero use of `Double` or `Float` for financial arithmetic.
  - Mandatory `java.math.BigDecimal` with Banker's Rounding (`RoundingMode.HALF_EVEN`).
  - Internal scale precision set to 10 decimal places during intermediate steps, rounded to 2 decimal places for INR currency outputs.
- **NFR-02: 120 FPS High-Refresh Rate UI**:
  - Jetpack Compose hardware-accelerated Canvas rendering.
  - Zero frame drops during rapid typing (< 1ms calculation execution time).
- **NFR-03: Advanced Neumorphic Rendering Performance**:
  - Dual shadow passes (Light blur + Dark blur) rendered efficiently using custom `DrawModifier` / Compose Layer caching to avoid unnecessary recompositions or GPU overdraw.
- **NFR-04: Offline-First & Zero Permission Leakage**:
  - Works 100% without internet access.
  - No internet permission required for core functionality.
  - Data stored locally in encrypted Room SQLite database.
- **NFR-05: App Size & Startup Time**:
  - Release APK < 12 MB.
  - Cold launch time < 70ms.
