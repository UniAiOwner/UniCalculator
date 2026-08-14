package com.unicalculator.feature.cashtally

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen

@Composable
fun CashTallyScreen(
    viewModel: CashTallyViewModel = viewModel(),
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
        // 1. Master Neumorphic Summary Header Plate
        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            elevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL CASH:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                        Text(
                            text = state.totalCashFormatted,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = colors.textPrimary
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Notes",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "${state.totalNotesCount} Pcs",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = colors.textPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // In Words Sub-Badge (Recessed Well)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphic(
                            shape = NeumorphicShape.CONCAVE,
                            cornerRadius = 14.dp,
                            elevation = 3.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.lcdWellBackground
                        )
                        .clickable { viewModel.toggleLanguage() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "In Words: ${state.wordsText}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Denomination Note Counter Rows (₹2000 down to ₹1)
        state.state.denominations.forEach { item ->
            val noteBadgeColor = when (item.faceValue) {
                2000 -> Color(0xFFD98880)
                500 -> Color(0xFFA3E4D7)
                200 -> Color(0xFFF9E79F)
                100 -> Color(0xFFD2B4DE)
                50 -> Color(0xFFAED6F1)
                20 -> Color(0xFFFCF3CF)
                10 -> Color(0xFFEDBB99)
                else -> Color(0xFFD5DBDB)
            }

            NeumorphicPlate(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                cornerRadius = 18.dp,
                elevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Currency Face Badge
                    Box(
                        modifier = Modifier
                            .size(width = 68.dp, height = 36.dp)
                            .neumorphic(
                                shape = NeumorphicShape.CONCAVE,
                                cornerRadius = 8.dp,
                                elevation = 2.dp,
                                lightShadowColor = colors.lightHighlight,
                                darkShadowColor = colors.darkShadow,
                                backgroundColor = noteBadgeColor.copy(alpha = 0.6f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₹${item.faceValue}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF1C2833)
                        )
                    }

                    // Count Input Box (Recessed Well)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Note Count", fontSize = 10.sp, color = colors.textSecondary)
                        Box(
                            modifier = Modifier
                                .size(width = 60.dp, height = 32.dp)
                                .neumorphic(
                                    shape = NeumorphicShape.CONCAVE,
                                    cornerRadius = 8.dp,
                                    elevation = 2.dp,
                                    lightShadowColor = colors.lightHighlight,
                                    darkShadowColor = colors.darkShadow,
                                    backgroundColor = colors.lcdWellBackground
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${item.count}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary
                            )
                        }
                    }

                    // Row Subtotal
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Subtotal", fontSize = 10.sp, color = colors.textSecondary)
                        Text(
                            text = IndianVedicFormatter.formatCurrency(item.subtotal),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = colors.textPrimary
                        )
                    }

                    // Steppers (+ and -)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Plus
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .neumorphic(
                                    shape = NeumorphicShape.CONVEX,
                                    cornerRadius = 8.dp,
                                    elevation = 3.dp,
                                    lightShadowColor = colors.lightHighlight,
                                    darkShadowColor = colors.darkShadow,
                                    backgroundColor = colors.background
                                )
                                .clickable {
                                    hapticEngine.playKeyClick()
                                    viewModel.increment(item.faceValue)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        }

                        // Minus
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .neumorphic(
                                    shape = NeumorphicShape.CONVEX,
                                    cornerRadius = 8.dp,
                                    elevation = 3.dp,
                                    lightShadowColor = colors.lightHighlight,
                                    darkShadowColor = colors.darkShadow,
                                    backgroundColor = colors.background
                                )
                                .clickable {
                                    hapticEngine.playKeyClick()
                                    viewModel.decrement(item.faceValue)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Primary WhatsApp Share Slip Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONVEX,
                    cornerRadius = 16.dp,
                    elevation = 6.dp,
                    lightShadowColor = RupeeEmeraldGreen.copy(alpha = 0.4f),
                    darkShadowColor = Color.Black.copy(alpha = 0.25f),
                    backgroundColor = RupeeEmeraldGreen
                )
                .clickable {
                    hapticEngine.playOperatorTick()
                    val slip = viewModel.generateWhatsAppSlipText()
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, slip)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Cash Closing Slip")
                    context.startActivity(shareIntent)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share WhatsApp Closing Slip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
