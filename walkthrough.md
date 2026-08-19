# Walkthrough: Commercial Percentage & Standard Calculator Arithmetic Precision Fixes

## 🎯 What Was Fixed & Verified

### 1. Contextual Commercial Percentage Engine (`%`)
- **Problem**: Previously, entering `100 - 10%` evaluated to `0.90` because the engine naively divided the evaluated expression `(100 - 10) / 100 = 0.90`.
- **Solution**: Implemented standard commercial/desk calculator percentage preprocessing in `ShuntingYardEvaluator.kt` and `StandardCalculatorViewModel.kt`:
  - **Discount (`A - B%`)**: `A - (A * B / 100)` ➔ `100 - 10%` = **`₹ 90`** (`Ninety Rupees Only`).
  - **Markup / Tax (`A + B%`)**: `A + (A * B / 100)` ➔ `100 + 10%` = **`₹ 110`** (`One Hundred Ten Rupees Only`).
  - **Portion (`A × B%`)**: `A * (B / 100)` ➔ `100 × 10%` = **`₹ 10`**.
  - **Ratio / Margin (`A ÷ B%`)**: `A / (B / 100)` ➔ `100 ÷ 10%` = **`₹ 1,000`**.
  - **Standalone (`B%`)**: `B / 100` ➔ `50%` = **`₹ 0.50`**.

### 2. Consecutive Operator Replacement & Decimal Safety
- Seamlessly replaces previous operator if a new operator is tapped (`100 +` followed by `×` becomes `100 ×`).
- Protected against multiple decimal point inputs within a single operand.
- Operator block backspacing (`⌫`) cleans trailing operator padding cleanly without corrupting the formula string.

---

## 🧪 Physical Hardware Verification (Realme RMX3998)

| Discount Test (`100 - 10% = ₹ 90`) | Markup Test (`100 + 10% = ₹ 110`) |
| :---: | :---: |
| ![Discount 90](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/26_standard_calc_percentage_verified.png) | ![Markup 110](/home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/27_standard_calc_markup_verified.png) |
