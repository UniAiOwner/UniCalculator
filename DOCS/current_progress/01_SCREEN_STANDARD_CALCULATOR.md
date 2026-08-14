# 01. Screen: Standard Calculator

## 🎯 Purpose & Utility
The **Standard Calculator** is the foundational entry point for daily commerce. It provides zero-latency arithmetic evaluation, memory functions (`MC`, `MR`, `M−`, `M+`), backspace editing, and real-time Indian Vedic currency formatting.

---

## 📱 Live Physical Hardware Snapshot

![Standard Calculator](visuals/01_screen_standard_calc.png)

---

## 🏗️ UI Layout & Keypad Matrix

### 1. Grand LCD Display Panel (Top)
- **Primary Amount**: Large 36sp dynamic monospace typography with live rupee symbol (`₹`).
- **Live Vedic In-Words Transcription**: Real-time conversion into Indian numbering (e.g. *Zero Rupees Only*, *Twenty Three Thousand Four Hundred Rupees Only*).
- **Recessed 3D Well**: Styled with `NeumorphicShape.CONCAVE` (`cornerRadius = 24.dp`, `elevation = 4.dp`).

### 2. Standard Keypad Grid (5 Rows × 4 Columns)
- **Row 1 (Memory Functions)**: `[ MC ]  [ MR ]  [ M− ]  [ M+ ]` (Soft Slate Grey)
- **Row 2 (Editing & Operations)**: `[ C ] (Red)  [ ⌫ ] (Amber)  [ % ] (Orange)  [ ÷ ] (Orange)`
- **Row 3 (High Digits & Multiply)**: `[ 7 ]  [ 8 ]  [ 9 ]  [ × ] (Orange)`
- **Row 4 (Mid Digits & Subtract)**: `[ 4 ]  [ 5 ]  [ 6 ]  [ − ] (Orange)`
- **Row 5 (Low Digits & Add)**: `[ 1 ]  [ 2 ]  [ 3 ]  [ + ] (Orange)`
- **Row 6 (Bottom Baseline)**: `[ 00 ]  [ 0 ]  [ . ]  [ = ] (Emerald Green)`

---

## 🧮 Mathematical Engine & Precision
- **BigDecimal Precision**: Prevents IEEE 754 floating-point rounding errors (e.g. `0.1 + 0.2 = 0.30` exactly).
- **Instant Operator Precedence**: Supports continuous chained calculations with automatic intermediate evaluation.
