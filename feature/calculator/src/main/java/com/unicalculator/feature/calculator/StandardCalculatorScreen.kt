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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicLCDWell
import com.unicalculator.core.designsystem.theme.DeleteRed
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

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 5-Row Ergonomic 3D Squircle Keypad Matrix (Zero dead space, tall keys)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: C, ⌫ (Backspace), %, ÷ (Directly Above 7, 8, 9)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton(
                    text = "C",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onClear()
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                    textColor = DeleteRed,
                    fontSize = 24
                )
                NeumorphicButton(
                    text = "⌫",
                    onClick = {
                        hapticEngine.playKeyClick()
                        viewModel.onDelete()
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                    textColor = OperatorOrange,
                    fontSize = 22
                )
                NeumorphicButton(
                    text = "%",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onPercentage()
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                    textColor = OperatorOrange,
                    fontSize = 24
                )
                NeumorphicButton(
                    text = "÷",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onOperator("÷")
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                    textColor = OperatorOrange,
                    fontSize = 26
                )
            }

            // Row 2: 7, 8, 9, ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("7", { hapticEngine.playKeyClick(); viewModel.onDigit("7") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("8", { hapticEngine.playKeyClick(); viewModel.onDigit("8") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("9", { hapticEngine.playKeyClick(); viewModel.onDigit("9") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("×", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("×")
                }, Modifier.weight(1f).height(64.dp), textColor = OperatorOrange, fontSize = 26)
            }

            // Row 3: 4, 5, 6, −
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("4", { hapticEngine.playKeyClick(); viewModel.onDigit("4") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("5", { hapticEngine.playKeyClick(); viewModel.onDigit("5") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("6", { hapticEngine.playKeyClick(); viewModel.onDigit("6") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("−", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("-")
                }, Modifier.weight(1f).height(64.dp), textColor = OperatorOrange, fontSize = 26)
            }

            // Row 4: 1, 2, 3, + (Directly Above =)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("1", { hapticEngine.playKeyClick(); viewModel.onDigit("1") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("2", { hapticEngine.playKeyClick(); viewModel.onDigit("2") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("3", { hapticEngine.playKeyClick(); viewModel.onDigit("3") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton("+", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("+")
                }, Modifier.weight(1f).height(64.dp), textColor = OperatorOrange, fontSize = 26)
            }

            // Row 5: 00, 0, ., = (Solid Rupee Emerald Green Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("00", { hapticEngine.playKeyClick(); viewModel.onDigit("00") }, Modifier.weight(1f).height(64.dp), fontSize = 22)
                NeumorphicButton("0", { hapticEngine.playKeyClick(); viewModel.onDigit("0") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton(".", { hapticEngine.playKeyClick(); viewModel.onDigit(".") }, Modifier.weight(1f).height(64.dp), fontSize = 24)
                NeumorphicButton(
                    text = "=",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onEquals()
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                    isSolidAccent = true,
                    backgroundColor = RupeeEmeraldGreen,
                    textColor = androidx.compose.ui.graphics.Color.White,
                    fontSize = 28
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
