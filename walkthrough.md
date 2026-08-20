# UniCalculator — Walkthrough: Per-Screen Segregated History, Screen-Specific Settings & Tools History Persistence

## Overview
We have implemented a segregated calculation history architecture and customized settings modal sheets across all 4 workstation screens in **UniCalculator Bharat**, along with SQLite database persistence for the entire Tools & Converters suite.

---

## 🚀 Key Features Implemented

### 1. Per-Screen Segregated Calculation History (`HistoryTapeScreen.kt`)
- **Multi-Tab Filter Bar**:
  - `All History`: Master chronological audit ledger across all screens.
  - `Standard`: Only standard mathematical calculations.
  - `GST Pro`: Forward & reverse GST invoices with CGST/SGST/IGST tax breakdown.
  - `Cash Tally`: Full cash denomination closing slips with note counts and grand total.
  - `Tools & Units`: Loan EMI schedules, discount stacks, profit margins, and unit conversions.
- **Top Bar History Routing**:
  - Tapping the History button (`🕐`) from any screen directly routes to that screen's pre-filtered history view.
- **Per-Screen Clearing**:
  - Tapping the red Trash button on a filtered tab only clears that specific category from the SQLite database (`deleteByTypes(...)`), leaving the remaining data intact.

### 2. Dedicated Screen-Specific Settings Sheets
Each screen now opens its own tailored Neumorphic modal bottom sheet:
- **Standard Calculator Settings** (`StandardSettingsSheet.kt`):
  - Decimal precision (2, 4, 6, 8 decimals)
  - Number formatting (Indian Vedic `1,00,000` vs Western `100,000`)
  - Haptic feedback intensity (Off / Light / Medium / Strong)
  - Keep Screen Awake toggle
  - Clear Standard History
- **GST Pro Settings** (`GSTProSettingsSheet.kt`):
  - Default Slab on Launch (3%, 5%, 12%, 18%, 28%)
  - Default Jurisdiction (Intra-State CGST+SGST vs Inter-State IGST)
  - Business / Store Name for receipts
  - Banker's Rounding (`HALF_EVEN`) vs Exact Paise
  - Clear GST Invoices
- **Cash Tally Settings** (`CashTallySettingsSheet.kt`):
  - Special Denomination Toggles (Show/Hide ₹2000, ₹2, ₹1 notes)
  - Cashier / Shift Name
  - Auto-copy closing slip to clipboard on save
  - Clear Cash Tally Sessions
- **Tools & Converters Settings** (`ToolsSettingsSheet.kt`):
  - Default Base Currency (INR, USD, EUR, AED)
  - Auto-save conversions to history
  - Clear Tools & Units History

### 3. SQLite Database Persistence for Tools Suite
- Upgraded SQLite Database schema (`DB_VERSION = 2`) in `LocalCalculationHistoryRepository.kt`.
- Thread-safe Singleton (`getInstance(context)`) with reactive `StateFlow` updates across all screens.
- Added `💾 Save to History` buttons in all tools:
  - Loan EMI Calculator
  - Discount Solver
  - Margin & Markup Solver
  - Generic Unit Converters (Length, Mass, Area, Volume, Temperature, Speed, Data, Time)
  - BMI Health Calculator
  - Date & Age Calculator

---

## 🧪 Verification & Hardware Testing
All features were compiled and verified on physical hardware (**Realme RMX3998**, `Android 14`, `1080x2400`):

1. **Settings Sheets**:
   - `StandardSettingsSheet` verified on device.
   - `GSTProSettingsSheet` verified on device.
   - `CashTallySettingsSheet` verified on device.
   - `ToolsSettingsSheet` verified on device.
2. **History Persistence**:
   - Standard math (`4 -> ₹ 4`) recorded and verified.
   - GST Pro reverse calculation (`8455822 - 5% GST -> ₹ 80,53,163.81`) recorded and verified.
   - Cash Tally closing slip (`Total Notes: 640 -> ₹ 1,60,650`) recorded and verified.
   - All History ledger displaying all items with real timestamps, copy, share, and individual delete buttons.
3. **Unit Tests**:
   - Ran `./gradlew testDebugUnitTest` ➔ **100% Passed**.

---
*Signed by: Shoeb Ahmad*
