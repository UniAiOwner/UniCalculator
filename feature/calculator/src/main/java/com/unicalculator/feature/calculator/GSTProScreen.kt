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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.common.prefs.UniCalculatorPreferences
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
import com.unicalculator.core.designsystem.theme.OperatorOrange
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType

private val SapphireBlue = Color(0xFF2980B9)
private val RoyalIndigo = Color(0xFF34495E)

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
    val hapticEngine = remember { NeumorphicHapticEngine(context) }
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    val defaultGstRate by prefs.defaultGstRate.collectAsState()
    val isInterStateDefault by prefs.isInterStateDefault.collectAsState()
    val hapticIntensity by prefs.hapticIntensity.collectAsState()
    var showSettingsSheet by remember { mutableStateOf(false) }

    fun click() = hapticEngine.playKeyClick(hapticIntensity)
    fun tick() = hapticEngine.playOperatorTick(hapticIntensity)

    LaunchedEffect(defaultGstRate, isInterStateDefault) {
        viewModel.setPreferences(prefs)
    }

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
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 0. Top App Bar: Screen Title (Left) + History/Theme/Settings Buttons (Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GST Pro",
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
                NeumorphicIconButton(
                    icon = Icons.Outlined.History,
                    contentDescription = "GST History",
                    onClick = {
                        tick()
                        onNavigateToHistory?.invoke()
                    },
                    iconTint = colors.accentEmerald,
                    size = 36.dp,
                    iconSize = 18.dp
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Theme",
                    onClick = {
                        tick()
                        onToggleTheme?.invoke()
                    },
                    iconTint = colors.textSecondary,
                    size = 36.dp,
                    iconSize = 18.dp
                )

                NeumorphicIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    onClick = {
                        tick()
                        if (onOpenSettings != null) onOpenSettings.invoke() else showSettingsSheet = true
                    },
                    iconTint = colors.textSecondary,
                    size = 36.dp,
                    iconSize = 18.dp
                )
            }
        }

        // 1. BEZEL-LESS MASTER HERO DISPLAY CANVAS
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
                // Top Row inside Card: Context Label (Left) + Quick Actions (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isReverseGst) "Gross Amount: ${state.displayAmount}" else "Base Amount: ${state.displayAmount}",
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

                    // Quick Action Icon Chips
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
                                    clipboardManager.setText(AnnotatedString(viewModel.generateShareableSummary()))
                                    Toast.makeText(context, "Slip Copied", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.ContentCopy, "Copy", tint = colors.textSecondary, modifier = Modifier.size(14.dp))
                        }

                        // Share Slip
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.background.copy(alpha = 0.6f))
                                .clickable {
                                    click()
                                    val summary = viewModel.generateShareableSummary()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, summary)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share GST Calculation"))
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
                                    Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Save, "Save", tint = SapphireBlue, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Sunken LCD Touch Well for Live Formula & Final Answer
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
                        val formulaExpr = "${state.amountInput.ifEmpty { "0" }} ${if (state.isReverseGst) "−" else "+"} ${state.selectedGstRate}% GST"
                        Text(
                            text = formulaExpr,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = colors.textSecondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val finalAnswerStr = if (state.isReverseGst) netBaseStr else grossFinalStr
                        Text(
                            text = finalAnswerStr,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = colors.textPrimary
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // 3-Column Statutory Tax Breakdown Grid with Vertical Dividers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.isInterState) {
                        TaxMetricCell(label = "IGST (${state.selectedGstRate}%):", value = igstStr, valueColor = SapphireBlue, modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.textSecondary.copy(alpha = 0.2f)))
                        TaxMetricCell(label = "Jurisdiction:", value = "Inter-State", valueColor = colors.textSecondary, modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.textSecondary.copy(alpha = 0.2f)))
                        TaxMetricCell(label = "Total Tax (${state.selectedGstRate}%):", value = totalTaxStr, valueColor = GstSaffronAmber, modifier = Modifier.weight(1f))
                    } else {
                        val halfRate = state.selectedGstRate / 2.0
                        TaxMetricCell(label = "CGST (${halfRate}%):", value = cgstStr, valueColor = SapphireBlue, modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.textSecondary.copy(alpha = 0.2f)))
                        TaxMetricCell(label = "SGST (${halfRate}%):", value = sgstStr, valueColor = SapphireBlue, modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.textSecondary.copy(alpha = 0.2f)))
                        TaxMetricCell(label = "Total Tax (${state.selectedGstRate}%):", value = totalTaxStr, valueColor = GstSaffronAmber, modifier = Modifier.weight(1f))
                    }
                }

                // Bottom In-Words / Cheque Words Banner (100% Dedicated to Words)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (state.isReverseGst) GstSaffronAmber
                            else RupeeEmeraldGreen
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = state.inWordsText,
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
                            text = state.inWordsHindiText,
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

        // 2. HIGH-CONTRAST SEGMENTED SWITCH CONTROLS (Mode + Jurisdiction)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeumorphicSlideSwitch(
                leftLabel = "+GST (Add)",
                rightLabel = "−GST (Extract)",
                isRightSelected = state.isReverseGst,
                onToggle = { isRev ->
                    tick()
                    viewModel.onSetReverseMode(isRev)
                },
                modifier = Modifier.weight(1f),
                leftActiveColor = RupeeEmeraldGreen,
                rightActiveColor = GstSaffronAmber,
                height = 36.dp
            )

            NeumorphicSlideSwitch(
                leftLabel = "CGST+SGST",
                rightLabel = "IGST",
                isRightSelected = state.isInterState,
                onToggle = { isInter ->
                    tick()
                    viewModel.onSetJurisdiction(isInter)
                },
                modifier = Modifier.weight(1f),
                leftActiveColor = SapphireBlue,
                rightActiveColor = RoyalIndigo,
                height = 36.dp
            )
        }

        // 3. STATUTORY GST RATE SLABS ROW (3%, 5%, 12%, 18%, 28%)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(3, 5, 12, 18, 28).forEach { rate ->
                val sign = if (state.isReverseGst) "−" else "+"
                NeumorphicGstPill(
                    text = "$sign$rate%",
                    isSelected = state.selectedGstRate == rate,
                    onClick = {
                        tick()
                        viewModel.onSelectSlab(rate)
                    },
                    modifier = Modifier.weight(1f).height(38.dp),
                    accentColor = when (rate) {
                        3, 28 -> GstSaffronAmber
                        else -> RupeeEmeraldGreen
                    },
                    fontSize = 12,
                    horizontalPadding = 2.dp
                )
            }
        }

        // 4. FULL 5-ROW COMPLETE 100% ZERO-SCROLL KEYPAD
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Row 1: C | ⌫ | % | ÷
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NeumorphicButton(
                    text = "C",
                    onClick = { tick(); viewModel.onClear() },
                    modifier = Modifier.weight(1f).height(46.dp),
                    textColor = DeleteRed,
                    fontSize = 18
                )
                NeumorphicButton(
                    text = "⌫",
                    onClick = { click(); viewModel.onDelete() },
                    modifier = Modifier.weight(1f).height(46.dp),
                    textColor = OperatorOrange,
                    fontSize = 18
                )
                NeumorphicButton(
                    text = "%",
                    onClick = { click(); viewModel.onDigit("%") },
                    modifier = Modifier.weight(1f).height(46.dp),
                    textColor = OperatorOrange,
                    fontSize = 18
                )
                NeumorphicButton(
                    text = "÷",
                    onClick = { click(); viewModel.onDigit(" ÷ ") },
                    modifier = Modifier.weight(1f).height(46.dp),
                    textColor = OperatorOrange,
                    fontSize = 20
                )
            }

            // Row 2: 7 | 8 | 9 | ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NeumorphicButton(text = "7", onClick = { click(); viewModel.onDigit("7") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(text = "8", onClick = { click(); viewModel.onDigit("8") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(text = "9", onClick = { click(); viewModel.onDigit("9") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(
                    text = "×",
                    onClick = { click(); viewModel.onDigit(" × ") },
                    modifier = Modifier.weight(1f).height(46.dp),
                    textColor = OperatorOrange,
                    fontSize = 20
                )
            }

            // Row 3: 4 | 5 | 6 | −
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NeumorphicButton(text = "4", onClick = { click(); viewModel.onDigit("4") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(text = "5", onClick = { click(); viewModel.onDigit("5") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(text = "6", onClick = { click(); viewModel.onDigit("6") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(
                    text = "−",
                    onClick = { click(); viewModel.onDigit(" − ") },
                    modifier = Modifier.weight(1f).height(46.dp),
                    textColor = OperatorOrange,
                    fontSize = 20
                )
            }

            // Row 4: 1 | 2 | 3 | +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NeumorphicButton(text = "1", onClick = { click(); viewModel.onDigit("1") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(text = "2", onClick = { click(); viewModel.onDigit("2") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(text = "3", onClick = { click(); viewModel.onDigit("3") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(
                    text = "+",
                    onClick = { click(); viewModel.onDigit(" + ") },
                    modifier = Modifier.weight(1f).height(46.dp),
                    textColor = OperatorOrange,
                    fontSize = 20
                )
            }

            // Row 5: 00 | 0 | . | =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NeumorphicButton(text = "00", onClick = { click(); viewModel.onDigit("00") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 18)
                NeumorphicButton(text = "0", onClick = { click(); viewModel.onDigit("0") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(text = ".", onClick = { click(); viewModel.onDigit(".") }, modifier = Modifier.weight(1f).height(46.dp), fontSize = 20)
                NeumorphicButton(
                    text = "=",
                    onClick = {
                        tick()
                        viewModel.onEquals()
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    isSolidAccent = true,
                    backgroundColor = RupeeEmeraldGreen,
                    textColor = Color.White,
                    fontSize = 22
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun TaxMetricCell(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeumorphicColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

