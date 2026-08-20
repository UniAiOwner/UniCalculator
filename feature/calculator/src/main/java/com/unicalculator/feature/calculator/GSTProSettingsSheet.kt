package com.unicalculator.feature.calculator

import android.widget.Toast
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.unicalculator.core.model.CalculationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSTProSettingsSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    val colors = LocalNeumorphicColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val defaultGstRate by prefs.defaultGstRate.collectAsState()
    val isInterStateDefault by prefs.isInterStateDefault.collectAsState()
    val businessName by prefs.businessName.collectAsState()
    val businessGstin by prefs.businessGstin.collectAsState()
    val isBankersRounding by prefs.isBankersRounding.collectAsState()

    var tempShopName by remember { mutableStateOf(businessName) }
    var tempGstin by remember { mutableStateOf(businessGstin) }

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
                    text = "GST Pro Settings",
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
                        prefs.setBusinessName(tempShopName)
                        prefs.setBusinessGstin(tempGstin)
                        onDismiss()
                    },
                    size = 36.dp,
                    iconSize = 18.dp
                )
            }

            // 1. Default GST Slab
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DEFAULT GST SLAB ON LAUNCH",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(3, 5, 12, 18, 28).forEach { slab ->
                            NeumorphicGstPill(
                                text = "$slab%",
                                isSelected = defaultGstRate == slab,
                                onClick = { prefs.setDefaultGstRate(slab) },
                                modifier = Modifier.weight(1f),
                                fontSize = 12
                            )
                        }
                    }
                }
            }

            // 2. Default Jurisdiction
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DEFAULT TAX JURISDICTION",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    NeumorphicSlideSwitch(
                        leftLabel = "CGST + SGST (Intra-State)",
                        rightLabel = "IGST (Inter-State)",
                        isRightSelected = isInterStateDefault,
                        onToggle = { isInter ->
                            prefs.setIsInterStateDefault(isInter)
                        }
                    )
                }
            }

            // 3. Business / Shop Name Header on Slips
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "BUSINESS / STORE NAME (ON INVOICES)",
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
                            value = tempShopName,
                            onValueChange = {
                                tempShopName = it
                                prefs.setBusinessName(it)
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

            // 4. Tax Rounding Rule
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TAX ROUNDING ALGORITHM",
                        style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    NeumorphicSlideSwitch(
                        leftLabel = "Exact Paise",
                        rightLabel = "Banker's (HALF_EVEN)",
                        isRightSelected = isBankersRounding,
                        onToggle = { prefs.setIsBankersRounding(it) }
                    )
                }
            }

            // 5. Clear GST History Only
            NeumorphicButton(
                text = "🗑️ Clear GST Invoices Only",
                onClick = {
                    historyRepo.deleteByTypes(listOf(CalculationType.GST_FORWARD, CalculationType.GST_REVERSE))
                    Toast.makeText(context, "GST Invoices history cleared", Toast.LENGTH_SHORT).show()
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
