package com.unicalculator.feature.cashtally

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.common.prefs.UniCalculatorPreferences
import com.unicalculator.core.common.words.WordsLanguage
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.component.NeumorphicSlideSwitch
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.CalculationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashTallySettingsSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    val colors = LocalNeumorphicColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val show2000Note by prefs.show2000Note.collectAsState()
    val show2Note by prefs.show2Note.collectAsState()
    val show1Note by prefs.show1Note.collectAsState()
    val firmName by prefs.firmName.collectAsState()
    val cashierName by prefs.cashierName.collectAsState()
    val autoCopySlip by prefs.autoCopySlip.collectAsState()
    val wordsLanguage by prefs.wordsLanguage.collectAsState()

    var tempFirm by remember { mutableStateOf(firmName) }
    var tempCashier by remember { mutableStateOf(cashierName) }

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
                    text = "Cash Tally Settings",
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
                    onClick = {
                        prefs.setFirmName(tempFirm)
                        prefs.setCashierName(tempCashier)
                        onDismiss()
                    },
                    size = 36.dp,
                    iconSize = 18.dp
                )
            }

            // Pro Plan Status Card
            var showSubscriptionSheet by remember { androidx.compose.runtime.mutableStateOf(false) }
            val subscriptionStatus by prefs.subscriptionStatus.collectAsState()

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = if (colors.isDark) androidx.compose.ui.graphics.Color(0xFFFFD700) else androidx.compose.ui.graphics.Color(0xFFD4AF37),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "UniCalculator Pro",
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = colors.textPrimary)
                            )
                        }
                        Text(
                            text = when (val status = subscriptionStatus) {
                                is com.unicalculator.core.model.SubscriptionStatus.TrialActive -> "🎁 30-Day Free Trial: ${status.daysRemaining} Days Left"
                                is com.unicalculator.core.model.SubscriptionStatus.Subscribed -> "👑 Subscribed: ${status.plan.title}"
                                is com.unicalculator.core.model.SubscriptionStatus.LifetimePro -> "👑 Lifetime VIP Active"
                                is com.unicalculator.core.model.SubscriptionStatus.TrialExpired -> "⚠️ Trial Ended • Upgrade to Pro"
                            },
                            style = TextStyle(fontSize = 11.5.sp, color = colors.textSecondary)
                        )
                    }

                    NeumorphicButton(
                        text = "✨ Upgrade",
                        onClick = { showSubscriptionSheet = true },
                        accentColor = RupeeEmeraldGreen,
                        fontSize = 11,
                        modifier = Modifier.width(95.dp).height(38.dp)
                    )
                }
            }

            if (showSubscriptionSheet) {
                com.unicalculator.core.designsystem.component.NeumorphicSubscriptionSheet(onDismiss = { showSubscriptionSheet = false })
            }

            // 1. Active Denomination Rows Visibility
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SPECIAL DENOMINATIONS VISIBILITY",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )

                    // ₹2000 Note Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show ₹2000 Note Row", style = TextStyle(fontFamily = FontFamily.Default, fontSize = 13.sp, color = colors.textPrimary))
                        NeumorphicSlideSwitch(leftLabel = "Hide", rightLabel = "Show", isRightSelected = show2000Note, onToggle = { prefs.setShow2000Note(it) }, modifier = Modifier.width(90.dp))
                    }

                    // ₹2 Note Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show ₹2 Note Row", style = TextStyle(fontFamily = FontFamily.Default, fontSize = 13.sp, color = colors.textPrimary))
                        NeumorphicSlideSwitch(leftLabel = "Hide", rightLabel = "Show", isRightSelected = show2Note, onToggle = { prefs.setShow2Note(it) }, modifier = Modifier.width(90.dp))
                    }

                    // ₹1 Note Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show ₹1 Note Row", style = TextStyle(fontFamily = FontFamily.Default, fontSize = 13.sp, color = colors.textPrimary))
                        NeumorphicSlideSwitch(leftLabel = "Hide", rightLabel = "Show", isRightSelected = show1Note, onToggle = { prefs.setShow1Note(it) }, modifier = Modifier.width(90.dp))
                    }
                }
            }

            // 2. Business & Cashier Identity Tag
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Firm / Business Name Field
                    Column {
                        Text(
                            text = "FIRM / BUSINESS / SHOP NAME",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .neumorphic(
                                    shape = NeumorphicShape.CONCAVE,
                                    cornerRadius = 12.dp,
                                    elevation = 3.dp,
                                    lightShadowColor = colors.lightHighlight,
                                    darkShadowColor = colors.darkShadow,
                                    backgroundColor = colors.lcdWellBackground
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = tempFirm,
                                onValueChange = {
                                    tempFirm = it
                                    prefs.setFirmName(it)
                                },
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = colors.textPrimary
                                ),
                                cursorBrush = SolidColor(colors.accentEmerald),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Cashier Name Field
                    Column {
                        Text(
                            text = "CASHIER NAME",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .neumorphic(
                                    shape = NeumorphicShape.CONCAVE,
                                    cornerRadius = 12.dp,
                                    elevation = 3.dp,
                                    lightShadowColor = colors.lightHighlight,
                                    darkShadowColor = colors.darkShadow,
                                    backgroundColor = colors.lcdWellBackground
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = tempCashier,
                                onValueChange = {
                                    tempCashier = it
                                    prefs.setCashierName(it)
                                },
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = colors.textPrimary
                                ),
                                cursorBrush = SolidColor(colors.accentEmerald),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // In-Words Language Setting (English, Hindi, Both, Off)
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RESULT IN-WORDS LANGUAGE",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            WordsLanguage.ENGLISH to "English",
                            WordsLanguage.HINDI to "Hindi",
                            WordsLanguage.BOTH to "Both",
                            WordsLanguage.OFF to "Off"
                        ).forEach { (lang, label) ->
                            NeumorphicGstPill(
                                text = label,
                                isSelected = wordsLanguage == lang,
                                onClick = { prefs.setWordsLanguage(lang) },
                                modifier = Modifier.weight(1f),
                                fontSize = 12
                            )
                        }
                    }
                }
            }

            // 3. Auto-Copy Closing Slip on Save
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
                            text = "Auto-Copy Closing Slip",
                            style = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textPrimary)
                        )
                        Text(
                            text = "Copy text slip to clipboard on Save",
                            style = TextStyle(fontFamily = FontFamily.Default, fontSize = 12.sp, color = colors.textSecondary)
                        )
                    }
                    NeumorphicSlideSwitch(
                        leftLabel = "Off",
                        rightLabel = "On",
                        isRightSelected = autoCopySlip,
                        onToggle = { prefs.setAutoCopySlip(it) },
                        modifier = Modifier.width(100.dp)
                    )
                }
            }

            // 4. About UniCalculator
            var showAboutSheet by remember { androidx.compose.runtime.mutableStateOf(false) }
            NeumorphicButton(
                text = "ℹ️ About UniCalculator",
                onClick = { showAboutSheet = true },
                accentColor = RupeeEmeraldGreen,
                fontSize = 13,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )

            if (showAboutSheet) {
                com.unicalculator.core.designsystem.component.AboutUniCalculatorSheet(
                    onDismiss = { showAboutSheet = false }
                )
            }

            // 5. Clear Cash Tally History Only
            NeumorphicButton(
                text = "🗑️ Clear Cash Tally Sessions Only",
                onClick = {
                    historyRepo.deleteByTypes(listOf(CalculationType.CASH_TALLY))
                    Toast.makeText(context, "Cash Tally sessions cleared", Toast.LENGTH_SHORT).show()
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
