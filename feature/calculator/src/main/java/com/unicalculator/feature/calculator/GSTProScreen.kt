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
        // 1. TOP SEGMENTED CONTROLS (Mode & Jurisdiction)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Dual Mode Switcher: +GST (Add Tax) vs -GST (Extract Base)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONCAVE,
                        cornerRadius = 12.dp,
                        elevation = 2.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    )
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .neumorphic(
                            shape = if (!state.isReverseGst) NeumorphicShape.CONVEX else NeumorphicShape.FLAT,
                            cornerRadius = 9.dp,
                            elevation = if (!state.isReverseGst) 3.dp else 0.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.background
                        )
                        .clickable {
                            hapticEngine.playOperatorTick()
                            viewModel.onSetReverseMode(false)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+GST (Add Tax)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (!state.isReverseGst) RupeeEmeraldGreen else colors.textSecondary
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .neumorphic(
                            shape = if (state.isReverseGst) NeumorphicShape.CONVEX else NeumorphicShape.FLAT,
                            cornerRadius = 9.dp,
                            elevation = if (state.isReverseGst) 3.dp else 0.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.background
                        )
                        .clickable {
                            hapticEngine.playOperatorTick()
                            viewModel.onSetReverseMode(true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−GST (Extract Base)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (state.isReverseGst) GstSaffronAmber else colors.textSecondary
                        )
                    )
                }
            }

            // Jurisdiction Selector: Intra-State vs Inter-State
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONCAVE,
                        cornerRadius = 10.dp,
                        elevation = 2.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    )
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .neumorphic(
                            shape = if (!state.isInterState) NeumorphicShape.CONVEX else NeumorphicShape.FLAT,
                            cornerRadius = 8.dp,
                            elevation = if (!state.isInterState) 3.dp else 0.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.background
                        )
                        .clickable {
                            hapticEngine.playOperatorTick()
                            viewModel.onSetJurisdiction(false)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏛️ Intra-State (CGST+SGST)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = if (!state.isInterState) RupeeEmeraldGreen else colors.textSecondary
                        ),
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .neumorphic(
                            shape = if (state.isInterState) NeumorphicShape.CONVEX else NeumorphicShape.FLAT,
                            cornerRadius = 8.dp,
                            elevation = if (state.isInterState) 3.dp else 0.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.background
                        )
                        .clickable {
                            hapticEngine.playOperatorTick()
                            viewModel.onSetJurisdiction(true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌐 Inter-State (IGST)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = if (state.isInterState) RupeeEmeraldGreen else colors.textSecondary
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        // 2. UNIFIED MASTER RECEIPT CARD (Merged Display + Live Calculation Plate)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neumorphic(
                    shape = NeumorphicShape.CONVEX,
                    cornerRadius = 16.dp,
                    elevation = 4.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.background
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
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
                        text = if (state.isReverseGst) "GROSS (MRP INCL.)" else "BASE (EXCL. TAX)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Text(
                        text = state.displayAmount,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = if (state.isReverseGst) GstSaffronAmber else RupeeEmeraldGreen
                        ),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Subtle Inset Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.darkShadow.copy(alpha = 0.4f))
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 2-Column Tax Breakdown Grid
                val breakdown = state.taxBreakdown
                val netBaseStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.netBaseAmount) } ?: "₹ 0.00"
                val cgstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.cgstAmount) } ?: "₹ 0.00"
                val sgstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.sgstAmount) } ?: "₹ 0.00"
                val igstStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.igstAmount) } ?: "₹ 0.00"
                val totalTaxStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.totalGstAmount) } ?: "₹ 0.00"
                val grossFinalStr = breakdown?.let { IndianVedicFormatter.formatCurrency(it.grossFinalAmount) } ?: "₹ 0.00"

                if (state.isInterState) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ReceiptItem(label = "Net Base:", value = netBaseStr, modifier = Modifier.weight(1f))
                        ReceiptItem(label = "IGST (${state.selectedGstRate}%):", value = igstStr, modifier = Modifier.weight(1f), isRightAlign = true)
                    }
                } else {
                    val halfRate = state.selectedGstRate / 2.0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ReceiptItem(label = "CGST (${halfRate}%):", value = cgstStr, modifier = Modifier.weight(1f))
                        ReceiptItem(label = "SGST (${halfRate}%):", value = sgstStr, modifier = Modifier.weight(1f), isRightAlign = true)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ReceiptItem(label = "Total Tax (${state.selectedGstRate}%):", value = totalTaxStr, modifier = Modifier.weight(1f))
                    ReceiptItem(
                        label = "Total Invoice:",
                        value = grossFinalStr,
                        modifier = Modifier.weight(1f),
                        isRightAlign = true,
                        isHighlight = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Words Transcription
                Text(
                    text = "In Words: ${state.inWordsText}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.5.sp,
                        color = colors.textSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
                    modifier = Modifier.weight(1f).height(38.dp),
                    accentColor = when (rate) {
                        3, 28 -> GstSaffronAmber
                        else -> RupeeEmeraldGreen
                    },
                    fontSize = 12,
                    horizontalPadding = 4.dp
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

            // Row 2: 4, 5, 6, ±
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "4", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("4") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "5", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("5") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "6", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("6") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "±", onClick = { hapticEngine.playOperatorTick() }, modifier = Modifier.weight(1f).height(54.dp), textColor = MemoryGrey, fontSize = 18)
            }

            // Row 3: 1, 2, 3, %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "1", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("1") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "2", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("2") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "3", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("3") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "%", onClick = { hapticEngine.playOperatorTick() }, modifier = Modifier.weight(1f).height(54.dp), textColor = OperatorOrange, fontSize = 18)
            }

            // Row 4: 00, 0, ., =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(text = "00", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("00") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 18)
                NeumorphicButton(text = "0", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit("0") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = ".", onClick = { hapticEngine.playKeyClick(); viewModel.onDigit(".") }, modifier = Modifier.weight(1f).height(54.dp), fontSize = 20)
                NeumorphicButton(text = "=", onClick = { hapticEngine.playOperatorTick() }, modifier = Modifier.weight(1f).height(54.dp), textColor = RupeeEmeraldGreen, fontSize = 20)
            }
        }
    }
}

@Composable
private fun ReceiptItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isRightAlign: Boolean = false,
    isHighlight: Boolean = false
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
        Text(
            text = value,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = if (isHighlight) 13.sp else 12.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isHighlight) RupeeEmeraldGreen else colors.textPrimary
            ),
            maxLines = 1
        )
    }
}
