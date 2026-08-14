package com.unicalculator.feature.calculator

import androidx.lifecycle.ViewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.words.IndianCurrencyWordConverter
import com.unicalculator.core.math.ShuntingYardEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.RoundingMode

data class CalculatorUiState(
    val expression: String = "",
    val displayResult: String = "₹ 0.00",
    val wordsText: String = "Zero Rupees Only",
    val memoryValue: BigDecimal = BigDecimal.ZERO
)

class StandardCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var currentRawInput = StringBuilder()

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
                wordsText = "Zero Rupees Only"
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

    fun onPercentage() {
        try {
            val eval = ShuntingYardEvaluator.evaluate(currentRawInput.toString())
            val percentVal = eval.divide(BigDecimal("100"), 4, RoundingMode.HALF_EVEN)
            currentRawInput = StringBuilder(percentVal.stripTrailingZeros().toPlainString())
            _uiState.update {
                it.copy(
                    expression = currentRawInput.toString(),
                    displayResult = IndianVedicFormatter.formatCurrency(percentVal),
                    wordsText = IndianCurrencyWordConverter.convertToWords(percentVal)
                )
            }
        } catch (_: Exception) {}
    }

    fun onMemoryClear() {
        _uiState.update { it.copy(memoryValue = BigDecimal.ZERO) }
    }

    fun onMemoryRecall() {
        val mem = _uiState.value.memoryValue
        if (mem != BigDecimal.ZERO) {
            currentRawInput.append(mem.toPlainString())
            recalculateMath()
        }
    }

    fun onMemoryAdd() {
        try {
            val eval = ShuntingYardEvaluator.evaluate(currentRawInput.toString())
            _uiState.update { it.copy(memoryValue = it.memoryValue.add(eval)) }
        } catch (_: Exception) {}
    }

    fun onMemorySubtract() {
        try {
            val eval = ShuntingYardEvaluator.evaluate(currentRawInput.toString())
            _uiState.update { it.copy(memoryValue = it.memoryValue.subtract(eval)) }
        } catch (_: Exception) {}
    }

    private fun recalculateMath() {
        try {
            if (currentRawInput.isEmpty()) {
                _uiState.update {
                    it.copy(
                        expression = "",
                        displayResult = "₹ 0.00",
                        wordsText = "Zero Rupees Only"
                    )
                }
                return
            }
            val eval = ShuntingYardEvaluator.evaluate(currentRawInput.toString())
            _uiState.update {
                it.copy(
                    expression = currentRawInput.toString(),
                    displayResult = IndianVedicFormatter.formatCurrency(eval),
                    wordsText = IndianCurrencyWordConverter.convertToWords(eval)
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(expression = currentRawInput.toString()) }
        }
    }
}

