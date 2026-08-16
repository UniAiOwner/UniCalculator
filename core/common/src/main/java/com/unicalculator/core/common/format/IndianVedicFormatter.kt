package com.unicalculator.core.common.format

import java.math.BigDecimal
import java.math.RoundingMode

object IndianVedicFormatter {
    fun formatCurrency(
        amount: BigDecimal,
        includeSymbol: Boolean = true,
        showDecimalsAlways: Boolean = false
    ): String {
        val symbol = if (includeSymbol) "₹ " else ""
        val isNegative = amount.signum() < 0
        val isInteger = amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0

        val absAmount = if (showDecimalsAlways || !isInteger) {
            amount.abs().setScale(2, RoundingMode.HALF_EVEN)
        } else {
            amount.abs().setScale(0, RoundingMode.UNNECESSARY)
        }

        val rawStr = absAmount.toPlainString()
        val parts = rawStr.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1 && (showDecimalsAlways || !isInteger)) {
            "." + parts[1]
        } else {
            ""
        }

        if (integerPart.length <= 3) {
            return "$symbol${if (isNegative) "-" else ""}$integerPart$decimalPart"
        }

        val lastThree = integerPart.substring(integerPart.length - 3)
        val remaining = integerPart.substring(0, integerPart.length - 3)
        val formattedRemaining = remaining.reversed().chunked(2).joinToString(",").reversed()

        return "$symbol${if (isNegative) "-" else ""}$formattedRemaining,$lastThree$decimalPart"
    }

    fun formatPlainNumber(amount: BigDecimal): String {
        val absAmount = amount.stripTrailingZeros()
        return if (absAmount.scale() <= 0) {
            absAmount.toPlainString()
        } else {
            amount.setScale(2, RoundingMode.HALF_EVEN).toPlainString()
        }
    }
}

