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

data class CalculationTapeItem(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CalculatorUiState(
    val expression: String = "",
    val displayResult: String = "₹ 0",
    val wordsText: String = "Zero Rupees Only",
    val memoryValue: BigDecimal = BigDecimal.ZERO,
    val tapeHistory: List<CalculationTapeItem> = emptyList()
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
                displayResult = "₹ 0",
                wordsText = "Zero Rupees Only"
            )
        }
    }


    fun onClearTape() {
        _uiState.update { it.copy(tapeHistory = emptyList()) }
    }

    fun onTapeRecall(item: CalculationTapeItem) {
        // Strip rupee formatting and set as active input
        val cleanNumber = item.result.replace("₹", "").replace(",", "").trim()
        currentRawInput = StringBuilder(cleanNumber)
        recalculateMath()
    }

    fun onDelete() {
        if (currentRawInput.isNotEmpty()) {
            currentRawInput.deleteCharAt(currentRawInput.length - 1)
            recalculateMath()
        }
    }

    fun onEquals() {
        try {
            val exprToEvaluate = currentRawInput.toString()
            if (exprToEvaluate.isEmpty()) return

            val eval = ShuntingYardEvaluator.evaluate(exprToEvaluate)
            val formattedResult = IndianVedicFormatter.formatCurrency(eval)
            val inWords = IndianCurrencyWordConverter.convertToWords(eval)

            val newTapeItem = CalculationTapeItem(
                expression = exprToEvaluate,
                result = formattedResult
            )

            currentRawInput = StringBuilder(eval.toPlainString())
            _uiState.update {
                it.copy(
                    displayResult = formattedResult,
                    wordsText = inWords,
                    tapeHistory = it.tapeHistory + newTapeItem
                )
            }
        } catch (_: Exception) {}
    }

    fun onPercentage() {
        try {
            val eval = ShuntingYardEvaluator.evaluate(currentRawInput.toString())
            val percentVal = eval.divide(BigDecimal("100"), 4, RoundingMode.HALF_EVEN)
            val formattedResult = IndianVedicFormatter.formatCurrency(percentVal)
            val inWords = IndianCurrencyWordConverter.convertToWords(percentVal)

            val newTapeItem = CalculationTapeItem(
                expression = "${currentRawInput}%",
                result = formattedResult
            )

            currentRawInput = StringBuilder(percentVal.stripTrailingZeros().toPlainString())
            _uiState.update {
                it.copy(
                    expression = currentRawInput.toString(),
                    displayResult = formattedResult,
                    wordsText = inWords,
                    tapeHistory = it.tapeHistory + newTapeItem
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
                        displayResult = "₹ 0",
                        wordsText = "Zero Rupees Only"
                    )
                }
                return
            }
            val raw = currentRawInput.toString()
            val eval = ShuntingYardEvaluator.evaluate(raw)
            var formatted = IndianVedicFormatter.formatCurrency(eval, includeSymbol = true, showDecimalsAlways = false)
            if (raw.endsWith(".") && !formatted.contains(".")) {
                formatted += "."
            }
            _uiState.update {
                it.copy(
                    expression = raw,
                    displayResult = formatted,
                    wordsText = IndianCurrencyWordConverter.convertToWords(eval)
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(expression = currentRawInput.toString()) }
        }
    }
}


