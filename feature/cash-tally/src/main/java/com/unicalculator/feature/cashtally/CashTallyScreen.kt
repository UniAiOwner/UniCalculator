package com.unicalculator.feature.cashtally

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.prefs.UniCalculatorPreferences
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType
import com.unicalculator.core.model.DenominationItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val hapticEngine = remember { NeumorphicHapticEngine(context) }
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val show2000Note by prefs.show2000Note.collectAsState()
    val show2Note by prefs.show2Note.collectAsState()
    val show1Note by prefs.show1Note.collectAsState()
    val hapticIntensity by prefs.hapticIntensity.collectAsState()
    val autoCopySlip by prefs.autoCopySlip.collectAsState()

    fun click() = hapticEngine.playKeyClick(hapticIntensity)
    fun tick() = hapticEngine.playOperatorTick(hapticIntensity)

    LaunchedEffect(Unit) {
        viewModel.setPreferences(prefs)
    }

    val filteredDenominations = remember(state.state.denominations, show2000Note, show2Note, show1Note) {
        state.state.denominations.filter { item ->
            when (item.faceValue) {
                2000 -> show2000Note
                2 -> show2Note
                1 -> show1Note
                else -> true
            }
        }
    }

    val liveDateFormatted = remember {
        try {
            LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))
        } catch (_: Exception) {
            "Today"
        }
    }

    if (showSettingsSheet) {
        CashTallySettingsSheet(onDismiss = { showSettingsSheet = false })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Top Header Bar: Title + Live Date + Top Action Icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Cash Tally",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textPrimary,
                    letterSpacing = 0.5.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(RupeeEmeraldGreen)
                    )
                    Text(
                        text = liveDateFormatted,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicIconButton(
                    icon = Icons.Outlined.History,
                    contentDescription = "Cash Tally History",
                    onClick = {
                        tick()
                        onNavigateToHistory?.invoke()
                    },
                    size = 36.dp,
                    cornerRadius = 10.dp,
                    iconSize = 18.dp,
                    iconTint = RupeeEmeraldGreen
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Theme",
                    onClick = {
                        tick()
                        onToggleTheme?.invoke()
                    },
                    size = 36.dp,
                    cornerRadius = 10.dp,
                    iconSize = 18.dp,
                    iconTint = colors.textSecondary
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Cash Tally Settings",
                    onClick = {
                        tick()
                        if (onOpenSettings != null) onOpenSettings.invoke() else showSettingsSheet = true
                    },
                    size = 36.dp,
                    cornerRadius = 10.dp,
                    iconSize = 18.dp,
                    iconTint = colors.textSecondary
                )
            }
        }

        // 2. BEZEL-LESS MASTER 3-WELL HUD CANVAS
        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONCAVE,
            cornerRadius = 18.dp,
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top Row: Shop/Cashier Label (Left) + Quick Action Chips (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cash Drawer: ${state.state.shopName}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = colors.textSecondary,
                            letterSpacing = 0.3.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Compact Action Chips (Copy | Share | Save | Clear)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy Slip
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.background.copy(alpha = 0.6f))
                                .clickable {
                                    click()
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Cash Tally Breakdown", viewModel.generateWhatsAppSlipText())
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Slip Copied", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.ContentCopy, "Copy", tint = colors.textSecondary, modifier = Modifier.size(14.dp))
                        }

                        // Share WhatsApp Slip
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.background.copy(alpha = 0.6f))
                                .clickable {
                                    click()
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
                            Icon(Icons.Outlined.Share, "Share", tint = RupeeEmeraldGreen, modifier = Modifier.size(14.dp))
                        }

                        // Save History
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.background.copy(alpha = 0.6f))
                                .clickable {
                                    click()
                                    val slip = viewModel.generateWhatsAppSlipText()
                                    val activeSummary = state.state.denominations.filter { it.count > 0 }
                                        .joinToString(", ") { "₹${it.faceValue}×${it.count}" }
                                    historyRepo.insert(
                                        CalculationHistoryItem(
                                            type = CalculationType.CASH_TALLY,
                                            formulaExpression = "Total Notes: ${state.totalNotesCount} ($activeSummary)",
                                            primaryResult = IndianVedicFormatter.formatCurrency(state.state.grandTotal),
                                            memoNote = slip
                                        )
                                    )
                                    if (autoCopySlip) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Cash Tally Breakdown", slip)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Saved & Slip Copied", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Save, "Save", tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                        }

                        // Clear All (C/CE)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.background.copy(alpha = 0.6f))
                                .clickable {
                                    tick()
                                    viewModel.resetAll()
                                    Toast.makeText(context, "Counter Reset", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.CleaningServices, "Reset", tint = DeleteRed, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Inset LCD Well: 3-Well Live Metrics (Total Cash + Notes Count + Packets Count)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .neumorphic(
                            shape = NeumorphicShape.CONCAVE,
                            cornerRadius = 10.dp,
                            elevation = 2.5.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.lcdWellBackground
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Total Cash
                        Column {
                            Text(
                                text = "TOTAL CASH",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.sp,
                                    color = colors.textSecondary
                                )
                            )
                            Text(
                                text = state.totalCashFormatted,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = RupeeEmeraldGreen
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // Center: Notes / Coins Count
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "PIECES",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.sp,
                                    color = colors.textSecondary
                                )
                            )
                            Text(
                                text = "${state.totalNotesCount} Pcs",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colors.textPrimary
                                )
                            )
                        }

                        // Right: Packets / Bundles Count
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "PACKETS",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.sp,
                                    color = colors.textSecondary
                                )
                            )
                            val pktText = if (state.totalPacketsCount > 0) {
                                "${state.totalPacketsCount} Pkt (${state.looseNotesCount})"
                            } else {
                                "${state.looseNotesCount} Loose"
                            }
                            Text(
                                text = pktText,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFB45309)
                                )
                            )
                        }
                    }
                }

                // Dedicated Dual-Language (English + Hindi) In-Words Cheque Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(RupeeEmeraldGreen)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = state.wordsEnglishText,
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = state.wordsHindiText,
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.92f),
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 4. Dual-Tab Segmented Switcher (📊 Cash Breakdown | ⚡ Quick Count)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONCAVE,
                    cornerRadius = 21.dp,
                    elevation = 2.5.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.lcdWellBackground
                )
                .padding(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 1: Cash Breakdown
                val isTab1 = state.activeTab == CashTallyTab.CASH_BREAKDOWN
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isTab1) colors.background else Color.Transparent)
                        .then(
                            if (isTab1) {
                                Modifier.border(1.dp, RupeeEmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            } else Modifier
                        )
                        .clickable {
                            click()
                            viewModel.setActiveTab(CashTallyTab.CASH_BREAKDOWN)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "📊", fontSize = 12.sp)
                        Text(
                            text = "Cash Breakdown",
                            fontSize = 11.5.sp,
                            fontWeight = if (isTab1) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = if (isTab1) RupeeEmeraldGreen else colors.textSecondary
                        )
                    }
                }

                // Tab 2: Quick Count
                val isTab2 = state.activeTab == CashTallyTab.QUICK_COUNT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isTab2) colors.background else Color.Transparent)
                        .then(
                            if (isTab2) {
                                Modifier.border(1.dp, RupeeEmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            } else Modifier
                        )
                        .clickable {
                            click()
                            viewModel.setActiveTab(CashTallyTab.QUICK_COUNT)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "⚡", fontSize = 12.sp)
                        Text(
                            text = "Quick Count",
                            fontSize = 11.5.sp,
                            fontWeight = if (isTab2) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = if (isTab2) RupeeEmeraldGreen else colors.textSecondary
                        )
                    }
                }
            }
        }

        // 5. Denomination Rows (with Stepper, Count Field, and Subtotal)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredDenominations.forEach { item ->
                DenominationRow(
                    item = item,
                    onIncrement = {
                        click()
                        viewModel.increment(item.faceValue)
                    },
                    onDecrement = {
                        click()
                        viewModel.decrement(item.faceValue)
                    },
                    onCountChanged = { newCount ->
                        viewModel.updateCount(item.faceValue, newCount)
                    },
                    onClear = {
                        tick()
                        viewModel.clearDenomination(item.faceValue)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
private fun DenominationRow(
    item: DenominationItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onCountChanged: (Int) -> Unit,
    onClear: () -> Unit
) {
    val colors = LocalNeumorphicColors.current
    var showMenu by remember { mutableStateOf(false) }

    // Indian Currency Pastel Badges
    val (badgeBg, badgeText) = when (item.faceValue) {
        2000 -> Color(0xFFFCE7F3) to Color(0xFFBE185D) // Soft Magenta
        500 -> Color(0xFFD1FAE5) to Color(0xFF047857)  // Soft Sage / Emerald
        200 -> Color(0xFFFEF3C7) to Color(0xFFB45309)  // Soft Amber / Gold
        100 -> Color(0xFFEDE9FE) to Color(0xFF6D28D9)  // Soft Lavender
        50 -> Color(0xFFE0F2FE) to Color(0xFF0369A1)   // Soft Sky Blue
        20 -> Color(0xFFFFE4E6) to Color(0xFFBE123C)   // Soft Peach / Coral
        10 -> Color(0xFFF5F5DC) to Color(0xFF78350F)   // Soft Sand / Warm Brown
        else -> Color(0xFFF3F4F6) to Color(0xFF374151)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Currency Tag Pill (Left)
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(badgeBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "₹${item.faceValue}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = badgeText
            )
        }

        // 2. Decrement Button (−)
        Box(
            modifier = Modifier
                .size(32.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONVEX,
                    cornerRadius = 16.dp,
                    elevation = 2.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.background
                )
                .clickable(onClick = onDecrement),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = colors.textPrimary,
                modifier = Modifier.size(15.dp)
            )
        }

        // 3. Recessed Count Box
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(32.dp)
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
                    onCountChanged(count)
                },
                textStyle = TextStyle(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (item.count > 0) colors.textPrimary else colors.textSecondary.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(RupeeEmeraldGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            )
            if (item.count == 0) {
                Text(
                    text = "0",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textSecondary.copy(alpha = 0.35f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 4. Increment Button (+)
        Box(
            modifier = Modifier
                .size(32.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONVEX,
                    cornerRadius = 16.dp,
                    elevation = 2.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.background
                )
                .clickable(onClick = onIncrement),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = colors.textPrimary,
                modifier = Modifier.size(15.dp)
            )
        }

        // 5. Subtotal & Note Count Display (Right)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = IndianVedicFormatter.formatCurrency(item.subtotal, includeSymbol = true, decimalPrecision = 0),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (item.count > 0) colors.textPrimary else colors.textSecondary.copy(alpha = 0.5f),
                maxLines = 1,
                softWrap = false
            )
            val packetBadge = when {
                item.count >= 100 -> {
                    val pkts = item.count / 100
                    val rem = item.count % 100
                    if (rem > 0) "$pkts Pkt + $rem Pcs" else "$pkts Pkt (${item.count})"
                }
                else -> "${item.count} Pcs"
            }
            Text(
                text = packetBadge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = colors.textSecondary,
                maxLines = 1
            )
        }

        // 6. Overflow Menu (⋮)
        Box {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Row Options",
                tint = colors.textSecondary.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { showMenu = true }
            )
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Zero / Clear Count") },
                    onClick = {
                        showMenu = false
                        onClear()
                    }
                )
            }
        }
    }
}
