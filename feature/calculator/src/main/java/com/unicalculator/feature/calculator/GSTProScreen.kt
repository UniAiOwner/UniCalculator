package com.unicalculator.feature.calculator

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.component.NeumorphicSlideSwitch
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.GstSaffronAmber
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.MemoryGrey
import com.unicalculator.core.designsystem.theme.OperatorOrange
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType

private val SapphireBlue = Color(0xFF2980B9)

@Composable
fun GSTProScreen(
    onNavigateToHistory: (() -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    viewModel: GSTProViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val hapticEngine = NeumorphicHapticEngine(context)
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val breakdown = state.taxBreakdown
    val netBaseStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.netBaseAmount) } ?: "₹ 0.00"
    val cgstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.cgstAmount) } ?: "₹ 0.00"
    val sgstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.sgstAmount) } ?: "₹ 0.00"
    val igstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.igstAmount) } ?: "₹ 0.00"
    val totalTaxStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.totalGstAmount) } ?: "₹ 0.00"
    val grossFinalStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.grossFinalAmount) } ?: "₹ 0.00"

    if (showSettingsSheet) {
        GSTProSettingsSheet(onDismiss = { showSettingsSheet = false })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 0. Top Action Bar: Screen Title (Left) + 3 Neumorphic Action Buttons (Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GST Pro",
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
                NeumorphicIconButton(
                    icon = Icons.Outlined.History,
                    contentDescription = "GST History",
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

        // 1. DUAL-DISPLAY NEUMORPHIC WELL & COMMERCIAL TAX BREAKDOWN
        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONCAVE,
            cornerRadius = 16.dp,
            elevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Top Row: Interactive Amount Input Well
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.isReverseGst) "TOTAL GROSS AMOUNT (INCL. TAX):" else "NET BASE AMOUNT (EXCL. TAX):",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = colors.textSecondary,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Sunken LCD Touch Well
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .neumorphic(
                                shape = NeumorphicShape.CONCAVE,
                                cornerRadius = 8.dp,
                                elevation = 2.dp,
                                lightShadowColor = colors.lightHighlight,
                                darkShadowColor = colors.darkShadow,
                                backgroundColor = colors.lcdWellBackground
                            )
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = state.displayAmount,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.textPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Subtle Inset Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.darkShadow.copy(alpha = 0.4f))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2-Column Tax Breakdown Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT COLUMN: Statutory Tax Split (CGST + SGST or IGST)
                    Column(
                        modifier = Modifier.weight(1.1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (state.isInterState) {
                            ReceiptItem(
                                label = "IGST (${state.selectedGstRate}%):",
                                value = igstStr
                            )
                            ReceiptItem(
                                label = "Jurisdiction:",
                                value = "Inter-State"
                            )
                        } else {
                            val halfRate = state.selectedGstRate / 2.0
                            ReceiptItem(
                                label = "CGST (${halfRate}%):",
                                value = cgstStr
                            )
                            ReceiptItem(
                                label = "SGST (${halfRate}%):",
                                value = sgstStr
                            )
                        }
                    }

                    // RIGHT COLUMN: Commercial Totals (Total Tax + Net/Gross Result)
                    Column(
                        modifier = Modifier.weight(1.1f),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (state.isReverseGst) {
                            ReceiptItem(
                                label = "Tax Deducted (${state.selectedGstRate}%):",
                                value = totalTaxStr,
                                isRightAlign = true
                            )
                            ReceiptItem(
                                label = "Net Base (Excl. Tax):",
                                value = netBaseStr,
                                isRightAlign = true,
                                isHighlight = true,
                                isEnlarged = state.isResultEnlarged,
                                highlightColor = GstSaffronAmber
                            )
                        } else {
                            ReceiptItem(
                                label = "Total Tax (${state.selectedGstRate}%):",
                                value = totalTaxStr,
                                isRightAlign = true
                            )
                            ReceiptItem(
                                label = "Total Invoice (Payable):",
                                value = grossFinalStr,
                                isRightAlign = true,
                                isHighlight = true,
                                isEnlarged = state.isResultEnlarged,
                                highlightColor = SapphireBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Words Transcription Plate (Multi-Line Recessed Container)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphic(
                            shape = NeumorphicShape.CONCAVE,
                            cornerRadius = 8.dp,
                            elevation = 2.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.background
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "IN WORDS: ",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = colors.textSecondary,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = state.inWordsText,
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = colors.textPrimary
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 2. DUAL NEUMORPHIC SLIDABLE SWITCHES (Mode & Jurisdiction)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left Switch: +GST vs −GST
            NeumorphicSlideSwitch(
                leftLabel = "+GST",
                rightLabel = "−GST",
                isRightSelected = state.isReverseGst,
                onToggle = { viewModel.onSetReverseMode(it) },
                modifier = Modifier.weight(1f),
                activeColor = if (state.isReverseGst) GstSaffronAmber else RupeeEmeraldGreen,
                height = 38.dp
            )

            // Right Switch: CGST+SGST vs IGST
            NeumorphicSlideSwitch(
                leftLabel = "CGST+SGST",
                rightLabel = "IGST",
                isRightSelected = state.isInterState,
                onToggle = { viewModel.onSetJurisdiction(it) },
                modifier = Modifier.weight(1f),
                activeColor = SapphireBlue,
                height = 38.dp
            )
        }

        // 3. SHIFTED GST SLABS ROW (3%, 5%, 12%, 18%, 28%)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(3, 5, 12, 18, 28).forEach { rate ->
                val sign = if (state.isReverseGst) "−" else "+"
                NeumorphicGstPill(
                    text = "$sign$rate%",
                    isSelected = state.selectedGstRate == rate,
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onSelectSlab(rate)
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    accentColor = when (rate) {
                        3, 28 -> GstSaffronAmber
                        else -> RupeeEmeraldGreen
                    },
                    fontSize = 12,
                    horizontalPadding = 3.dp
                )
            }
        }

        // 4. ACTION BAR (WhatsApp Share | Save | Copy | Clear)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeumorphicButton(
                text = "📤 Share",
                onClick = {
                    hapticEngine.playKeyClick()
                    val summary = viewModel.generateShareableSummary()
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, summary)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share GST Invoice"))
                },
                modifier = Modifier.weight(1.1f).height(42.dp),
                textColor = RupeeEmeraldGreen,
                fontSize = 12
            )

            NeumorphicButton(
                text = "💾 Save",
                onClick = {
                    hapticEngine.playKeyClick()
                    val resultVal = if (state.isReverseGst) netBaseStr else grossFinalStr
                    val expr = "${state.amountInput} ${if (state.isReverseGst) "−" else "+"} ${state.selectedGstRate}% GST"
                    historyRepo.insert(
                        CalculationHistoryItem(
                            type = if (state.isReverseGst) CalculationType.GST_REVERSE else CalculationType.GST_FORWARD,
                            formulaExpression = expr,
                            primaryResult = resultVal,
                            netBaseAmount = netBaseStr,
                            totalTaxAmount = totalTaxStr,
                            cgstAmount = cgstStr,
                            sgstAmount = sgstStr,
                            igstAmount = igstStr
                        )
                    )
                    Toast.makeText(context, "GST Invoice Saved to History", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f).height(42.dp),
                textColor = colors.textPrimary,
                fontSize = 12
            )

            NeumorphicButton(
                text = "📋 Copy",
                onClick = {
                    hapticEngine.playKeyClick()
                    clipboardManager.setText(AnnotatedString(viewModel.generateShareableSummary()))
                    Toast.makeText(context, "Invoice Summary Copied", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f).height(42.dp),
                textColor = colors.textPrimary,
                fontSize = 12
            )

            NeumorphicButton(
                text = "C",
                onClick = {
                    hapticEngine.playOperatorTick()
                    viewModel.onClear()
                },
                modifier = Modifier.width(48.dp).height(42.dp),
                textColor = DeleteRed,
                fontSize = 16
            )
        }

        // 5. FULL 4-ROW ZERO-SCROLL NUMPAD
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: 7 | 8 | 9 | ⌫
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "7", onClick = { viewModel.onDigit("7") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(text = "8", onClick = { viewModel.onDigit("8") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(text = "9", onClick = { viewModel.onDigit("9") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(
                    text = "⌫",
                    onClick = { viewModel.onDelete() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    textColor = OperatorOrange,
                    fontSize = 20
                )
            }

            // Row 2: 4 | 5 | 6 | +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "4", onClick = { viewModel.onDigit("4") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(text = "5", onClick = { viewModel.onDigit("5") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(text = "6", onClick = { viewModel.onDigit("6") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(
                    text = "+",
                    onClick = { viewModel.onSetReverseMode(false) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    textColor = if (!state.isReverseGst) RupeeEmeraldGreen else MemoryGrey,
                    fontSize = 22
                )
            }

            // Row 3: 1 | 2 | 3 | −
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "1", onClick = { viewModel.onDigit("1") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(text = "2", onClick = { viewModel.onDigit("2") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(text = "3", onClick = { viewModel.onDigit("3") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(
                    text = "−",
                    onClick = { viewModel.onSetReverseMode(true) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    textColor = if (state.isReverseGst) GstSaffronAmber else MemoryGrey,
                    fontSize = 22
                )
            }

            // Row 4: 00 | 0 | . | =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "00", onClick = { viewModel.onDigit("00") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 20)
                NeumorphicButton(text = "0", onClick = { viewModel.onDigit("0") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 21)
                NeumorphicButton(text = ".", onClick = { viewModel.onDigit(".") }, modifier = Modifier.weight(1f).height(52.dp), fontSize = 22)
                NeumorphicButton(
                    text = "=",
                    onClick = {
                        hapticEngine.playKeyClick()
                        viewModel.onEquals()
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    textColor = colors.accentEmerald,
                    fontSize = 22
                )
            }
        }
    }
}

@Composable
private fun ReceiptItem(
    label: String,
    value: String,
    isRightAlign: Boolean = false,
    isHighlight: Boolean = false,
    isEnlarged: Boolean = false,
    highlightColor: Color = Color.Unspecified
) {
    val colors = LocalNeumorphicColors.current
    Column(horizontalAlignment = if (isRightAlign) Alignment.End else Alignment.Start) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = if (isEnlarged && isHighlight) 17.5.sp else if (isHighlight) 14.5.sp else 12.5.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isHighlight) highlightColor else colors.textPrimary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
