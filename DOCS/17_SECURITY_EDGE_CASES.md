# 🛡️ 17. SECURITY, PRIVACY & FINANCIAL EDGE CASES
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. Financial Arithmetic Edge Cases & Solutions

| Edge Case | Risk | Engineered Solution in UniCalculator |
| :--- | :--- | :--- |
| **Floating-Point Imprecision** (`0.1 + 0.2`) | Inaccurate tax returns (`0.30000000000000004`) | Complete `BigDecimal` arithmetic pipeline with strict Banker's rounding (`RoundingMode.HALF_EVEN`). |
| **Division by Zero (`x ÷ 0`)** | App crash (`ArithmeticException`) | Graceful expression interceptor returning localized error state (`"Cannot divide by zero"`). |
| **Infinite Decimal Recurrence (`100 ÷ 3`)** | Buffer overflow | Internal math precision clamped to 10 decimal digits; UI displays 2 decimal places with `...` indicator. |
| **Odd Number GST Split (₹0.01 Tax Split)** | Half-paisa discrepancy between CGST & SGST | Assigns higher fractional paisa to CGST (`CGST = ₹0.01, SGST = ₹0.00`) maintaining `CGST + SGST == Total GST`. |
| **Very Large Number Inputs (Crores / Billions)** | Screen text overflow | Dynamic Auto-scaling TextView (56sp down to 18sp) + Indian Vedic scientific abbreviation toggle (`₹ 48.20 Cr`). |
| **Demonetized ₹2000 Note** | Irrelevant for daily trade | Maintained in Cash Tally with optional visibility toggle in Settings for legacy ledger audits. |

---

## 2. Privacy & Security Architecture

1. **100% Offline-First (Zero Data Outflow)**:
   - Core calculator and cash records require **NO INTERNET PERMISSION** (`android.permission.INTERNET` is excluded).
   - Commercial shop transactions, cash tally ledgers, and profit margins never leave the physical device.
2. **Encrypted Local Storage**:
   - Room SQLite database encrypted using SQLCipher / Android Keystore encrypted preferences for multi-user retail tablet environments.
3. **Biometric Security Lock**:
   - Optional BiometricPrompt (Fingerprint / Face Unlock) to guard daily Cash Tally and revenue logs from unauthorized employee access.
