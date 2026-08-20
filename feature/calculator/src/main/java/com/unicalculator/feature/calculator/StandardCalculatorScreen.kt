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
    var showSettingsSheet by remember { mutableStateOf(false) }
    var isHistoryShadeExpanded by remember { mutableStateOf(false) }

    viewModel.setHistoryRepository(historyRepo)

    LaunchedEffect(showCurrencySymbol) {
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
                        hapticEngine.playOperatorTick()
                        onNavigateToHistory?.invoke()
                    },
                    iconTint = colors.accentEmerald
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Theme",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        onToggleTheme?.invoke()
                    },
                    iconTint = colors.textSecondary
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    onClick = {
                        hapticEngine.playOperatorTick()
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
                hapticEngine.playOperatorTick()
                viewModel.onSetCursorPosition(pos)
            },
            tapeHistory = state.tapeHistory.map { LCDTapeItem(it.expression, it.result) },
            onTapeItemClick = { item ->
                hapticEngine.playKeyClick()
                viewModel.onTapeRecall(CalculationTapeItem(item.expression, item.result))
            },
            onExpandHistory = {
                hapticEngine.playOperatorTick()
                isHistoryShadeExpanded = true
            },
            resultColor = colors.accentEmerald,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (isHistoryShadeExpanded) {
            NeumorphicPlate(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 24.dp,
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📜 Session Tape (${state.tapeHistory.size})",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.textPrimary
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (state.tapeHistory.isNotEmpty()) {
                                NeumorphicIconButton(
                                    icon = Icons.Outlined.DeleteSweep,
                                    contentDescription = "Clear Session History",
                                    onClick = {
                                        hapticEngine.playOperatorTick()
                                        viewModel.onClearTape()
                                    },
                                    size = 38.dp,
                                    iconTint = DeleteRed
                                )
                            }
                            NeumorphicIconButton(
                                icon = Icons.Outlined.Close,
                                contentDescription = "Close History",
                                onClick = {
                                    hapticEngine.playOperatorTick()
                                    isHistoryShadeExpanded = false
                                },
                                size = 38.dp,
                                iconTint = colors.textSecondary
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = colors.darkShadow.copy(alpha = 0.5f)
                    )

                    if (state.tapeHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No calculations in this session yet.",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    color = colors.textSecondary
                                )
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.tapeHistory.reversed()) { item ->
                                NeumorphicPlate(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            hapticEngine.playKeyClick()
                                            viewModel.onTapeRecall(item)
                                            isHistoryShadeExpanded = false
                                        },
                                    shape = NeumorphicShape.CONCAVE,
                                    cornerRadius = 14.dp,
                                    elevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.expression,
                                            style = TextStyle(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 15.sp,
                                                color = colors.textSecondary
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "= ${item.result}",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = colors.accentEmerald
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                hapticEngine.playOperatorTick()
                                isHistoryShadeExpanded = false
                            }
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowUp,
                                contentDescription = "Collapse",
                                tint = colors.textSecondary
                            )
                            Text(
                                text = "Slide up or tap to return to Keypad",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary
                                )
                            )
                        }
                    }
                }
            }
        } else {
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
                        onClick = { hapticEngine.playOperatorTick(); viewModel.onClear() },
                        modifier = Modifier.weight(1f).height(62.dp),
                        textColor = DeleteRed,
                        fontSize = 24
                    )
                    NeumorphicButton(
                        text = "⌫",
                        onClick = { hapticEngine.playKeyClick(); viewModel.onDelete() },
                        modifier = Modifier.weight(1f).height(62.dp),
                        textColor = OperatorOrange,
                        fontSize = 22
                    )
                    NeumorphicButton(
                        text = "%",
                        onClick = { hapticEngine.playOperatorTick(); viewModel.onPercentage() },
                        modifier = Modifier.weight(1f).height(62.dp),
                        textColor = OperatorOrange,
                        fontSize = 24
                    )
                    NeumorphicButton(
                        text = "÷",
                        onClick = { hapticEngine.playOperatorTick(); viewModel.onOperator("÷") },
                        modifier = Modifier.weight(1f).height(62.dp),
                        textColor = OperatorOrange,
                        fontSize = 26
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeumorphicButton("7", { hapticEngine.playKeyClick(); viewModel.onDigit("7") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("8", { hapticEngine.playKeyClick(); viewModel.onDigit("8") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("9", { hapticEngine.playKeyClick(); viewModel.onDigit("9") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("×", { hapticEngine.playOperatorTick(); viewModel.onOperator("×") }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeumorphicButton("4", { hapticEngine.playKeyClick(); viewModel.onDigit("4") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("5", { hapticEngine.playKeyClick(); viewModel.onDigit("5") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("6", { hapticEngine.playKeyClick(); viewModel.onDigit("6") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("−", { hapticEngine.playOperatorTick(); viewModel.onOperator("-") }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeumorphicButton("1", { hapticEngine.playKeyClick(); viewModel.onDigit("1") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("2", { hapticEngine.playKeyClick(); viewModel.onDigit("2") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("3", { hapticEngine.playKeyClick(); viewModel.onDigit("3") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton("+", { hapticEngine.playOperatorTick(); viewModel.onOperator("+") }, Modifier.weight(1f).height(62.dp), textColor = OperatorOrange, fontSize = 26)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeumorphicButton("00", { hapticEngine.playKeyClick(); viewModel.onDigit("00") }, Modifier.weight(1f).height(62.dp), fontSize = 22)
                    NeumorphicButton("0", { hapticEngine.playKeyClick(); viewModel.onDigit("0") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton(".", { hapticEngine.playKeyClick(); viewModel.onDigit(".") }, Modifier.weight(1f).height(62.dp), fontSize = 24)
                    NeumorphicButton(
                        text = "=",
                        onClick = { hapticEngine.playOperatorTick(); viewModel.onEquals() },
                        modifier = Modifier.weight(1f).height(62.dp),
                        isSolidAccent = true,
                        backgroundColor = RupeeEmeraldGreen,
                        textColor = androidx.compose.ui.graphics.Color.White,
                        fontSize = 28
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
