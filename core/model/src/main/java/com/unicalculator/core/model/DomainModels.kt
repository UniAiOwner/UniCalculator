package com.unicalculator.core.model

import java.math.BigDecimal

data class TaxBreakdown(
    val netBaseAmount: BigDecimal,
    val totalGstAmount: BigDecimal,
    val cgstAmount: BigDecimal,
    val sgstAmount: BigDecimal,
    val igstAmount: BigDecimal,
    val cessAmount: BigDecimal,
    val grossFinalAmount: BigDecimal,
    val ratePercentage: BigDecimal,
    val isInterState: Boolean = false,
    val isReverseGst: Boolean = false
)

enum class CalculationType {
    STANDARD_MATH,
    GST_FORWARD,
    GST_REVERSE,
    CASH_TALLY,
    MARGIN_MARKUP,
    DISCOUNT_STACK,
    LOAN_EMI
}

data class DenominationItem(
    val faceValue: Int,
    val count: Int = 0,
    val isCoin: Boolean = false
) {
    val subtotal: BigDecimal
        get() = BigDecimal(faceValue).multiply(BigDecimal(count))
}

data class CashTallyState(
    val denominations: List<DenominationItem> = defaultDenominations(),
    val customCoinsAmount: BigDecimal = BigDecimal.ZERO,
    val shopName: String = "My Store"
) {
    val totalNotesCount: Int
        get() = denominations.filter { !it.isCoin }.sumOf { it.count }

    val grandTotal: BigDecimal
        get() = denominations.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.subtotal) }
            .add(customCoinsAmount)

    companion object {
        fun defaultDenominations(): List<DenominationItem> = listOf(
            DenominationItem(2000),
            DenominationItem(500),
            DenominationItem(200),
            DenominationItem(100),
            DenominationItem(50),
            DenominationItem(20),
            DenominationItem(10),
            DenominationItem(5),
            DenominationItem(2),
            DenominationItem(1)
        )
    }
}

data class CalculationHistoryItem(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: CalculationType,
    val formulaExpression: String,
    val primaryResult: String,
    val netBaseAmount: String? = null,
    val totalTaxAmount: String? = null,
    val cgstAmount: String? = null,
    val sgstAmount: String? = null,
    val igstAmount: String? = null,
    val memoNote: String? = null,
    val isPinned: Boolean = false
)

data class MarginMarkupResult(
    val costPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val grossProfit: BigDecimal,
    val profitMarginPercent: BigDecimal,
    val markupPercent: BigDecimal
)

data class LoanEmiResult(
    val principalAmount: BigDecimal,
    val annualInterestRate: BigDecimal,
    val tenureMonths: Int,
    val monthlyEmi: BigDecimal,
    val totalInterest: BigDecimal,
    val totalPayment: BigDecimal
)
