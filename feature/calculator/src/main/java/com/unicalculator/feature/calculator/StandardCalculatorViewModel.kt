package com.unicalculator.feature.calculator

import androidx.lifecycle.ViewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.words.IndianCurrencyWordConverter
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.math.ShuntingYardEvaluator
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

data class CalculationTapeItem(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CalculatorUiState(
    val expression: String = "",
    val cursorPosition: Int = 0,
    val selectionStart: Int = -1,
    val selectionEnd: Int = -1,
    val displayResult: String = "₹ 0",
    val wordsText: String = "Zero Rupees Only",
    val memoryValue: BigDecimal = BigDecimal.ZERO,
    val tapeHistory: List<CalculationTapeItem> = emptyList()
)

class StandardCalculatorViewModel(
    private var historyRepository: LocalCalculationHistoryRepository? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun setHistoryRepository(repo: LocalCalculationHistoryRepository) {
        this.historyRepository = repo
    }

    private var currentRawInput = StringBuilder()
    private var currentCursorPos = 0

    fun onSetCursorPosition(pos: Int) {
        val clamped = pos.coerceIn(0, currentRawInput.length)
        currentCursorPos = clamped
        _uiState.update {
            it.copy(
                cursorPosition = clamped,
                selectionStart = -1,
                selectionEnd = -1
            )
        }
    }

    fun onSelectRange(start: Int, end: Int) {
        val s = start.coerceIn(0, currentRawInput.length)
        val e = end.coerceIn(0, currentRawInput.length)
        if (s < e) {
            currentCursorPos = e
            _uiState.update {
                it.copy(
                    cursorPosition = e,
                    selectionStart = s,
                    selectionEnd = e
                )
            }
        } else {
            onSetCursorPosition(s)
        }
    }

    fun onDigit(digit: String) {
        val state = _uiState.value
        if (state.selectionStart != -1 && state.selectionEnd > state.selectionStart) {
            // Replace selected slice
            currentRawInput.delete(state.selectionStart, state.selectionEnd)
            currentCursorPos = state.selectionStart
            _uiState.update { it.copy(selectionStart = -1, selectionEnd = -1) }
        }

        if (digit == ".") {
            val current = currentRawInput.toString()
            // Check current token around cursor
            val beforeCursor = current.substring(0, currentCursorPos)
            val afterCursor = current.substring(currentCursorPos)
            val tokenBefore = beforeCursor.split(Regex("""[+\-*×/÷−\s]""")).lastOrNull() ?: ""
            val tokenAfter = afterCursor.split(Regex("""[+\-*×/÷−\s]""")).firstOrNull() ?: ""
            if (tokenBefore.contains(".") || tokenAfter.contains(".")) {
                return // Prevent duplicate dot in operand
            }
            if (currentRawInput.isEmpty() || currentCursorPos == 0 || beforeCursor.endsWith(" ")) {
                currentRawInput.insert(currentCursorPos, "0.")
                currentCursorPos += 2
                recalculateMath()
                return
            }
        }

        if (currentRawInput.toString() == "0" && digit != ".") {
            currentRawInput.clear()
            currentCursorPos = 0
        }

        val insertIndex = currentCursorPos.coerceIn(0, currentRawInput.length)
        currentRawInput.insert(insertIndex, digit)
        currentCursorPos = insertIndex + digit.length
        recalculateMath()
    }

    fun onOperator(op: String) {
        if (currentRawInput.isEmpty()) return
        val state = _uiState.value
        if (state.selectionStart != -1 && state.selectionEnd > state.selectionStart) {
            currentRawInput.delete(state.selectionStart, state.selectionEnd)
            currentCursorPos = state.selectionStart
            _uiState.update { it.copy(selectionStart = -1, selectionEnd = -1) }
        }

        val insertIndex = currentCursorPos.coerceIn(0, currentRawInput.length)
        val formattedOp = " $op "

        // If cursor is right after an operator or inside whitespace padding, replace the operator
        val text = currentRawInput.toString()
        val before = text.substring(0, insertIndex)
        val after = text.substring(insertIndex)

        val opBeforeRegex = Regex("""\s*[+\-*×/÷−]\s*$""")
        val matchBefore = opBeforeRegex.find(before)

        if (matchBefore != null) {
            val strippedBefore = before.substring(0, matchBefore.range.first)
            currentRawInput = StringBuilder(strippedBefore).append(formattedOp).append(after)
            currentCursorPos = (strippedBefore + formattedOp).length
        } else {
            currentRawInput.insert(insertIndex, formattedOp)
            currentCursorPos = insertIndex + formattedOp.length
        }

        _uiState.update {
            it.copy(
                expression = currentRawInput.toString(),
                cursorPosition = currentCursorPos
            )
        }
    }

    fun onDelete() {
        val state = _uiState.value
        if (state.selectionStart != -1 && state.selectionEnd > state.selectionStart) {
            currentRawInput.delete(state.selectionStart, state.selectionEnd)
            currentCursorPos = state.selectionStart
            _uiState.update {
                it.copy(
                    selectionStart = -1,
                    selectionEnd = -1,
                    cursorPosition = currentCursorPos
                )
            }
            recalculateMath()
            return
        }

        if (currentCursorPos > 0 && currentRawInput.isNotEmpty()) {
            val text = currentRawInput.toString()
            val before = text.substring(0, currentCursorPos)
            val after = text.substring(currentCursorPos)

            // Check if cursor is right after an operator like " + "
            val opBlockRegex = Regex("""\s*[+\-*×/÷−]\s*$""")
            val match = opBlockRegex.find(before)
            if (match != null && match.range.last == before.length - 1) {
                val newBefore = before.substring(0, match.range.first)
                currentRawInput = StringBuilder(newBefore).append(after)
                currentCursorPos = newBefore.length
            } else {
                currentRawInput.deleteCharAt(currentCursorPos - 1)
                currentCursorPos--
            }
            recalculateMath()
        }
    }

    fun onClear() {
        currentRawInput.clear()
        currentCursorPos = 0
        _uiState.update {
            it.copy(
                expression = "",
                cursorPosition = 0,
                selectionStart = -1,
                selectionEnd = -1,
                displayResult = "₹ 0",
                wordsText = "Zero Rupees Only"
            )
        }
    }

    fun onClearTape() {
        _uiState.update { it.copy(tapeHistory = emptyList()) }
    }

    fun onTapeRecall(item: CalculationTapeItem) {
        val cleanNumber = item.result.replace("₹", "").replace(",", "").trim()
        currentRawInput = StringBuilder(cleanNumber)
        currentCursorPos = currentRawInput.length
        recalculateMath()
    }

    fun onEquals() {
        try {
            val rawExpr = currentRawInput.toString().trim()
            if (rawExpr.isEmpty()) return
            val exprToEvaluate = rawExpr.trimEnd('+', '-', '*', '/', '×', '÷', '%', ' ')
            if (exprToEvaluate.isEmpty()) return

            val eval = ShuntingYardEvaluator.evaluate(exprToEvaluate)
            val formattedResult = IndianVedicFormatter.formatCurrency(eval)
            val inWords = IndianCurrencyWordConverter.convertToWords(eval)

            val newTapeItem = CalculationTapeItem(
                expression = exprToEvaluate,
                result = formattedResult
            )

            historyRepository?.insert(
                CalculationHistoryItem(
                    type = CalculationType.STANDARD_MATH,
                    formulaExpression = exprToEvaluate,
                    primaryResult = formattedResult
                )
            )

            currentRawInput = StringBuilder(eval.stripTrailingZeros().toPlainString())
            currentCursorPos = currentRawInput.length
            _uiState.update {
                it.copy(
                    expression = currentRawInput.toString(),
                    cursorPosition = currentCursorPos,
                    displayResult = formattedResult,
                    wordsText = inWords,
                    tapeHistory = it.tapeHistory + newTapeItem
                )
            }
        } catch (_: Exception) {}
    }

    fun onPercentage() {
        try {
            if (currentRawInput.isEmpty()) return
            val raw = currentRawInput.toString().trim()
            val exprWithPercent = "$raw%"
            val eval = ShuntingYardEvaluator.evaluate(exprWithPercent)
            val formattedResult = IndianVedicFormatter.formatCurrency(eval)
            val inWords = IndianCurrencyWordConverter.convertToWords(eval)

            val newTapeItem = CalculationTapeItem(
                expression = exprWithPercent,
                result = formattedResult
            )

            historyRepository?.insert(
                CalculationHistoryItem(
                    type = CalculationType.STANDARD_MATH,
                    formulaExpression = exprWithPercent,
                    primaryResult = formattedResult
                )
            )

            currentRawInput = StringBuilder(eval.stripTrailingZeros().toPlainString())
            currentCursorPos = currentRawInput.length
            _uiState.update {
                it.copy(
                    expression = currentRawInput.toString(),
                    cursorPosition = currentCursorPos,
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
            val memStr = mem.stripTrailingZeros().toPlainString()
            currentRawInput.insert(currentCursorPos, memStr)
            currentCursorPos += memStr.length
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
                        cursorPosition = 0,
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
                    cursorPosition = currentCursorPos,
                    displayResult = formatted,
                    wordsText = IndianCurrencyWordConverter.convertToWords(eval)
                )
            }
        } catch (_: Exception) {
            _uiState.update {
                it.copy(
                    expression = currentRawInput.toString(),
                    cursorPosition = currentCursorPos
                )
            }
        }
    }
}



