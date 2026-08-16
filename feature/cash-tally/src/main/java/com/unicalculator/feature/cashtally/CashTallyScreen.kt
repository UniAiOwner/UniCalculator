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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen

@Composable
fun CashTallyScreen(
    onNavigateToHistory: (() -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Top Action Bar: Screen Title (Left) + 3 Neumorphic Action Buttons (Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cash Tally",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
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
                    contentDescription = "Cash Tally History",
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

        Spacer(modifier = Modifier.height(2.dp))

        // 2. Master Neumorphic Summary Header Plate with C/CE Repositioned to Top-Right
        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            elevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Column: Total Cash Heading + Large Amount
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TOTAL CASH:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.totalCashFormatted,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = colors.textPrimary
                            )
                        )
                    }

                    // Right Column: C/CE Button (Top-Right) + Total Notes underneath
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Clear Entries (C / CE) Action Button in Top-Right
                        Box(
                            modifier = Modifier
                                .size(width = 64.dp, height = 34.dp)
                                .neumorphic(
                                    shape = NeumorphicShape.CONVEX,
                                    cornerRadius = 10.dp,
                                    elevation = 3.dp,
                                    lightShadowColor = colors.lightHighlight,
                                    darkShadowColor = colors.darkShadow,
                                    backgroundColor = colors.background
                                )
                                .clickable {
                                    hapticEngine.playOperatorTick()
                                    viewModel.resetAll()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "C/CE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeleteRed
                            )
                        }

                        // Total Notes readout placed right below C/CE
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Notes:",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "${state.totalNotesCount} Pcs",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = colors.textPrimary
                                )
                            )
                        }
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

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Table Column Header Bar: NOTE | COUNT (PCS) | SUBTOTAL
        NeumorphicPlate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            cornerRadius = 14.dp,
            elevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NOTE",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textSecondary,
                    modifier = Modifier.width(72.dp)
                )
                Text(
                    text = "COUNT (PCS)",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(96.dp)
                )
                Text(
                    text = "SUBTOTAL",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Denomination Note Counter Rows (₹500 down to ₹1) - Clean Ledger Grid
        state.state.denominations.forEach { item ->
            val noteBadgeColor = when (item.faceValue) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Currency Face Badge (Left, 72.dp wide)
                    Box(
                        modifier = Modifier
                            .size(width = 72.dp, height = 38.dp)
                            .neumorphic(
                                shape = NeumorphicShape.CONCAVE,
                                cornerRadius = 10.dp,
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

                    // 2. Direct Numeric Input Quantity Field (Center, 96.dp wide)
                    Box(
                        modifier = Modifier
                            .size(width = 96.dp, height = 38.dp)
                            .neumorphic(
                                shape = NeumorphicShape.CONCAVE,
                                cornerRadius = 10.dp,
                                elevation = 2.dp,
                                lightShadowColor = colors.lightHighlight,
                                darkShadowColor = colors.darkShadow,
                                backgroundColor = colors.lcdWellBackground
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = if (item.count == 0) "" else "${item.count}",
                            onValueChange = { input ->
                                val sanitized = input.filter { it.isDigit() }
                                val count = sanitized.toIntOrNull() ?: 0
                                viewModel.updateCount(item.faceValue, count)
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            cursorBrush = SolidColor(colors.accentEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                        )
                        if (item.count == 0) {
                            Text(
                                text = "0",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // 3. Row Subtotal (Right, flexible weight, right-aligned)
                    Text(
                        text = IndianVedicFormatter.formatCurrency(item.subtotal),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textPrimary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
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
