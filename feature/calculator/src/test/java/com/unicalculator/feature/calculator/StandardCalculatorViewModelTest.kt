package com.unicalculator.feature.calculator

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StandardCalculatorViewModelTest {

    private lateinit var viewModel: StandardCalculatorViewModel

    @Before
    fun setUp() {
        viewModel = StandardCalculatorViewModel()
    }

    @Test
    fun `test sequential digit typing and default end cursor`() {
        viewModel.onDigit("1")
        viewModel.onDigit("2")
        viewModel.onDigit("0")
        viewModel.onDigit("0")
        viewModel.onDigit("5")

        assertEquals("12005", viewModel.uiState.value.expression)
        assertEquals(5, viewModel.uiState.value.cursorPosition)
    }

    @Test
    fun `test in-place middle digit edit changing 12005 to 12003`() {
        // Type 12005
        "12005".forEach { viewModel.onDigit(it.toString()) }
        assertEquals("12005", viewModel.uiState.value.expression)

        // Set cursor directly after '5' (position 5)
        viewModel.onSetCursorPosition(5)

        // Delete '5' -> expression becomes "1200", cursor becomes 4
        viewModel.onDelete()
        assertEquals("1200", viewModel.uiState.value.expression)
        assertEquals(4, viewModel.uiState.value.cursorPosition)

        // Type '3' -> expression becomes "12003", cursor becomes 5
        viewModel.onDigit("3")
        assertEquals("12003", viewModel.uiState.value.expression)
        assertEquals(5, viewModel.uiState.value.cursorPosition)
    }

    @Test
    fun `test middle operator insertion between numbers`() {
        // Type 12005
        "12005".forEach { viewModel.onDigit(it.toString()) }

        // Move cursor to index 2 (between '2' and '0')
        viewModel.onSetCursorPosition(2)
        assertEquals(2, viewModel.uiState.value.cursorPosition)

        // Insert operator "+"
        viewModel.onOperator("+")
        assertEquals("12 + 005", viewModel.uiState.value.expression)
        assertEquals(5, viewModel.uiState.value.cursorPosition)
    }

    @Test
    fun `test range selection replacement`() {
        // Type 12005
        "12005".forEach { viewModel.onDigit(it.toString()) }

        // Select '5' at index 4..5
        viewModel.onSelectRange(4, 5)
        assertEquals(4, viewModel.uiState.value.selectionStart)
        assertEquals(5, viewModel.uiState.value.selectionEnd)

        // Directly type '3' to overwrite selection
        viewModel.onDigit("3")
        assertEquals("12003", viewModel.uiState.value.expression)
        assertEquals(5, viewModel.uiState.value.cursorPosition)
        assertEquals(-1, viewModel.uiState.value.selectionStart)
    }
}
