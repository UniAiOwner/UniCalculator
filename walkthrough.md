# 🚀 Walkthrough: In-Words Language Setting (English, Hindi, Both, Off) & Pure Number Words Engine

UniCalculator has been enhanced with:
1. **Pure Mathematical In-Words Number Mode** (eliminating the `"Rupees"` / `"रुपये"` suffix in Standard Calculator math).
2. **Comprehensive In-Words Language Preference** (`English`, `Hindi`, `Both`, `Off`) fully configurable across all settings sheets.
3. **1-Month (30-Day) 100% Free Full-Access Trial Engine** & **Neumorphic Pro Subscription Suite** under **UniCore Technologies**.

---

## 🌟 Key Accomplishments

### 1. 🔤 Pure Mathematical Number Words vs Currency Mode
- **Pure Math Mode (`includeRupeesSuffix = false`)**:
  - `100` ➔ `"One Hundred"` (EN) / `"एक सौ"` (HI)
  - `100.50` ➔ `"One Hundred Point Fifty"` (EN) / `"एक सौ दशमलव पचास"` (HI)
  - `0` ➔ `"Zero"` (EN) / `"शून्य"` (HI)
  - `-500` ➔ `"Minus Five Hundred"` (EN) / `"ऋण पाँच सौ"` (HI)
  - No `"Rupees"` or `"रुपये"` appended in standard arithmetic calculations.
- **Currency Mode (`includeRupeesSuffix = true`)**:
  - Automatically activated for GST Invoices, Cash Tally closing slips, and when `"Show Currency Symbol (₹)"` toggle is enabled (`"One Hundred Rupees Only"` / `"एक सौ रुपये मात्र"`).

### 2. ⚙️ In-Words Language Preference (`English`, `Hindi`, `Both`, `Off`)
- **4 Configurable Options**:
  - **`English` (Default)**: In-words text in English.
  - **`Hindi`**: In-words text in pure Hindi (Devanagari).
  - **`Both`**: Dual-language display (`"One Hundred • एक सौ"`).
  - **`Off`**: In-words display hidden for users wanting pure numeric minimalism.
- **Unified UI Across All 4 Settings Sheets**:
  - `StandardSettingsSheet.kt`
  - `GSTProSettingsSheet.kt`
  - `CashTallySettingsSheet.kt`
  - `ToolsSettingsSheet.kt`

### 3. 🎁 1-Month Free Full-Access Trial Engine & Pro Subscription Suite
- **30-Day Free Trial**: Complete unlocked access to all workstations and tools.
- **India-First Nominal Plans**: Monthly ₹29, Annual ₹199 (Save 45%), Lifetime ₹499.
- **Standard Calculator**: 100% Free Forever with Zero Ads.

---

## 📸 Physical Hardware Verification (Realme RMX3998)

````carousel
![Standard Calculator Pure Math (One Hundred)](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/361_calc_100_one_hundred.png)
<!-- slide -->
![Settings In-Words Language Selector Cards](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/363_settings_clean_pills.png)
<!-- slide -->
![Hindi Mode Active (एक सौ)](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/369_hindi_active.png)
<!-- slide -->
![Both Mode Active (One Hundred • एक सौ)](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/370_both_active.png)
<!-- slide -->
![Off Mode Active (Words Hidden)](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/371_off_active.png)
<!-- slide -->
![Pro Subscription Sheet](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/354_pro_sheet_fixed_header.png)
````

---

## 🧪 Verification Results
- **Automated Tests**: `./gradlew testDebugUnitTest` ➔ **PASSED (0 errors, 100% test pass rate)**.
- **Build**: `./gradlew :app:assembleDebug` ➔ **BUILD SUCCESSFUL**.
- **Physical Device**: Verified live on Realme phone across all language modes.

