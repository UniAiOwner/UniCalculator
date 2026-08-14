package com.unicalculator.core.database

import android.content.Context
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalCalculationHistoryRepository(context: Context) {
    private val _historyList = MutableStateFlow<List<CalculationHistoryItem>>(emptyList())
    val historyList: Flow<List<CalculationHistoryItem>> = _historyList.asStateFlow()

    fun insert(item: CalculationHistoryItem) {
        _historyList.update { current ->
            listOf(item.copy(id = System.currentTimeMillis())) + current
        }
    }

    fun clearAll() {
        _historyList.value = emptyList()
    }
}
