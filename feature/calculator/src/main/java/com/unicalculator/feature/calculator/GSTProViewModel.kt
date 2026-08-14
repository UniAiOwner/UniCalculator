package com.unicalculator.feature.calculator

import androidx.lifecycle.ViewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.words.IndianCurrencyWordConverter
import com.unicalculator.core.math.IndianGSTCalculationEngine
import com.unicalculator.core.model.TaxBreakdown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

data class GSTProUiState(
    val amountInput: String = "",
    val displayAmount: String = "₹ 0.00",
    val selectedGstRate: Int = 18,
    val isCustomRate: Boolean = false,
    val customRateText: String = "",
    val isReverseGst: Boolean = false, // false: +GST (Exclusive), true: -GST (Inclusive)
    val isInterState: Boolean = false, // false: CGST+SGST (Intra), true: IGST (Inter)
    val taxBreakdown: TaxBreakdown? = null,
    val inWordsText: String = "Zero Rupees Only"
)

class GSTProViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GSTProUiState())
    val uiState: StateFlow<GSTProUiState> = _uiState.asStateFlow()

    private var rawAmount = StringBuilder()

    fun onDigit(digit: String) {
        if (rawAmount.isEmpty() && digit == ".") {
            rawAmount.append("0.")
        } else if (digit == "." && rawAmount.contains(".")) {
            return
        } else {
            rawAmount.append(digit)
        }
        recalculateGST()
    }

    fun onDelete() {
        if (rawAmount.isNotEmpty()) {
            rawAmount.deleteCharAt(rawAmount.length - 1)
            recalculateGST()
        }
    }

    fun onClear() {
        rawAmount.clear()
        _uiState.update {
            it.copy(
                amountInput = "",
                displayAmount = "₹ 0.00",
                taxBreakdown = null,
                inWordsText = "Zero Rupees Only"
            )
        }
    }

    fun onSelectSlab(rate: Int) {
        _uiState.update {
            it.copy(
                selectedGstRate = rate,
                isCustomRate = false,
                customRateText = ""
            )
        }
        recalculateGST()
    }

    fun onSetReverseMode(isReverse: Boolean) {
        _uiState.update { it.copy(isReverseGst = isReverse) }
        recalculateGST()
    }

    fun onSetJurisdiction(isInterState: Boolean) {
        _uiState.update { it.copy(isInterState = isInterState) }
        recalculateGST()
    }

    private fun recalculateGST() {
        val amountStr = rawAmount.toString()
        if (amountStr.isEmpty() || amountStr == "0" || amountStr == ".") {
            _uiState.update {
                it.copy(
                    amountInput = amountStr,
                    displayAmount = "₹ 0.00",
                    taxBreakdown = null,
                    inWordsText = "Zero Rupees Only"
                )
            }
            return
        }

        try {
            val amount = amountStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val rate = BigDecimal(_uiState.value.selectedGstRate)

            val breakdown = if (_uiState.value.isReverseGst) {
                IndianGSTCalculationEngine.calculateReverseGST(
                    grossAmount = amount,
                    gstRate = rate,
                    isInterState = _uiState.value.isInterState
                )
            } else {
                IndianGSTCalculationEngine.calculateForwardGST(
                    baseAmount = amount,
                    gstRate = rate,
                    isInterState = _uiState.value.isInterState
                )
            }

            val targetForWords = if (_uiState.value.isReverseGst) {
                breakdown.netBaseAmount
            } else {
                breakdown.grossFinalAmount
            }

            _uiState.update {
                it.copy(
                    amountInput = amountStr,
                    displayAmount = IndianVedicFormatter.formatCurrency(amount, true),
                    taxBreakdown = breakdown,
                    inWordsText = IndianCurrencyWordConverter.convertToWords(targetForWords)
                )
            }
        } catch (_: Exception) {}
    }

    fun generateShareableSummary(): String {
        val state = _uiState.value
        val breakdown = state.taxBreakdown ?: return "UniCalculator GST Summary: ₹ 0.00"
        val rate = state.selectedGstRate
        val mode = if (state.isReverseGst) "Inclusive (-GST / Tax Extracted)" else "Exclusive (+GST / Forward Added)"
        val jur = if (state.isInterState) "Inter-State (IGST)" else "Intra-State (CGST + SGST)"

        return buildString {
            appendLine("🧾 *UniCalculator GST Tax Invoice Summary*")
            appendLine("━━━━━━━━━━━━━━━━━━━━━")
            appendLine("⚙️ Mode: $mode")
            appendLine("🏛️ Jurisdiction: $jur")
            appendLine("📊 GST Slab: $rate%")
            appendLine("━━━━━━━━━━━━━━━━━━━━━")
            appendLine("🏷️ Net Base Amount: ${IndianVedicFormatter.formatCurrency(breakdown.netBaseAmount)}")
            if (state.isInterState) {
                appendLine("🌐 IGST ($rate%): ${IndianVedicFormatter.formatCurrency(breakdown.igstAmount)}")
            } else {
                val half = rate / 2.0
                appendLine("🏛️ CGST (${half}%): ${IndianVedicFormatter.formatCurrency(breakdown.cgstAmount)}")
                appendLine("🏛️ SGST (${half}%): ${IndianVedicFormatter.formatCurrency(breakdown.sgstAmount)}")
            }
            appendLine("━━━━━━━━━━━━━━━━━━━━━")
            appendLine("💰 *Gross Total Amount: ${IndianVedicFormatter.formatCurrency(breakdown.grossFinalAmount)}*")
            appendLine("✍️ In Words: ${state.inWordsText}")
            appendLine("━━━━━━━━━━━━━━━━━━━━━")
            appendLine("Generated via UniCalculator PRO")
        }
    }
}
