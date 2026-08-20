package com.unicalculator.core.common.format

import com.unicalculator.core.common.prefs.NumberFormatStyle
import java.math.BigDecimal
import java.math.RoundingMode

object IndianVedicFormatter {
    fun formatCurrency(
        amount: BigDecimal,
        includeSymbol: Boolean = true,
        showDecimalsAlways: Boolean = false,
        formatStyle: NumberFormatStyle = NumberFormatStyle.INDIAN_VEDIC,
        decimalPrecision: Int = -1
    ): String {
        val symbol = if (includeSymbol) "₹ " else ""
        val isNegative = amount.signum() < 0
        val isInteger = amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0

        val scaledAmount = when {
            decimalPrecision >= 0 -> amount.abs().setScale(decimalPrecision, RoundingMode.HALF_EVEN)
            showDecimalsAlways || !isInteger -> amount.abs().setScale(2, RoundingMode.HALF_EVEN)
            else -> amount.abs().setScale(0, RoundingMode.UNNECESSARY)
        }

        val rawStr = scaledAmount.toPlainString()
        val parts = rawStr.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1 && (decimalPrecision > 0 || (decimalPrecision == -1 && (showDecimalsAlways || !isInteger)))) {
            "." + parts[1]
        } else {
            ""
        }

        val formattedInteger = when (formatStyle) {
            NumberFormatStyle.INDIAN_VEDIC -> formatIndianGrouping(integerPart)
            NumberFormatStyle.INTERNATIONAL_WESTERN -> formatWesternGrouping(integerPart)
        }

        return "$symbol${if (isNegative) "-" else ""}$formattedInteger$decimalPart"
    }

    private fun formatIndianGrouping(integerPart: String): String {
        if (integerPart.length <= 3) return integerPart
        val lastThree = integerPart.substring(integerPart.length - 3)
        val remaining = integerPart.substring(0, integerPart.length - 3)
        val formattedRemaining = remaining.reversed().chunked(2).joinToString(",").reversed()
        return "$formattedRemaining,$lastThree"
    }

    private fun formatWesternGrouping(integerPart: String): String {
        if (integerPart.length <= 3) return integerPart
        return integerPart.reversed().chunked(3).joinToString(",").reversed()
    }

    fun formatPlainNumber(amount: BigDecimal, decimalPrecision: Int = -1): String {
        return if (decimalPrecision >= 0) {
            amount.setScale(decimalPrecision, RoundingMode.HALF_EVEN).toPlainString()
        } else {
            val absAmount = amount.stripTrailingZeros()
            if (absAmount.scale() <= 0) {
                absAmount.toPlainString()
            } else {
                amount.setScale(2, RoundingMode.HALF_EVEN).toPlainString()
            }
        }
    }
}

