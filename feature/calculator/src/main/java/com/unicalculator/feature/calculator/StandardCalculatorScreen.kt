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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.designsystem.component.LCDTapeItem
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicLCDWell
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.OperatorOrange
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen

@Composable
fun StandardCalculatorScreen(
    viewModel: StandardCalculatorViewModel = viewModel(),
    onNavigateToHistory: (() -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Top Action Bar: App Title (Left) + 3 Neumorphic Action Buttons (Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "UniCalculator",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colors.textPrimary,
                    letterSpacing = 0.5.sp
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: History Tape
                NeumorphicIconButton(
                    icon = Icons.Outlined.History,
                    contentDescription = "Calculation History",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        onNavigateToHistory?.invoke()
                    },
                    iconTint = colors.accentEmerald
                )

                // Button 2: Light / Dark Theme
                NeumorphicIconButton(
                    icon = Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Theme",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        onToggleTheme?.invoke()
                    },
                    iconTint = colors.textSecondary
                )

                // Button 3: Settings
                NeumorphicIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        onOpenSettings?.invoke()
                    },
                    iconTint = colors.textSecondary
                )
            }
        }

        // 2. Inset Recessed Neumorphic LCD Well with Scrollable Calculation Tape
        NeumorphicLCDWell(
            expressionText = state.expression,
            resultText = state.displayResult,
            wordsText = state.wordsText,
            tapeHistory = state.tapeHistory.map { LCDTapeItem(it.expression, it.result) },
            onTapeItemClick = { item ->
                hapticEngine.playKeyClick()
                viewModel.onTapeRecall(CalculationTapeItem(item.expression, item.result))
            },
            resultColor = colors.accentEmerald,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. 5-Row Ergonomic 3D Squircle Keypad Matrix
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
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = DeleteRed,
                    fontSize = 24
                )
                NeumorphicButton(
                    text = "⌫",
                    onClick = {
                        hapticEngine.playKeyClick()
                        viewModel.onDelete()
                    },
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = OperatorOrange,
                    fontSize = 22
                )
                NeumorphicButton(
                    text = "%",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onPercentage()
                    },
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = OperatorOrange,
                    fontSize = 24
                )
                NeumorphicButton(
                    text = "÷",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onOperator("÷")
                    },
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = OperatorOrange,
                    fontSize = 26
                )
            }

            // Row 2: 7, 8, 9, ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("7", { hapticEngine.playKeyClick(); viewModel.onDigit("7") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("8", { hapticEngine.playKeyClick(); viewModel.onDigit("8") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("9", { hapticEngine.playKeyClick(); viewModel.onDigit("9") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("×", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("×")
                }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
            }

            // Row 3: 4, 5, 6, −
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("4", { hapticEngine.playKeyClick(); viewModel.onDigit("4") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("5", { hapticEngine.playKeyClick(); viewModel.onDigit("5") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("6", { hapticEngine.playKeyClick(); viewModel.onDigit("6") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("−", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("-")
                }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
            }

            // Row 4: 1, 2, 3, + (Directly Above =)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("1", { hapticEngine.playKeyClick(); viewModel.onDigit("1") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("2", { hapticEngine.playKeyClick(); viewModel.onDigit("2") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("3", { hapticEngine.playKeyClick(); viewModel.onDigit("3") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("+", {
                    hapticEngine.playOperatorTick()
                    viewModel.onOperator("+")
                }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
            }

            // Row 5: 00, 0, ., = (Solid Rupee Emerald Green Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("00", { hapticEngine.playKeyClick(); viewModel.onDigit("00") }, Modifier.weight(1f).height(62.dp), fontSize = 22)
                NeumorphicButton("0", { hapticEngine.playKeyClick(); viewModel.onDigit("0") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton(".", { hapticEngine.playKeyClick(); viewModel.onDigit(".") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton(
                    text = "=",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onEquals()
                    },
                    modifier = Modifier.weight(1f).height(62.dp),
                    isSolidAccent = true,
                    backgroundColor = RupeeEmeraldGreen,
                    textColor = androidx.compose.ui.graphics.Color.White,
                    fontSize = 28
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

