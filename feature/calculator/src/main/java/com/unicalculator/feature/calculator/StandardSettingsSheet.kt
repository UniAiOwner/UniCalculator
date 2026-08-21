package com.unicalculator.feature.calculator

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.common.prefs.HapticIntensity
import com.unicalculator.core.common.prefs.NumberFormatStyle
import com.unicalculator.core.common.prefs.UniCalculatorPreferences
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.designsystem.component.AboutUniCalculatorSheet
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.component.NeumorphicSlideSwitch
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.CalculationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardSettingsSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    val colors = LocalNeumorphicColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val decimalPrecision by prefs.decimalPrecision.collectAsState()
    val numberFormat by prefs.numberFormat.collectAsState()
    val hapticIntensity by prefs.hapticIntensity.collectAsState()
    val keepAwake by prefs.keepScreenAwake.collectAsState()
    val showCurrencySymbol by prefs.showCurrencySymbol.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Standard Calculator Settings",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = colors.textPrimary
                    )
                )
                NeumorphicIconButton(
                    icon = Icons.Default.Close,
                    contentDescription = "Close",
                    onClick = onDismiss,
                    size = 36.dp,
                    iconSize = 18.dp
                )
            }

            // 0. Currency Symbol & Words (Default OFF)
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Show Currency Symbol (₹)",
                            style = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textPrimary)
                        )
                        Text(
                            text = "Prefix results with '₹' & 'Rupees Only'",
                            style = TextStyle(fontFamily = FontFamily.Default, fontSize = 12.sp, color = colors.textSecondary)
                        )
                    }
                    NeumorphicSlideSwitch(
                        leftLabel = "Off",
                        rightLabel = "On",
                        isRightSelected = showCurrencySymbol,
                        onToggle = { prefs.setShowCurrencySymbol(it) },
                        modifier = Modifier.width(100.dp)
                    )
                }
            }

            // 1. Decimal Precision
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DECIMAL PRECISION",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(-1 to "Auto", 0 to "0", 2 to "2", 4 to "4", 6 to "6").forEach { (prec, label) ->
                            NeumorphicGstPill(
                                text = label,
                                isSelected = decimalPrecision == prec,
                                onClick = { prefs.setDecimalPrecision(prec) },
                                modifier = Modifier.weight(1f),
                                fontSize = 12
                            )
                        }
                    }
                }
            }

            // 2. Number Grouping Format (Indian Vedic vs Western)
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "NUMBER GROUPING FORMAT",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    NeumorphicSlideSwitch(
                        leftLabel = "Indian (12,34,567)",
                        rightLabel = "Western (1,234,567)",
                        isRightSelected = numberFormat == NumberFormatStyle.INTERNATIONAL_WESTERN,
                        onToggle = { isWestern ->
                            prefs.setNumberFormat(if (isWestern) NumberFormatStyle.INTERNATIONAL_WESTERN else NumberFormatStyle.INDIAN_VEDIC)
                        }
                    )
                }
            }

            // 3. Tactile Haptics
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "KEYPAD HAPTIC INTENSITY",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HapticIntensity.entries.forEach { intensity ->
                            NeumorphicGstPill(
                                text = intensity.name.lowercase().replaceFirstChar { it.uppercase() },
                                isSelected = hapticIntensity == intensity,
                                onClick = { prefs.setHapticIntensity(intensity) },
                                modifier = Modifier.weight(1f),
                                fontSize = 11
                            )
                        }
                    }
                }
            }

            // 4. Keep Screen Awake
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Keep Screen Awake",
                            style = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textPrimary)
                        )
                        Text(
                            text = "Prevent screen timeout during active math",
                            style = TextStyle(fontFamily = FontFamily.Default, fontSize = 12.sp, color = colors.textSecondary)
                        )
                    }
                    NeumorphicSlideSwitch(
                        leftLabel = "Off",
                        rightLabel = "On",
                        isRightSelected = keepAwake,
                        onToggle = { prefs.setKeepScreenAwake(it) },
                        modifier = Modifier.width(100.dp)
                    )
                }
            }

            // 5. About UniCalculator Bharat
            var showAboutSheet by remember { androidx.compose.runtime.mutableStateOf(false) }
            NeumorphicButton(
                text = "ℹ️ About UniCalculator Bharat",
                onClick = { showAboutSheet = true },
                accentColor = RupeeEmeraldGreen,
                fontSize = 13,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            if (showAboutSheet) {
                AboutUniCalculatorSheet(onDismiss = { showAboutSheet = false })
            }

            // 6. Clear Standard History Only
            NeumorphicButton(
                text = "🗑️ Clear Standard History Only",
                onClick = {
                    historyRepo.deleteByTypes(listOf(CalculationType.STANDARD_MATH))
                    Toast.makeText(context, "Standard math history cleared", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                accentColor = DeleteRed,
                fontSize = 13,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
