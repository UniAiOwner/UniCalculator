# Walkthrough: Configurable Currency Symbol & Words in Standard Calculator (Default OFF)

## 🎯 Goal
Allow the user to use Standard Calculator for pure, general arithmetic without forcing the Indian Rupee symbol (`₹`) or `"Rupees Only"` suffix, while providing a toggle in the Standard Calculator Settings sheet to turn it back ON whenever desired (defaulting to **OFF**).

---

## 🛠️ Key Architectural Changes

### 1. Data Store & Preferences (`:core:common`)
- **File**: `UniCalculatorPreferences.kt`
- Added `KEY_SHOW_CURRENCY_SYMBOL` with `_showCurrencySymbol = MutableStateFlow(false)`.
- Added setter `setShowCurrencySymbol(Boolean)`.

### 2. Multi-Language In-Words Engine (`:core:common`)
- **File**: `IndianCurrencyWordConverter.kt`
- Updated `convertToWords(amount: BigDecimal, inHindi: Boolean = false, includeRupeesSuffix: Boolean = true)`.
- When `includeRupeesSuffix = false`:
  - English: `Zero`, `Twelve Thousand Five Hundred`
  - Hindi: `शून्य`, `बारह हज़ार पाँच सौ`
- When `includeRupeesSuffix = true`:
  - English: `Zero Rupees Only`, `Twelve Thousand Five Hundred Rupees Only`
  - Hindi: `शून्य रुपये मात्र`, `बारह हज़ार पाँच सौ रुपये मात्र`

### 3. Standard Calculator Settings Sheet (`:feature:calculator`)
- **File**: `StandardSettingsSheet.kt`
- Added a dedicated top Neumorphic Plate: **"Show Currency Symbol (₹)"** with description *"Prefix results with '₹' & 'Rupees Only'"* and Neumorphic slide toggle switch.

### 4. ViewModel & Screen Integration (`:feature:calculator`)
- **File**: `StandardCalculatorViewModel.kt`
  - Dynamically passes `includeSymbol = showCurrency` to `IndianVedicFormatter.formatCurrency(...)`.
  - Dynamically passes `includeRupeesSuffix = showCurrency` to `IndianCurrencyWordConverter.convertToWords(...)`.
  - Resets to clean `"0"` and `"Zero"` on clear.
- **File**: `StandardCalculatorScreen.kt`
  - Observes `prefs.showCurrencySymbol` and synchronizes with ViewModel.

---

## 📱 Hardware Verification & Visual Evidence

| Default OFF (Pure Math: `0`, `Zero`) | Calculation `500 × 25 = 12,500` |
| :---: | :---: |
| ![Default Clean Screen](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/108_currency_off_default.png) | ![Pure Math Result](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/109_calc_currency_off.png) |

| Settings Toggle Sheet (Default OFF) | Toggled ON Verification (`₹ 12,500`) |
| :---: | :---: |
| ![Settings Sheet](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/110_settings_currency_toggle.png) | ![Currency ON Result](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/114_currency_on_screen.png) |

---

## 🧪 Automated Tests
- `./gradlew testDebugUnitTest` ➔ **100% Passed (0 Failures)**.
