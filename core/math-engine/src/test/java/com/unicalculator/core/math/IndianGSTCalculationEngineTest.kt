package com.unicalculator.core.math

import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.words.IndianCurrencyWordConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class IndianGSTCalculationEngineTest {
    @Test
    fun `test forward 18 percent GST on 125000`() {
        val base = BigDecimal("125000.00")
        val rate = BigDecimal("18.00")
        val result = IndianGSTCalculationEngine.calculateForwardGST(base, rate, isInterState = false)

        assertEquals(BigDecimal("125000.00"), result.netBaseAmount)
        assertEquals(BigDecimal("22500.00"), result.totalGstAmount)
        assertEquals(BigDecimal("11250.00"), result.cgstAmount)
        assertEquals(BigDecimal("11250.00"), result.sgstAmount)
        assertEquals(BigDecimal("147500.00"), result.grossFinalAmount)
    }

    @Test
    fun `test reverse 18 percent GST on MRP 1500`() {
        val gross = BigDecimal("1500.00")
        val rate = BigDecimal("18.00")
        val result = IndianGSTCalculationEngine.calculateReverseGST(gross, rate, isInterState = false)

        assertEquals(BigDecimal("1271.19"), result.netBaseAmount)
        assertEquals(BigDecimal("228.81"), result.totalGstAmount)
        assertEquals(BigDecimal("114.40"), result.cgstAmount)
        assertEquals(BigDecimal("114.41"), result.sgstAmount)
        assertEquals(BigDecimal("1500.00"), result.grossFinalAmount)
    }

    @Test
    fun `test shunting yard math evaluation`() {
        val result = ShuntingYardEvaluator.evaluate("10 + 20 * 3")
        assertEquals(BigDecimal("70.00"), result)

        val bracketResult = ShuntingYardEvaluator.evaluate("(10 + 20) * 3")
        assertEquals(BigDecimal("90.00"), bracketResult)
    }

    @Test
    fun `test commercial percentage discount 100 minus 10 percent`() {
        val result = ShuntingYardEvaluator.evaluate("100 - 10%")
        assertEquals(BigDecimal("90.00"), result)
    }

    @Test
    fun `test commercial percentage markup 100 plus 10 percent`() {
        val result = ShuntingYardEvaluator.evaluate("100 + 10%")
        assertEquals(BigDecimal("110.00"), result)
    }

    @Test
    fun `test commercial percentage portion 100 times 10 percent`() {
        val result = ShuntingYardEvaluator.evaluate("100 × 10%")
        assertEquals(BigDecimal("10.00"), result)
    }

    @Test
    fun `test commercial percentage ratio 100 div 10 percent`() {
        val result = ShuntingYardEvaluator.evaluate("100 ÷ 10%")
        assertEquals(BigDecimal("1000.00"), result)
    }

    @Test
    fun `test standalone percentage 50 percent`() {
        val result = ShuntingYardEvaluator.evaluate("50%")
        assertEquals(BigDecimal("0.50"), result)
    }

    @Test
    fun `test commercial cumulative running total 900 plus 100 minus 10 percent`() {
        val result = ShuntingYardEvaluator.evaluate("900 + 100 - 10%")
        assertEquals(BigDecimal("900.00"), result)
    }

    @Test
    fun `test commercial chained discount 1000 minus 10 percent minus 10 percent`() {
        val result = ShuntingYardEvaluator.evaluate("1000 - 10% - 10%")
        assertEquals(BigDecimal("810.00"), result)
    }

    @Test
    fun `test commercial margin and markup calculation`() {
        val cp = BigDecimal("1200.00")
        val sp = BigDecimal("1600.00")
        val result = CommercialCalculatorEngine.calculateMarginMarkup(cp, sp)

        assertEquals(BigDecimal("400.00"), result.grossProfit)
        assertEquals(BigDecimal("25.00"), result.profitMarginPercent)
        assertEquals(BigDecimal("33.33"), result.markupPercent)
    }

    @Test
    fun `test loan emi calculation 5 lakh at 8_5 percent for 36 months`() {
        val principal = BigDecimal("500000.00")
        val rate = BigDecimal("8.50")
        val tenure = 36
        val result = CommercialCalculatorEngine.calculateLoanEmi(principal, rate, tenure)

        assertTrue(result.monthlyEmi > BigDecimal("15000.00"))
        assertTrue(result.totalPayment > principal)
        assertEquals(result.totalPayment.subtract(principal), result.totalInterest)
    }

    @Test
    fun `test Indian Vedic currency formatting`() {
        val formatted = IndianVedicFormatter.formatCurrency(BigDecimal("123456789.50"), includeSymbol = true)
        assertEquals("₹ 12,34,56,789.50", formatted)
    }

    @Test(expected = ArithmeticException::class)
    fun `test divide by zero throws ArithmeticException`() {
        ShuntingYardEvaluator.evaluate("10 / 0")
    }

    @Test
    fun `test Indian currency words with Paise in English and Hindi`() {
        val englishWords = IndianCurrencyWordConverter.convertToWords(BigDecimal("1250.75"), inHindi = false)
        assertEquals("One Thousand Two Hundred Fifty Rupees and Seventy Five Paise Only", englishWords)

        val hindiWords = IndianCurrencyWordConverter.convertToWords(BigDecimal("1250.75"), inHindi = true)
        assertEquals("एक हज़ार दो सौ पचास रुपये पचहत्तर पैसे मात्र", hindiWords)
    }

    @Test
    fun `test Indian currency words large amount 50 Crore and 5 Arab`() {
        val wordsFiftyCrore = IndianCurrencyWordConverter.convertToWords(BigDecimal("500000000.00"), inHindi = false)
        assertEquals("Fifty Crore Rupees Only", wordsFiftyCrore)

        val wordsFiveArab = IndianCurrencyWordConverter.convertToWords(BigDecimal("5000000000.00"), inHindi = false)
        assertEquals("Five Arab Rupees Only", wordsFiveArab)
    }

    @Test
    fun `test successive festive discount 50 percent plus 20 percent on 1000`() {
        val (finalPrice, savings) = CommercialCalculatorEngine.calculateSuccessiveDiscount(
            originalPrice = BigDecimal("1000.00"),
            discounts = listOf(BigDecimal("50.00"), BigDecimal("20.00"))
        )
        assertEquals(BigDecimal("400.00"), finalPrice)
        assertEquals(BigDecimal("600.00"), savings)
    }

    @Test
    fun `test Casio reverse margin solver CP 800 with 20 percent margin gives SP 1000`() {
        val targetSP = CommercialCalculatorEngine.calculateTargetSellingPrice(
            costPrice = BigDecimal("800.00"),
            desiredMarginPercent = BigDecimal("20.00")
        )
        assertEquals(BigDecimal("1000.00"), targetSP)
    }

    @Test
    fun `test Mandi and Jewellery unit conversions`() {
        // 5 Quintal = 500 kg
        val kg = UnitConversionEngine.convertMass(BigDecimal("5.0"), "Quintal (q)", "Kilogram (kg)")
        assertEquals(0, kg.compareTo(BigDecimal("500")))

        // 1 Tola Vedic to Grams
        val grams = UnitConversionEngine.convertMass(BigDecimal("1.0"), "Tola (Vedic 11.66g)", "Gram (g)")
        assertEquals(0, grams.compareTo(BigDecimal("11.6638")))

        // 1 Acre = 40 Guntha
        val guntha = UnitConversionEngine.convertArea(BigDecimal("1.0"), "Acre", "Guntha")
        assertTrue(guntha.toDouble() >= 39.9 && guntha.toDouble() <= 40.1)
    }
}


