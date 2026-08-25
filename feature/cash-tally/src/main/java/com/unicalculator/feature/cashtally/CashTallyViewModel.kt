package com.unicalculator.feature.cashtally

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.words.IndianCurrencyWordConverter
import com.unicalculator.core.common.words.WordsLanguage
import com.unicalculator.core.model.CashTallyState
import com.unicalculator.core.model.DenominationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class CashTallyUiState(
    val state: CashTallyState = CashTallyState(
        denominations = listOf(
            DenominationItem(500, 320),
            DenominationItem(200, 0),
            DenominationItem(100, 150),
            DenominationItem(50, 65),
            DenominationItem(20, 95),
            DenominationItem(10, 0),
            DenominationItem(5, 0),
            DenominationItem(2, 0),
            DenominationItem(1, 0)
        )
    ),
    val totalCashFormatted: String = "₹ 1,60,650.00",
    val totalNotesCount: Int = 640,
    val totalPacketsCount: Int = 6,
    val totalBundlesCount: Int = 0,
    val looseNotesCount: Int = 40,
    val wordsEnglishText: String = "One Lakh Sixty Thousand Six Hundred Fifty Rupees Only",
    val wordsHindiText: String = "",
    val wordsText: String = "One Lakh Sixty Thousand Six Hundred Fifty Rupees Only",
    val inHindi: Boolean = false,
    val highestDenom: Int = 500,
    val lowestDenom: Int = 20
)

class CashTallyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CashTallyUiState())
    val uiState: StateFlow<CashTallyUiState> = _uiState.asStateFlow()

    private var preferences: com.unicalculator.core.common.prefs.UniCalculatorPreferences? = null

    fun setPreferences(prefs: com.unicalculator.core.common.prefs.UniCalculatorPreferences) {
        this.preferences = prefs
        viewModelScope.launch {
            prefs.wordsLanguage.collect {
                recalculateTotals()
            }
        }
        recalculateTotals()
    }

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

    fun clearDenomination(faceValue: Int) {
        updateCount(faceValue, 0)
    }

    fun addCustomDenomination(faceValue: Int) {
        if (faceValue <= 0) return
        _uiState.update { current ->
            if (current.state.denominations.any { it.faceValue == faceValue }) {
                current
            } else {
                val updated = (current.state.denominations + DenominationItem(faceValue, 0))
                    .sortedByDescending { it.faceValue }
                current.copy(state = current.state.copy(denominations = updated))
            }
        }
        recalculateTotals()
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
        val firm = preferences?.firmName?.value?.takeIf { it.isNotBlank() } ?: state.shopName.ifBlank { "UniAi Retail Store" }
        val cashier = preferences?.cashierName?.value?.takeIf { it.isNotBlank() } ?: "Counter Master"
        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("   🧾 ${firm.uppercase()} — CASH TALLY\n")
        sb.append("========================================\n")
        sb.append("📅 Date: ${java.time.LocalDate.now()} | ⏰ Time: ${java.time.LocalTime.now().toString().take(5)}\n")
        sb.append("👤 Cashier: $cashier\n\n")
        sb.append("DENOMINATION BREAKDOWN:\n")
        sb.append("----------------------------------------\n")
        state.denominations.filter { it.count > 0 }.forEach { item ->
            sb.append("₹ %-4d x %4d = ₹ %,.2f\n".format(item.faceValue, item.count, item.subtotal.toDouble()))
        }
        if (state.customCoinsAmount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Coins                   = ₹ %,.2f\n".format(state.customCoinsAmount.toDouble()))
        }
        sb.append("----------------------------------------\n")
        sb.append("🔢 Total Notes : ${state.totalNotesCount} Pcs\n")
        sb.append("💰 GRAND TOTAL : ${IndianVedicFormatter.formatCurrency(state.grandTotal)}\n")
        sb.append("----------------------------------------\n")
        val wordsLang = preferences?.wordsLanguage?.value ?: WordsLanguage.ENGLISH
        val inWords = IndianCurrencyWordConverter.convert(state.grandTotal, language = wordsLang, includeRupeesSuffix = true)
        if (inWords.isNotBlank()) {
            sb.append("📝 IN WORDS:\n")
            sb.append(inWords).append("\n")
        }
        sb.append("========================================\n")
        sb.append("✨ Generated via UniCalculator • UniCore Technologies")
        return sb.toString()
    }

    private fun recalculateTotals() {
        val state = _uiState.value.state
        val total = state.grandTotal
        val wordsLang = preferences?.wordsLanguage?.value ?: WordsLanguage.ENGLISH
        val words = IndianCurrencyWordConverter.convert(total, language = wordsLang, includeRupeesSuffix = true)
        val activeDenoms = state.denominations.filter { it.count > 0 }
        val highest = activeDenoms.maxOfOrNull { it.faceValue } ?: 0
        val lowest = activeDenoms.minOfOrNull { it.faceValue } ?: 0
        val totalPackets = state.denominations.sumOf { it.count / 100 }
        val totalBundles = state.denominations.sumOf { it.count / 1000 }
        val looseNotes = state.denominations.sumOf { it.count % 100 }

        _uiState.update {
            it.copy(
                totalCashFormatted = IndianVedicFormatter.formatCurrency(total),
                totalNotesCount = state.totalNotesCount,
                totalPacketsCount = totalPackets,
                totalBundlesCount = totalBundles,
                looseNotesCount = looseNotes,
                wordsEnglishText = words,
                wordsHindiText = "",
                wordsText = words,
                highestDenom = highest,
                lowestDenom = lowest
            )
        }
    }
}
