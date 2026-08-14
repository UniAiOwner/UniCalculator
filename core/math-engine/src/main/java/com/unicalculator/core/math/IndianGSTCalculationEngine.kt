package com.unicalculator.core.math

import com.unicalculator.core.model.TaxBreakdown
import java.math.BigDecimal
import java.math.RoundingMode

object IndianGSTCalculationEngine {
    private val HUNDRED = BigDecimal("100")
    private val TWO = BigDecimal("2")

    fun calculateForwardGST(
        baseAmount: BigDecimal,
        gstRate: BigDecimal,
        cessRate: BigDecimal = BigDecimal.ZERO,
        isInterState: Boolean = false
    ): TaxBreakdown {
        val gstTax = baseAmount.multiply(gstRate).divide(HUNDRED, 2, RoundingMode.HALF_EVEN)
        val cessTax = baseAmount.multiply(cessRate).divide(HUNDRED, 2, RoundingMode.HALF_EVEN)
        val totalTax = gstTax.add(cessTax)
        val grossAmount = baseAmount.add(totalTax)

        val (cgst, sgst, igst) = if (isInterState) {
            Triple(BigDecimal.ZERO, BigDecimal.ZERO, gstTax)
        } else {
            val halfGst = gstTax.divide(TWO, 2, RoundingMode.HALF_EVEN)
            val remSgst = gstTax.subtract(halfGst)
            Triple(halfGst, remSgst, BigDecimal.ZERO)
        }

        return TaxBreakdown(
            netBaseAmount = baseAmount,
            totalGstAmount = totalTax,
            cgstAmount = cgst,
            sgstAmount = sgst,
            igstAmount = igst,
            cessAmount = cessTax,
            grossFinalAmount = grossAmount,
            ratePercentage = gstRate,
            isInterState = isInterState,
            isReverseGst = false
        )
    }

    fun calculateReverseGST(
        grossAmount: BigDecimal,
        gstRate: BigDecimal,
        cessRate: BigDecimal = BigDecimal.ZERO,
        isInterState: Boolean = false
    ): TaxBreakdown {
        val totalFactor = HUNDRED.add(gstRate).add(cessRate)
        val baseAmount = grossAmount.multiply(HUNDRED).divide(totalFactor, 2, RoundingMode.HALF_EVEN)
        val totalTax = grossAmount.subtract(baseAmount)
        val cessTax = baseAmount.multiply(cessRate).divide(HUNDRED, 2, RoundingMode.HALF_EVEN)
        val gstTax = totalTax.subtract(cessTax)

        val (cgst, sgst, igst) = if (isInterState) {
            Triple(BigDecimal.ZERO, BigDecimal.ZERO, gstTax)
        } else {
            val halfGst = gstTax.divide(TWO, 2, RoundingMode.HALF_EVEN)
            val remSgst = gstTax.subtract(halfGst)
            Triple(halfGst, remSgst, BigDecimal.ZERO)
        }

        return TaxBreakdown(
            netBaseAmount = baseAmount,
            totalGstAmount = totalTax,
            cgstAmount = cgst,
            sgstAmount = sgst,
            igstAmount = igst,
            cessAmount = cessTax,
            grossFinalAmount = grossAmount,
            ratePercentage = gstRate,
            isInterState = isInterState,
            isReverseGst = true
        )
    }
}
