package com.unicalculator.feature.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.designsystem.component.LCDTapeItem
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicLCDWell
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.OperatorOrange
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen

import androidx.compose.runtime.LaunchedEffect
import com.unicalculator.core.common.prefs.UniCalculatorPreferences

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
    val hapticEngine = remember { NeumorphicHapticEngine(context) }
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    val showCurrencySymbol by prefs.showCurrencySymbol.collectAsState()
    val numberFormat by prefs.numberFormat.collectAsState()
    val decimalPrecision by prefs.decimalPrecision.collectAsState()
    val hapticIntensity by prefs.hapticIntensity.collectAsState()
    var showSettingsSheet by remember { mutableStateOf(false) }
    var isHistoryShadeExpanded by remember { mutableStateOf(false) }

    fun click() = hapticEngine.playKeyClick(hapticIntensity)
    fun tick() = hapticEngine.playOperatorTick(hapticIntensity)

    viewModel.setHistoryRepository(historyRepo)

    LaunchedEffect(showCurrencySymbol, numberFormat, decimalPrecision) {
        viewModel.setPreferences(prefs)
    }

    if (showSettingsSheet) {
        StandardSettingsSheet(onDismiss = { showSettingsSheet = false })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "UniCalculator",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = colors.textPrimary,
                    letterSpacing = 0.5.sp
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicIconButton(
                    icon = Icons.Outlined.History,
                    contentDescription = "Calculation History",
                    onClick = {
                        tick()
                        onNavigateToHistory?.invoke()
                    },
                    iconTint = colors.accentEmerald
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Theme",
                    onClick = {
                        tick()
                        onToggleTheme?.invoke()
                    },
                    iconTint = colors.textSecondary
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    onClick = {
                        tick()
                        if (onOpenSettings != null) onOpenSettings.invoke() else showSettingsSheet = true
                    },
                    iconTint = colors.textSecondary
                )
            }
        }

        NeumorphicLCDWell(
            expressionText = state.expression,
            resultText = state.displayResult,
            wordsText = state.wordsText,
            cursorPosition = state.cursorPosition,
            selectionStart = state.selectionStart,
            selectionEnd = state.selectionEnd,
            onSetCursorPosition = { pos ->
                tick()
                viewModel.onSetCursorPosition(pos)
            },
            tapeHistory = state.tapeHistory.map { LCDTapeItem(it.expression, it.result) },
            onTapeItemClick = { item ->
                click()
                viewModel.onTapeRecall(CalculationTapeItem(item.expression, item.result))
            },
            resultColor = colors.accentEmerald,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton(
                    text = "C",
                    onClick = { tick(); viewModel.onClear() },
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = DeleteRed,
                    fontSize = 24
                )
                NeumorphicButton(
                    text = "⌫",
                    onClick = { click(); viewModel.onDelete() },
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = OperatorOrange,
                    fontSize = 22
                )
                NeumorphicButton(
                    text = "%",
                    onClick = { tick(); viewModel.onPercentage() },
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = OperatorOrange,
                    fontSize = 24
                )
                NeumorphicButton(
                    text = "÷",
                    onClick = { tick(); viewModel.onOperator("÷") },
                    modifier = Modifier.weight(1f).height(62.dp),
                    textColor = OperatorOrange,
                    fontSize = 26
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("7", { click(); viewModel.onDigit("7") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("8", { click(); viewModel.onDigit("8") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("9", { click(); viewModel.onDigit("9") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("×", { tick(); viewModel.onOperator("×") }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("4", { click(); viewModel.onDigit("4") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("5", { click(); viewModel.onDigit("5") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("6", { click(); viewModel.onDigit("6") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("−", { tick(); viewModel.onOperator("-") }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("1", { click(); viewModel.onDigit("1") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("2", { click(); viewModel.onDigit("2") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("3", { click(); viewModel.onDigit("3") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton("+", { tick(); viewModel.onOperator("+") }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton("00", { click(); viewModel.onDigit("00") }, Modifier.weight(1f).height(62.dp), fontSize = 22)
                NeumorphicButton("0", { click(); viewModel.onDigit("0") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton(".", { click(); viewModel.onDigit(".") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                NeumorphicButton(
                    text = "=",
                    onClick = { tick(); viewModel.onEquals() },
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
