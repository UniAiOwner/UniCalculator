package com.unicalculator.feature.calculator

import android.content.Intent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicPlate
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Dual Mode Switcher: +GST (Exclusive) vs -GST (Inclusive)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONCAVE,
                    cornerRadius = 14.dp,
                    elevation = 3.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.background
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .neumorphic(
                        shape = if (!state.isReverseGst) NeumorphicShape.CONVEX else NeumorphicShape.FLAT,
                        cornerRadius = 10.dp,
                        elevation = if (!state.isReverseGst) 4.dp else 0.dp,
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
                    fontSize = 13.sp,
                    fontWeight = if (!state.isReverseGst) FontWeight.Bold else FontWeight.Medium,
                    color = if (!state.isReverseGst) RupeeEmeraldGreen else colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .neumorphic(
                        shape = if (state.isReverseGst) NeumorphicShape.CONVEX else NeumorphicShape.FLAT,
                        cornerRadius = 10.dp,
                        elevation = if (state.isReverseGst) 4.dp else 0.dp,
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
                    fontSize = 13.sp,
                    fontWeight = if (state.isReverseGst) FontWeight.Bold else FontWeight.Medium,
                    color = if (state.isReverseGst) OperatorOrange else colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Jurisdiction Switcher: Intra-State vs Inter-State
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeumorphicGstPill(
                text = "🏛️ Intra-State (CGST+SGST)",
                isSelected = !state.isInterState,
                onClick = {
                    hapticEngine.playOperatorTick()
                    viewModel.onSetJurisdiction(false)
                },
                modifier = Modifier.weight(1f),
                accentColor = RupeeEmeraldGreen
            )
            NeumorphicGstPill(
                text = "🌐 Inter-State (IGST)",
                isSelected = state.isInterState,
                onClick = {
                    hapticEngine.playOperatorTick()
                    viewModel.onSetJurisdiction(true)
                },
                modifier = Modifier.weight(1f),
                accentColor = GstSaffronAmber
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Amount LCD Display Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONCAVE,
                    cornerRadius = 18.dp,
                    elevation = 5.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.background
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (state.isReverseGst) "GROSS AMOUNT (MRP INCL. TAX)" else "NET BASE AMOUNT (EXCL. TAX)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
                Text(
                    text = state.displayAmount,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (state.isReverseGst) OperatorOrange else colors.accentEmerald,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. GST Slabs Matrix Row (3%, 5%, 12%, 18%, 28%)
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
                    modifier = Modifier.weight(1f).height(42.dp),
                    accentColor = when (rate) {
                        3 -> GstSaffronAmber
                        28 -> GstSaffronAmber
                        else -> RupeeEmeraldGreen
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Dynamic Tax Invoice Summary Plate
        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val breakdown = state.taxBreakdown
                val base = breakdown?.let { IndianVedicFormatter.formatCurrency(it.netBaseAmount) } ?: "₹ 0.00"
                val gstTotal = breakdown?.let { IndianVedicFormatter.formatCurrency(it.totalGstAmount) } ?: "₹ 0.00"
                val gross = breakdown?.let { IndianVedicFormatter.formatCurrency(it.grossFinalAmount) } ?: "₹ 0.00"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Net Base Price:", fontSize = 13.sp, color = colors.textSecondary)
                    Text(base, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                }

                if (state.isInterState) {
                    val igst = breakdown?.let { IndianVedicFormatter.formatCurrency(it.igstAmount) } ?: "₹ 0.00"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("IGST (${state.selectedGstRate}%):", fontSize = 13.sp, color = colors.textSecondary)
                        Text(igst, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = OperatorOrange)
                    }
                } else {
                    val halfRate = state.selectedGstRate / 2.0
                    val cgst = breakdown?.let { IndianVedicFormatter.formatCurrency(it.cgstAmount) } ?: "₹ 0.00"
                    val sgst = breakdown?.let { IndianVedicFormatter.formatCurrency(it.sgstAmount) } ?: "₹ 0.00"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CGST ($halfRate%):", fontSize = 13.sp, color = colors.textSecondary)
                        Text(cgst, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = OperatorOrange)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("SGST ($halfRate%):", fontSize = 13.sp, color = colors.textSecondary)
                        Text(sgst, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = OperatorOrange)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Tax (${state.selectedGstRate}%):", fontSize = 13.sp, color = colors.textSecondary)
                    Text(gstTotal, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = GstSaffronAmber)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.darkShadow.copy(alpha = 0.3f))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Invoice Amount:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    Text(gross, fontSize = 17.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = RupeeEmeraldGreen)
                }

                Text(
                    text = "In Words: ${state.inWordsText}",
                    fontSize = 11.5.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Action Bar: 1-Tap Share to WhatsApp & Copy Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeumorphicButton(
                text = "📤 WhatsApp",
                onClick = {
                    hapticEngine.playKeyClick()
                    val summary = viewModel.generateShareableSummary()
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, summary)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share GST Summary"))
                },
                modifier = Modifier.weight(1f).height(48.dp),
                textColor = RupeeEmeraldGreen,
                fontSize = 13
            )

            NeumorphicButton(
                text = "📋 Copy",
                onClick = {
                    hapticEngine.playKeyClick()
                    clipboardManager.setText(AnnotatedString(viewModel.generateShareableSummary()))
                },
                modifier = Modifier.width(90.dp).height(48.dp),
                textColor = colors.textPrimary,
                fontSize = 14
            )

            NeumorphicButton(
                text = "C",
                onClick = {
                    hapticEngine.playOperatorTick()
                    viewModel.onClear()
                },
                modifier = Modifier.width(60.dp).height(48.dp),
                textColor = DeleteRed,
                fontSize = 18
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7. Compact 4-Column Numpad for Instant Amount Entry
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicButton("7", { hapticEngine.playKeyClick(); viewModel.onDigit("7") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("8", { hapticEngine.playKeyClick(); viewModel.onDigit("8") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("9", { hapticEngine.playKeyClick(); viewModel.onDigit("9") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("⌫", { hapticEngine.playKeyClick(); viewModel.onDelete() }, Modifier.weight(1f).height(50.dp), textColor = DeleteRed, fontSize = 20)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicButton("4", { hapticEngine.playKeyClick(); viewModel.onDigit("4") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("5", { hapticEngine.playKeyClick(); viewModel.onDigit("5") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("6", { hapticEngine.playKeyClick(); viewModel.onDigit("6") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("00", { hapticEngine.playKeyClick(); viewModel.onDigit("00") }, Modifier.weight(1f).height(50.dp), fontSize = 18)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicButton("1", { hapticEngine.playKeyClick(); viewModel.onDigit("1") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("2", { hapticEngine.playKeyClick(); viewModel.onDigit("2") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("3", { hapticEngine.playKeyClick(); viewModel.onDigit("3") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("0", { hapticEngine.playKeyClick(); viewModel.onDigit("0") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicButton(".", { hapticEngine.playKeyClick(); viewModel.onDigit(".") }, Modifier.weight(1f).height(50.dp), fontSize = 20)
                NeumorphicButton("000", { hapticEngine.playKeyClick(); viewModel.onDigit("000") }, Modifier.weight(2f).height(50.dp), fontSize = 18)
                NeumorphicButton("=", { hapticEngine.playKeyClick() }, Modifier.weight(1f).height(50.dp), textColor = RupeeEmeraldGreen, isAccent = true, fontSize = 22)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
