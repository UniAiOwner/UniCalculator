package com.unicalculator.feature.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicLCDWell
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.GstSaffronAmber
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.MemoryGrey
import com.unicalculator.core.designsystem.theme.OperatorOrange
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen

@Composable
fun StandardCalculatorScreen(
    viewModel: StandardCalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val hapticEngine = NeumorphicHapticEngine(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Inset Recessed Neumorphic LCD Well
        NeumorphicLCDWell(
            expressionText = state.expression,
            resultText = state.displayResult,
            wordsText = state.wordsText,
            resultColor = colors.accentEmerald,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Soft Elevated Tax Breakdown Plate (Base | CGST | SGST)
        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val breakdown = state.taxBreakdown
                val base = breakdown?.let { IndianVedicFormatter.formatCurrency(it.netBaseAmount) } ?: "₹ 1,25,000"
                val cgst = breakdown?.let { IndianVedicFormatter.formatCurrency(it.cgstAmount) } ?: "₹ 11,250"
                val sgst = breakdown?.let { IndianVedicFormatter.formatCurrency(it.sgstAmount) } ?: "₹ 11,250"

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Base", fontSize = 12.sp, color = colors.textSecondary)
                    Text(base, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CGST (9%)", fontSize = 12.sp, color = colors.textSecondary)
                    Text(cgst, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SGST (9%)", fontSize = 12.sp, color = colors.textSecondary)
                    Text(sgst, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Glowing Neumorphic GST Slab Pills Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(5, 12, 18, 28).forEach { slab ->
                NeumorphicGstPill(
                    text = "+$slab%",
                    isSelected = state.selectedGstSlab == slab,
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.applyGST(slab)
                    },
                    modifier = Modifier.weight(1f),
                    accentColor = if (slab == 28) GstSaffronAmber else RupeeEmeraldGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. 4x5 3D Squircle Keypad Matrix
        val keys = listOf(
            listOf("MC", "MR", "M-", "M+", "C"),
            listOf("%", "7", "8", "9", "÷"),
            listOf("×", "4", "5", "6", "×"),
            listOf("-", "1", "2", "3", "-"),
            listOf("+", "00", "0", ".", "=")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Memory & Clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton("MC", { hapticEngine.playKeyClick() }, Modifier.weight(1f), textColor = MemoryGrey, fontSize = 16)
                NeumorphicButton("MR", { hapticEngine.playKeyClick() }, Modifier.weight(1f), textColor = MemoryGrey, fontSize = 16)
                NeumorphicButton("M-", { hapticEngine.playKeyClick() }, Modifier.weight(1f), textColor = MemoryGrey, fontSize = 16)
                NeumorphicButton("M+", { hapticEngine.playKeyClick() }, Modifier.weight(1f), textColor = MemoryGrey, fontSize = 16)
                NeumorphicButton("C", {
                    hapticEngine.playOperatorTick()
                    viewModel.onClear()
                }, Modifier.weight(1f), textColor = DeleteRed, fontSize = 18)
            }

            // Row 2: %, 7, 8, 9, ÷
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton("%", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("%")
                }, Modifier.weight(1f), textColor = OperatorOrange, fontSize = 18)
                NeumorphicButton("7", { hapticEngine.playKeyClick(); viewModel.onDigit("7") }, Modifier.weight(1f))
                NeumorphicButton("8", { hapticEngine.playKeyClick(); viewModel.onDigit("8") }, Modifier.weight(1f))
                NeumorphicButton("9", { hapticEngine.playKeyClick(); viewModel.onDigit("9") }, Modifier.weight(1f))
                NeumorphicButton("÷", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("÷")
                }, Modifier.weight(1f), textColor = OperatorOrange, fontSize = 22)
            }

            // Row 3: ×, 4, 5, 6, ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton("×", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("×")
                }, Modifier.weight(1f), textColor = OperatorOrange, fontSize = 22)
                NeumorphicButton("4", { hapticEngine.playKeyClick(); viewModel.onDigit("4") }, Modifier.weight(1f))
                NeumorphicButton("5", { hapticEngine.playKeyClick(); viewModel.onDigit("5") }, Modifier.weight(1f))
                NeumorphicButton("6", { hapticEngine.playKeyClick(); viewModel.onDigit("6") }, Modifier.weight(1f))
                NeumorphicButton("×", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("×")
                }, Modifier.weight(1f), textColor = OperatorOrange, fontSize = 22)
            }

            // Row 4: -, 1, 2, 3, -
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton("-", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("-")
                }, Modifier.weight(1f), textColor = OperatorOrange, fontSize = 22)
                NeumorphicButton("1", { hapticEngine.playKeyClick(); viewModel.onDigit("1") }, Modifier.weight(1f))
                NeumorphicButton("2", { hapticEngine.playKeyClick(); viewModel.onDigit("2") }, Modifier.weight(1f))
                NeumorphicButton("3", { hapticEngine.playKeyClick(); viewModel.onDigit("3") }, Modifier.weight(1f))
                NeumorphicButton("-", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("-")
                }, Modifier.weight(1f), textColor = OperatorOrange, fontSize = 22)
            }

            // Row 5: +, 00, 0, ., =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton("+", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("+")
                }, Modifier.weight(1f), textColor = OperatorOrange, fontSize = 22)
                NeumorphicButton("00", { hapticEngine.playKeyClick(); viewModel.onDigit("00") }, Modifier.weight(1f), fontSize = 18)
                NeumorphicButton("0", { hapticEngine.playKeyClick(); viewModel.onDigit("0") }, Modifier.weight(1f))
                NeumorphicButton(".", { hapticEngine.playKeyClick(); viewModel.onDigit(".") }, Modifier.weight(1f))
                NeumorphicButton("=", {
                    hapticEngine.playOperatorTick()
                    viewModel.onEquals()
                }, Modifier.weight(1f), textColor = RupeeEmeraldGreen, isAccent = true, fontSize = 24)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
