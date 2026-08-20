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

import com.unicalculator.core.common.prefs.UniCalculatorPreferences

data class GSTProUiState(
    val amountInput: String = "",
    val cursorPosition: Int = 0,
    val displayAmount: String = "₹ 0.00",
    val selectedGstRate: Int = 18,
    val isCustomRate: Boolean = false,
    val customRateText: String = "",
    val isReverseGst: Boolean = false, // false: +GST (Exclusive), true: -GST (Inclusive)
    val isInterState: Boolean = false, // false: CGST+SGST (Intra), true: IGST (Inter)
    val taxBreakdown: TaxBreakdown? = null,
    val inWordsText: String = "Zero Rupees Only",
    val inWordsHindiText: String = "शून्य रुपये मात्र",
    val isResultEnlarged: Boolean = false
)

class GSTProViewModel(
    private var preferences: UniCalculatorPreferences? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(GSTProUiState())
    val uiState: StateFlow<GSTProUiState> = _uiState.asStateFlow()

    fun setPreferences(prefs: UniCalculatorPreferences) {
        this.preferences = prefs
        _uiState.update {
            it.copy(
                selectedGstRate = prefs.defaultGstRate.value,
                isInterState = prefs.isInterStateDefault.value
            )
        }
        recalculateGST()
    }

    private var rawAmount = StringBuilder()
    private var currentCursorPos = 0

    fun onSetCursorPosition(pos: Int) {
        val clamped = pos.coerceIn(0, rawAmount.length)
        currentCursorPos = clamped
        _uiState.update { it.copy(cursorPosition = clamped) }
    }

    fun onDigit(digit: String) {
        _uiState.update { it.copy(isResultEnlarged = false) }
        val insertIndex = currentCursorPos.coerceIn(0, rawAmount.length)
        if (digit == ".") {
            val text = rawAmount.toString()
            val before = text.substring(0, insertIndex)
            val after = text.substring(insertIndex)
            val tokenBefore = before.split(Regex("""[×÷+\-\s]""")).lastOrNull() ?: ""
            val tokenAfter = after.split(Regex("""[×÷+\-\s]""")).firstOrNull() ?: ""
            if (tokenBefore.contains(".") || tokenAfter.contains(".")) {
                return
            }
            if (rawAmount.isEmpty() || insertIndex == 0 || before.endsWith(" ")) {
                rawAmount.insert(insertIndex, "0.")
                currentCursorPos += 2
                recalculateGST()
                return
            }
        }

        if (rawAmount.toString() == "0" && digit != ".") {
            rawAmount.clear()
            currentCursorPos = 0
        }

        rawAmount.insert(insertIndex, digit)
        currentCursorPos = insertIndex + digit.length
        recalculateGST()
    }

    fun onDelete() {
        _uiState.update { it.copy(isResultEnlarged = false) }
        if (currentCursorPos > 0 && rawAmount.isNotEmpty()) {
            rawAmount.deleteCharAt(currentCursorPos - 1)
            currentCursorPos--
            recalculateGST()
        }
    }

    fun onClear() {
        rawAmount.clear()
        currentCursorPos = 0
        _uiState.update {
            it.copy(
                amountInput = "",
                cursorPosition = 0,
                displayAmount = "₹ 0.00",
                taxBreakdown = null,
                inWordsText = "Zero Rupees Only",
                inWordsHindiText = "शून्य रुपये मात्र",
                isResultEnlarged = false
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { it.copy(isResultEnlarged = false) }
        if (rawAmount.isEmpty()) return
        val lastChar = rawAmount.last()
        if (lastChar == '×' || lastChar == '÷' || lastChar == '+' || lastChar == '-') {
            rawAmount.setCharAt(rawAmount.length - 1, op[0])
        } else {
            rawAmount.append(op)
        }
        recalculateGST()
    }

    fun onEquals() {
        if (_uiState.value.taxBreakdown != null) {
            val evaluated = evaluateExpression(rawAmount.toString())
            rawAmount.clear()
            rawAmount.append(evaluated.stripTrailingZeros().toPlainString())
            recalculateGST()
            _uiState.update { it.copy(isResultEnlarged = true) }
        }
    }

    private fun evaluateExpression(expr: String): BigDecimal {
        if (expr.isEmpty()) return BigDecimal.ZERO
        return try {
            com.unicalculator.core.math.ShuntingYardEvaluator.evaluate(expr)
        } catch (_: Exception) {
            BigDecimal.ZERO
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
            val evaluatedAmount = evaluateExpression(amountStr)
            val rate = BigDecimal(_uiState.value.selectedGstRate)

            val breakdown = if (_uiState.value.isReverseGst) {
                IndianGSTCalculationEngine.calculateReverseGST(
                    grossAmount = evaluatedAmount,
                    gstRate = rate,
                    isInterState = _uiState.value.isInterState
                )
            } else {
                IndianGSTCalculationEngine.calculateForwardGST(
                    baseAmount = evaluatedAmount,
                    gstRate = rate,
                    isInterState = _uiState.value.isInterState
                )
            }

            val targetForWords = if (_uiState.value.isReverseGst) {
                breakdown.netBaseAmount
            } else {
                breakdown.grossFinalAmount
            }

            val headerDisplay = if (amountStr.contains("×") || amountStr.contains("÷")) {
                "$amountStr = ${IndianVedicFormatter.formatCurrency(evaluatedAmount)}"
            } else {
                IndianVedicFormatter.formatCurrency(evaluatedAmount, true)
            }

            val inWordsEn = IndianCurrencyWordConverter.convertToWords(targetForWords, inHindi = false)
            val inWordsHi = IndianCurrencyWordConverter.convertToWords(targetForWords, inHindi = true)

            _uiState.update {
                it.copy(
                    amountInput = amountStr,
                    displayAmount = headerDisplay,
                    taxBreakdown = breakdown,
                    inWordsText = inWordsEn,
                    inWordsHindiText = inWordsHi
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
            appendLine("📊 *UniCalculator GST Calculation Summary*")
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
            appendLine("Calculated via UniCalculator")
        }
    }
}
