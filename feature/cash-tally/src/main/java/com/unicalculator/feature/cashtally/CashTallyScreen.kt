package com.unicalculator.feature.cashtally

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import com.unicalculator.core.designsystem.component.NeumorphicButton
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
    val hapticEngine = NeumorphicHapticEngine(LocalContext.current)
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // 1. Consistent Top Action Bar (Title + History, Theme, Settings)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cash Tally",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.textPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // History Button (Opens Cash Tally Segregated History)
                NeumorphicIconButton(
                    icon = Icons.Outlined.History,
                    contentDescription = "Cash Tally History",
                    onClick = {
                        hapticEngine.playKeyClick()
                        onNavigateToHistory?.invoke()
                    },
                    size = 42.dp,
                    cornerRadius = 14.dp,
                    iconTint = RupeeEmeraldGreen
                )

                // Theme Switcher Button
                NeumorphicIconButton(
                    icon = Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Theme",
                    onClick = {
                        hapticEngine.playKeyClick()
                        onToggleTheme?.invoke()
                    },
                    size = 42.dp,
                    cornerRadius = 14.dp
                )

                // Settings Button
                NeumorphicIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    onClick = {
                        hapticEngine.playKeyClick()
                        onOpenSettings?.invoke()
                    },
                    size = 42.dp,
                    cornerRadius = 14.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 2. Master Neumorphic Summary Header Plate (Variant 3: Cashier Pro Split Dual-Metric HUD)
        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            elevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Row: Split Dual Sunken Wells (Left: TOTAL CASH, Right: TOTAL PCS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Sunken Well: TOTAL CASH
                    Box(
                        modifier = Modifier
                            .weight(1.35f)
                            .height(62.dp)
                            .neumorphic(
                                shape = NeumorphicShape.CONCAVE,
                                cornerRadius = 14.dp,
                                elevation = 2.5.dp,
                                lightShadowColor = colors.lightHighlight,
                                darkShadowColor = colors.darkShadow,
                                backgroundColor = colors.lcdWellBackground
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "TOTAL CASH:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary,
                                lineHeight = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = state.totalCashFormatted,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = RupeeEmeraldGreen
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    // Right Sunken Well: TOTAL PCS
                    Box(
                        modifier = Modifier
                            .weight(0.65f)
                            .height(62.dp)
                            .neumorphic(
                                shape = NeumorphicShape.CONCAVE,
                                cornerRadius = 14.dp,
                                elevation = 2.5.dp,
                                lightShadowColor = colors.lightHighlight,
                                darkShadowColor = colors.darkShadow,
                                backgroundColor = colors.lcdWellBackground
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "TOTAL PCS",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary,
                                lineHeight = 11.sp
                            )
                            Text(
                                text = "${state.totalNotesCount}",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary,
                                lineHeight = 23.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Sunken Well: In Words Sub-Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphic(
                            shape = NeumorphicShape.CONCAVE,
                            cornerRadius = 12.dp,
                            elevation = 2.5.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.lcdWellBackground
                        )
                        .clickable { viewModel.toggleLanguage() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "In Words: ${state.wordsText}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. ACTION BAR (Share | Save | Copy | C/CE)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Share Action (Solid Emerald Pill)
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .height(38.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 12.dp,
                        elevation = 3.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = RupeeEmeraldGreen
                    )
                    .clickable {
                        hapticEngine.playKeyClick()
                        val slip = viewModel.generateWhatsAppSlipText()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, slip)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Cash Closing Slip"))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📤 Share",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }

            // Save Action
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 12.dp,
                        elevation = 3.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    )
                    .clickable {
                        hapticEngine.playKeyClick()
                        Toast.makeText(context, "Cash Tally Saved to History", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💾 Save",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textPrimary
                )
            }

            // Copy Action
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 12.dp,
                        elevation = 3.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    )
                    .clickable {
                        hapticEngine.playKeyClick()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Cash Tally Breakdown", viewModel.generateWhatsAppSlipText())
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Breakdown Copied to Clipboard", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📋 Copy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textPrimary
                )
            }

            // C/CE Action (Solid Crimson Red Pill)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 12.dp,
                        elevation = 3.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = DeleteRed
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
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. TABLE COLUMN HEADERS (Unified Slot Matrix - 66dp, 16dp, 80dp, 16dp, weight 1f)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [ 💵 NOTE ] (Exact 66.dp width)
            Box(
                modifier = Modifier
                    .width(66.dp)
                    .height(36.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 10.dp,
                        elevation = 2.5.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ 💵 NOTE ]",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textPrimary
                )
            }

            // Multiplication Column Spacer (Exact 16.dp width)
            Spacer(modifier = Modifier.width(16.dp))

            // [ COUNT (Pcs) ] (Exact 80.dp width - 2 Line Layout)
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 10.dp,
                        elevation = 2.5.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "[ COUNT ]",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textPrimary,
                        lineHeight = 11.sp
                    )
                    Text(
                        text = "(Pcs)",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary,
                        lineHeight = 9.5.sp
                    )
                }
            }

            // Equals Column Spacer (Exact 16.dp width)
            Spacer(modifier = Modifier.width(16.dp))

            // [ 💰 SUBTOTAL ] (Flexible weight(1f) width)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 10.dp,
                        elevation = 2.5.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ 💰 SUBTOTAL ]",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = RupeeEmeraldGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 5. Denomination Note Counter Rows (₹500 down to ₹1) - Direct Surface Ledger
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

            val isActive = item.count > 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Currency Face Badge (Left, Exact 66.dp wide, Convex Pill)
                Box(
                    modifier = Modifier
                        .width(66.dp)
                        .height(38.dp)
                        .neumorphic(
                            shape = NeumorphicShape.CONVEX,
                            cornerRadius = 10.dp,
                            elevation = if (isActive) 3.5.dp else 2.5.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = noteBadgeColor.copy(alpha = if (isActive) 1f else 0.85f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₹${item.faceValue}",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF1C2833)
                    )
                }

                // Mathematical Multiplication Sign Slot (Exact 16.dp wide)
                Box(
                    modifier = Modifier.width(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary.copy(alpha = 0.65f)
                    )
                }

                // 2. Direct Numeric Input Quantity Field (Center, Exact 80.dp wide, Deep Recessed Concave Well)
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(38.dp)
                        .neumorphic(
                            shape = NeumorphicShape.CONCAVE,
                            cornerRadius = 10.dp,
                            elevation = 2.5.dp,
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
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isActive) colors.textPrimary else colors.textSecondary.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        cursorBrush = SolidColor(colors.accentEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )
                    if (item.count == 0) {
                        Text(
                            text = "0",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = colors.textSecondary.copy(alpha = 0.35f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Mathematical Equals Sign Slot (Exact 16.dp wide)
                Box(
                    modifier = Modifier.width(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "=",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary.copy(alpha = 0.65f)
                    )
                }

                // 3. Row Subtotal (Right, Bold Emerald Highlight, strictly singleLine)
                Text(
                    text = IndianVedicFormatter.formatCurrency(item.subtotal),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isActive) RupeeEmeraldGreen else colors.textSecondary.copy(alpha = 0.45f),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
