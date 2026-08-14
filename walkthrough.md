# Walkthrough: Standard Calculator vs GST Pro Screen Separation & Clean Zero Initialization

## 🎯 Overview
We have completely separated the **Standard Calculator** and **GST Pro Workspace** into two distinct, specialized screens, eliminated the hardcoded `125000` base amount prefill, and verified both screens on the physical Android hardware device (`Realme RMX3998`).

---

## 🛠️ Changes Implemented

### 1. Standard Calculator Screen (`StandardCalculatorScreen.kt` & `StandardCalculatorViewModel.kt`)
- **Clean Zero State**: Starts fresh at `₹ 0.00` ("Zero Rupees Only") with no dummy 125000 base prefill.
- **Pure General Utility**: Removed all GST tax breakdown plates and GST slab buttons from the Standard screen.
- **Complete Ergonomic Keypad**:
  - Memory operations: `MC`, `MR`, `M-`, `M+`
  - Functional keys: `C`, `⌫`, `%`, `÷` (shifted above numbers `7, 8, 9`)
  - Arithmetic keys: `×`, `−`, `+`, `=` with `+` stacked right above `=`
  - Quick input: `00`, `0`, `.`

### 2. Dedicated GST Pro Workspace (`GSTProScreen.kt` & `GSTProViewModel.kt`)
- **Forward & Reverse Tax Engine**:
  - `+GST (Add Tax / Exclusive)`: Adds tax onto base price.
  - `−GST (Extract Base / Inclusive)`: Extracts net base amount from MRP inclusive total.
- **Jurisdiction Breakdown**:
  - `Intra-State`: Splits into CGST (50%) + SGST (50%).
  - `Inter-State`: Applies 100% IGST.
- **Instant Tax Matrix**: `3% (Gold/Jewellery)`, `5%`, `12%`, `18%`, `28%`.
- **Live Dynamic Invoice Summary Plate**:
  - Net Base Price
  - CGST / SGST / IGST breakdown
  - Total Tax Amount & Total Invoice Amount
  - Real-time Indian currency words ("In Words: Five Rupees Only")
- **Export Utility**: 1-Tap WhatsApp share formatted invoice slip & 1-Tap Copy to clipboard.

### 3. Application Routing (`UniCalculatorApp.kt`)
- `Tab 0` (Standard) ➔ `StandardCalculatorScreen()`
- `Tab 1` (GST Pro) ➔ `GSTProScreen()`
- `Tab 2` (Cash Tally) ➔ `CashTallyScreen()`
- `Tab 3` (Tools) ➔ Business Tools
- `Tab 4` (History) ➔ History Tape

---

## 📱 Hardware Verification & Screenshots

| Screen | Description | Live Hardware Snapshot |
|---|---|---|
| **Standard Calculator** | Clean `₹ 0.00` state with memory row & large 4-column numpad | ![Standard Clean](file:///home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/clean_standard_screen_live.png) |
| **GST Pro (+GST Mode)** | Interactive forward GST with real-time tax calculation and in-words | ![GST Pro Forward](file:///home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/gst_pro_calculated_live.png) |
| **GST Pro (−GST Mode)** | Reverse GST base extraction with dynamic CGST/SGST split | ![GST Pro Reverse](file:///home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/gst_pro_reverse_live.png) |
| **GST Pro (Clean State)** | 1-Tap Clear resetting workstation to `₹ 0.00` | ![GST Pro Clear](file:///home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/gst_pro_cleared_live.png) |
