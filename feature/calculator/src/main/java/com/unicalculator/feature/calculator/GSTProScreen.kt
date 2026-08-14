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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicSlideSwitch
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.GstSaffronAmber
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.MemoryGrey
import com.unicalculator.core.designsystem.theme.OperatorOrange
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen

@Composable
fun GSTProScreen(
    viewModel: GSTProViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val hapticEngine = NeumorphicHapticEngine(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. UNIFIED MASTER RECEIPT CARD (DISPLAY AT TOP)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neumorphic(
                    shape = NeumorphicShape.CONVEX,
                    cornerRadius = 18.dp,
                    elevation = 5.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.background
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header: Input Label & Entered Big Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isReverseGst) "GROSS / MRP" else "BASE AMOUNT",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Text(
                        text = state.displayAmount,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = colors.textSecondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                val breakdown = state.taxBreakdown
                val netBaseStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.netBaseAmount) } ?: "₹ 0.00"
                val cgstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.cgstAmount) } ?: "₹ 0.00"
                val sgstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.sgstAmount) } ?: "₹ 0.00"
                val igstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.igstAmount) } ?: "₹ 0.00"
                val totalTaxStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.totalGstAmount) } ?: "₹ 0.00"
                val grossFinalStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.grossFinalAmount) } ?: "₹ 0.00"

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
                                highlightColor = ElectricSapphireBlue
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
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "✍️ In Words: ${state.inWordsText}",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = colors.textSecondary
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
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
                activeColor = ElectricSapphireBlue,
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
            // Row 1: 7, 8, 9, ⌫
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "7", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("7") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "8", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("8") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "9", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("9") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "⌫", onClick = { hapticEngine.playOperatorTick(); viewModel.onDelete() }, modifier = Modifier.weight(1f).height(54.dp), textColor = OperatorOrange, fontSize = 18)
            }

            // Row 2: 4, 5, 6, ÷
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "4", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("4") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "5", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("5") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "6", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("6") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "÷", onClick = { hapticEngine.playOperatorTick(); viewModel.onOperator("÷") }, modifier = Modifier.weight(1f).height(54.dp), textColor = OperatorOrange, fontSize = 22)
            }

            // Row 3: 1, 2, 3, ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "1", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("1") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "2", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("2") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "3", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("3") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "×", onClick = { hapticEngine.playOperatorTick(); viewModel.onOperator("×") }, modifier = Modifier.weight(1f).height(54.dp), textColor = OperatorOrange, fontSize = 22)
            }

            // Row 4: 00, 0, ., =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "00", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("00") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 18)
                NeumorphicButton(text = "0", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("0") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = ".", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit(".") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(
                    text = "=",
                    onClick = {
                        hapticEngine.playOperatorTick()
                        viewModel.onEquals()
                    },
                    modifier = Modifier.weight(1f).height(54.dp),
                    textColor = ElectricSapphireBlue,
                    fontSize = 22
                )
            }
        }
    }
}

private val ElectricSapphireBlue = Color(0xFF2563EB)

@Composable
private fun ReceiptItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isRightAlign: Boolean = false,
    isHighlight: Boolean = false,
    isEnlarged: Boolean = false,
    highlightColor: androidx.compose.ui.graphics.Color = RupeeEmeraldGreen
) {
    val colors = LocalNeumorphicColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = if (isRightAlign) Alignment.End else Alignment.Start
    ) {
        Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            ),
            maxLines = 1
        )
        val dynamicFontSize = when {
            isHighlight && isEnlarged && value.length > 15 -> 15.5.sp
            isHighlight && isEnlarged && value.length > 12 -> 17.5.sp
            isHighlight && isEnlarged -> 19.5.sp
            isHighlight && value.length > 15 -> 13.sp
            isHighlight -> 15.sp
            value.length > 15 -> 10.5.sp
            else -> 12.sp
        }
        Text(
            text = value,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = dynamicFontSize,
                fontWeight = if (isHighlight && isEnlarged) FontWeight.Black else if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isHighlight) highlightColor else colors.textPrimary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

