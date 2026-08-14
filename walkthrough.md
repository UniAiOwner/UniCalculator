# Walkthrough: GST Pro Reverse (−GST) Dynamic Calculation & Highlight Fix

## 🎯 Overview
Resolved the issue where clicking **`−GST (Extract Base)`** previously appeared static. Engineered dynamic mode-aware target highlighting and synchronized the Indian In-Words transcription to reflect the extracted **Net Base Amount** in reverse mode.

---

## 🛠️ Changes Implemented

### 1. Dynamic Mode-Aware Receipt Breakdown (`GSTProScreen.kt`)
- **In `+GST (Add Tax)` Mode**:
  - Top Header: `BASE AMOUNT`
  - Tax Row: `CGST` & `SGST` (or `IGST`)
  - Bottom Row: `Total Tax Added` (Left) | **`Total Invoice (Payable):`** (Right highlighted in Emerald Green `RupeeEmeraldGreen`).
- **In `−GST (Extract Base)` Mode**:
  - Top Header: `GROSS / MRP` (Amber `GstSaffronAmber`)
  - Tax Row: `CGST` & `SGST` (or `IGST`)
  - Bottom Row: `Tax Deducted` (Left) | **`Net Base (Excl. Tax):`** (Right highlighted in Saffron Amber `GstSaffronAmber`).

### 2. In-Words Engine Dynamic Target (`GSTProViewModel.kt`)
- Dynamically targets:
  - `breakdown.netBaseAmount` in `−GST` mode.
  - `breakdown.grossFinalAmount` in `+GST` mode.

---

## 📱 Hardware Verification & Comparison

| Mode | Key Highlight | Live Snapshot |
|---|---|---|
| **Forward GST (+GST)** | Total Invoice Payable: `₹ 1,45,63,821.96` | ![Forward GST](file:///media/uniai/UniAi/PROJECTS_MIGRATED/UniCalculator/PLANNING/visuals/gst_pro_forward_verified.png) |
| **Reverse GST (−GST)** | Net Base Extracted: `₹ 1,04,59,510.17` | ![Reverse GST](file:///media/uniai/UniAi/PROJECTS_MIGRATED/UniCalculator/PLANNING/visuals/gst_pro_reverse_verified.png) |
