package com.unicalculator.feature.cashtally

import androidx.lifecycle.ViewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.words.IndianCurrencyWordConverter
import com.unicalculator.core.model.CashTallyState
import com.unicalculator.core.model.DenominationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

data class CashTallyUiState(
    val state: CashTallyState = CashTallyState(
        denominations = listOf(
            DenominationItem(2000, 0),
            DenominationItem(500, 250),
            DenominationItem(200, 80),
            DenominationItem(100, 150),
            DenominationItem(50, 65),
            DenominationItem(20, 45),
            DenominationItem(10, 50),
            DenominationItem(5, 0),
            DenominationItem(2, 0),
            DenominationItem(1, 0)
        )
    ),
    val totalCashFormatted: String = "₹ 1,84,650.00",
    val totalNotesCount: Int = 412,
    val wordsText: String = "One Lakh Eighty-Four Thousand Six Hundred Fifty Rupees Only",
    val inHindi: Boolean = false
)

class CashTallyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CashTallyUiState())
    val uiState: StateFlow<CashTallyUiState> = _uiState.asStateFlow()

    init {
        recalculateTotals()
    }

    fun updateCount(faceValue: Int, newCount: Int) {
        val count = newCount.coerceAtLeast(0)
        _uiState.update { current ->
            val updated = current.state.denominations.map { item ->
                if (item.faceValue == faceValue) item.copy(count = count) else item
            }
            current.copy(state = current.state.copy(denominations = updated))
        }
        recalculateTotals()
    }

    fun increment(faceValue: Int) {
        val currentItem = _uiState.value.state.denominations.firstOrNull { it.faceValue == faceValue }
        val count = (currentItem?.count ?: 0) + 1
        updateCount(faceValue, count)
    }

    fun decrement(faceValue: Int) {
        val currentItem = _uiState.value.state.denominations.firstOrNull { it.faceValue == faceValue }
        val count = ((currentItem?.count ?: 0) - 1).coerceAtLeast(0)
        updateCount(faceValue, count)
    }

    fun resetAll() {
        _uiState.update { current ->
            val cleared = current.state.denominations.map { it.copy(count = 0) }
            current.copy(state = current.state.copy(denominations = cleared, customCoinsAmount = BigDecimal.ZERO))
        }
        recalculateTotals()
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(inHindi = !it.inHindi) }
        recalculateTotals()
    }

    fun generateWhatsAppSlipText(): String {
        val state = _uiState.value.state
        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("   🧾 ${state.shopName.uppercase()} — CASH TALLY\n")
        sb.append("========================================\n")
        sb.append("📅 Date: ${java.time.LocalDate.now()} | ⏰ Time: ${java.time.LocalTime.now().toString().take(5)}\n\n")
        sb.append("DENOMINATION BREAKDOWN:\n")
        sb.append("----------------------------------------\n")
        state.denominations.filter { it.count > 0 }.forEach { item ->
            sb.append("₹ %-4d x %4d  =  ₹ %,.2f\n".format(item.faceValue, item.count, item.subtotal.toDouble()))
        }
        if (state.customCoinsAmount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Coins         =  ₹ %,.2f\n".format(state.customCoinsAmount.toDouble()))
        }
        sb.append("----------------------------------------\n")
        sb.append("🔢 Total Notes : ${state.totalNotesCount} Pcs\n")
        sb.append("💰 GRAND TOTAL : ${IndianVedicFormatter.formatCurrency(state.grandTotal)}\n")
        sb.append("----------------------------------------\n")
        sb.append("📝 IN WORDS:\n")
        sb.append(IndianCurrencyWordConverter.convertToWords(state.grandTotal, false)).append("\n")
        sb.append("(${IndianCurrencyWordConverter.convertToWords(state.grandTotal, true)})\n")
        sb.append("========================================\n")
        sb.append("✨ Generated via UniCalculator Bharat")
        return sb.toString()
    }

    private fun recalculateTotals() {
        val state = _uiState.value.state
        val total = state.grandTotal
        val words = IndianCurrencyWordConverter.convertToWords(total, _uiState.value.inHindi)
        _uiState.update {
            it.copy(
                totalCashFormatted = IndianVedicFormatter.formatCurrency(total),
                totalNotesCount = state.totalNotesCount,
                wordsText = words
            )
        }
    }
}
