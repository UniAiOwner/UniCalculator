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

    @Test
    fun `test Indian currency words in English and Hindi`() {
        val englishWords = IndianCurrencyWordConverter.convertToWords(BigDecimal("125000.00"), inHindi = false)
        assertEquals("One Lakh Twenty Five Thousand Rupees Only", englishWords)

        val hindiWords = IndianCurrencyWordConverter.convertToWords(BigDecimal("125000.00"), inHindi = true)
        assertEquals("एक लाख पच्चीस हज़ार रुपये मात्र", hindiWords)
    }
}


