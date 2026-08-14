package com.unicalculator.feature.calculator

import androidx.lifecycle.ViewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.words.IndianCurrencyWordConverter
import com.unicalculator.core.math.IndianGSTCalculationEngine
import com.unicalculator.core.math.ShuntingYardEvaluator
import com.unicalculator.core.model.TaxBreakdown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

data class CalculatorUiState(
    val expression: String = "",
    val displayResult: String = "₹ 0.00",
    val wordsText: String = "Zero Rupees Only",
    val taxBreakdown: TaxBreakdown? = null,
    val selectedGstSlab: Int? = null,
    val isInterState: Boolean = false,
    val isReverseGst: Boolean = false,
    val memoryValue: BigDecimal = BigDecimal.ZERO
)

class StandardCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var currentRawInput = StringBuilder("125000")

    init {
        // Initialize with default standard mockup value
        applyGST(18)
    }

    fun onDigit(digit: String) {
        if (currentRawInput.toString() == "0" && digit != ".") {
            currentRawInput.clear()
        }
        currentRawInput.append(digit)
        recalculateMath()
    }

    fun onOperator(op: String) {
        if (currentRawInput.isNotEmpty()) {
            currentRawInput.append(" $op ")
            _uiState.update { it.copy(expression = currentRawInput.toString()) }
        }
    }

    fun onClear() {
        currentRawInput.clear()
        _uiState.update {
            it.copy(
                expression = "",
                displayResult = "₹ 0.00",
                wordsText = "Zero Rupees Only",
                taxBreakdown = null,
                selectedGstSlab = null
            )
        }
    }

    fun onDelete() {
        if (currentRawInput.isNotEmpty()) {
            currentRawInput.deleteCharAt(currentRawInput.length - 1)
            recalculateMath()
        }
    }

    fun onEquals() {
        try {
            val eval = ShuntingYardEvaluator.evaluate(currentRawInput.toString())
            currentRawInput = StringBuilder(eval.toPlainString())
            _uiState.update {
                it.copy(
                    displayResult = IndianVedicFormatter.formatCurrency(eval),
                    wordsText = IndianCurrencyWordConverter.convertToWords(eval)
                )
            }
        } catch (_: Exception) {}
    }

    fun applyGST(rate: Int) {
        try {
            val rawNum = currentRawInput.toString().replace(" ", "").split("+", "-", "*", "/", "×", "÷").lastOrNull()
            val amount = rawNum?.toBigDecimalOrNull() ?: BigDecimal("125000")
            val gstRate = BigDecimal(rate)

            val breakdown = if (_uiState.value.isReverseGst) {
                IndianGSTCalculationEngine.calculateReverseGST(amount, gstRate, isInterState = _uiState.value.isInterState)
            } else {
                IndianGSTCalculationEngine.calculateForwardGST(amount, gstRate, isInterState = _uiState.value.isInterState)
            }

            _uiState.update {
                it.copy(
                    expression = "${IndianVedicFormatter.formatCurrency(amount, false)} + $rate% GST (${if (it.isInterState) "Inter-State" else "Intra-State"})",
                    displayResult = IndianVedicFormatter.formatCurrency(breakdown.grossFinalAmount),
                    wordsText = IndianCurrencyWordConverter.convertToWords(breakdown.grossFinalAmount),
                    taxBreakdown = breakdown,
                    selectedGstSlab = rate
                )
            }
        } catch (_: Exception) {}
    }

    fun toggleJurisdiction() {
        _uiState.update { it.copy(isInterState = !it.isInterState) }
        _uiState.value.selectedGstSlab?.let { applyGST(it) }
    }

    fun toggleReverseGst() {
        _uiState.update { it.copy(isReverseGst = !it.isReverseGst) }
        _uiState.value.selectedGstSlab?.let { applyGST(it) }
    }

    private fun recalculateMath() {
        try {
            val eval = ShuntingYardEvaluator.evaluate(currentRawInput.toString())
            _uiState.update {
                it.copy(
                    expression = currentRawInput.toString(),
                    displayResult = IndianVedicFormatter.formatCurrency(eval),
                    wordsText = IndianCurrencyWordConverter.convertToWords(eval)
                )
            }
        } catch (_: Exception) {}
    }
}
