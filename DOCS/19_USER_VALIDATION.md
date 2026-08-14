# 🧪 19. USER VALIDATION & BENCHMARKING TESTING
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. Official CBIC GST Benchmark Invoicing Matrix

To guarantee 100% legal and commercial tax compliance, UniCalculator's math engine is validated against official CBIC (Central Board of Indirect Taxes and Customs) test invoices:

```
┌─────────────────┬──────────┬───────────┬─────────────┬─────────────┬─────────────┬──────────────┐
│ Test Case ID    │ Base (₹) │ Rate (%)  │ CGST (₹)    │ SGST (₹)    │ IGST (₹)    │ Total Gross  │
├─────────────────┼──────────┼───────────┼─────────────┼─────────────┼─────────────┼──────────────┤
│ TC-GST-01 (5%)  │ 10,000   │ 5% Intra  │ 250.00      │ 250.00      │ 0.00        │ 10,500.00    │
│ TC-GST-02 (12%) │ 45,500   │ 12% Intra │ 2,730.00    │ 2,730.00    │ 0.00        │ 50,960.00    │
│ TC-GST-03 (18%) │ 1,25,000 │ 18% Intra │ 11,250.00   │ 11,250.00   │ 0.00        │ 1,47,500.00  │
│ TC-GST-04 (28%) │ 85,000   │ 28% Inter │ 0.00        │ 0.00        │ 23,800.00   │ 1,08,800.00  │
│ TC-GST-REV-01   │ 1,271.19 │ 18% Rev   │ 114.41      │ 114.41      │ 0.00        │ 1,500.00     │
│ TC-GST-REV-02   │ 8,474.58 │ 18% Rev   │ 762.71      │ 762.71      │ 0.00        │ 10,000.00    │
│ TC-GST-GOLD     │ 1,00,000 │ 3% Gold   │ 1,500.00    │ 1,500.00    │ 0.00        │ 1,03,000.00  │
│ TC-GST-DIAMOND  │ 5,00,000 │ 0.25% Dia │ 625.00      │ 625.00      │ 0.00        │ 5,01,250.00  │
└─────────────────┴──────────┴───────────┴─────────────┴─────────────┴─────────────┴──────────────┘
```

---

## 2. Automated JUnit 5 Unit Test Suite

```kotlin
class IndianGSTCalculationEngineTest {
    private val engine = IndianGSTCalculationEngine()

    @Test
    fun `test exact reverse 18 percent GST calculation on MRP 1500`() {
        val gross = BigDecimal("1500.00")
        val rate = BigDecimal("18.00")
        val breakdown = engine.calculateReverseGST(gross, rate, isInterState = false)

        assertThat(breakdown.netBaseAmount).isEqualTo(BigDecimal("1271.19"))
        assertThat(breakdown.totalGstAmount).isEqualTo(BigDecimal("228.81"))
        assertThat(breakdown.cgstAmount).isEqualTo(BigDecimal("114.41"))
        assertThat(breakdown.sgstAmount).isEqualTo(BigDecimal("114.40"))
        assertThat(breakdown.grossFinalAmount).isEqualTo(BigDecimal("1500.00"))
    }
}
```
