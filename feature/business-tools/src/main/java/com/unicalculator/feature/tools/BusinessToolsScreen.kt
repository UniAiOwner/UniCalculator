package com.unicalculator.feature.tools

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.math.CommercialCalculatorEngine
import java.math.BigDecimal

@Composable
fun BusinessToolsScreen(modifier: Modifier = Modifier) {
    val colors = LocalNeumorphicColors.current
    var selectedTool by remember { mutableStateOf(0) } // 0: Margin/Markup, 1: Loan EMI

    var costPriceText by remember { mutableStateOf("1200") }
    var sellingPriceText by remember { mutableStateOf("1600") }
    var principalText by remember { mutableStateOf("500000") }
    var interestRateText by remember { mutableStateOf("8.5") }
    var tenureMonthsText by remember { mutableStateOf("36") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Tool Switcher Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeumorphicGstPill(
                text = "Margin & Markup",
                isSelected = selectedTool == 0,
                onClick = { selectedTool = 0 },
                modifier = Modifier.weight(1f)
            )
            NeumorphicGstPill(
                text = "Loan EMI",
                isSelected = selectedTool == 1,
                onClick = { selectedTool = 1 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedTool == 0) {
            // --- MARGIN & MARKUP CALCULATOR ---
            val costPrice = costPriceText.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val sellingPrice = sellingPriceText.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val marginResult = CommercialCalculatorEngine.calculateMarginMarkup(costPrice, sellingPrice)

            // Input 1: Cost Price (CP)
            NeumorphicInputField(
                label = "Cost Price (CP)",
                value = costPriceText,
                onValueChange = { costPriceText = it },
                prefix = "₹"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input 2: Selling Price (SP)
            NeumorphicInputField(
                label = "Selling Price (SP)",
                value = sellingPriceText,
                onValueChange = { sellingPriceText = it },
                prefix = "₹"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Result Breakdown Plate
            NeumorphicPlate(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp, elevation = 4.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text(
                        text = "Profitability Breakdown",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gross Profit:", fontWeight = FontWeight.SemiBold, color = colors.accentEmerald)
                        Text(
                            text = IndianVedicFormatter.formatCurrency(marginResult.grossProfit),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 17.sp,
                            color = colors.accentEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Profit Margin (on SP):", color = colors.textPrimary)
                        Text(
                            text = "${marginResult.profitMarginPercent} %",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp,
                            color = colors.accentEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Markup Percentage (on CP):", color = colors.textPrimary)
                        Text(
                            text = "${marginResult.markupPercent} %",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp,
                            color = colors.accentAmber
                        )
                    }
                }
            }
        } else {
            // --- LOAN EMI CALCULATOR ---
            val principal = principalText.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val interestRate = interestRateText.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val tenureMonths = tenureMonthsText.toIntOrNull() ?: 1
            val emiResult = CommercialCalculatorEngine.calculateLoanEmi(principal, interestRate, tenureMonths)

            // Input 1: Loan Amount
            NeumorphicInputField(
                label = "Loan Amount (Principal)",
                value = principalText,
                onValueChange = { principalText = it },
                prefix = "₹"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input 2: Interest Rate
            NeumorphicInputField(
                label = "Annual Interest Rate (%)",
                value = interestRateText,
                onValueChange = { interestRateText = it },
                prefix = "%"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input 3: Tenure (Months)
            NeumorphicInputField(
                label = "Loan Tenure (Months)",
                value = tenureMonthsText,
                onValueChange = { tenureMonthsText = it },
                prefix = "📅"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Tenure Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(12 to "1 Yr", 24 to "2 Yrs", 36 to "3 Yrs", 60 to "5 Yrs").forEach { (months, label) ->
                    val isSelected = tenureMonthsText == months.toString()
                    NeumorphicButton(
                        text = label,
                        onClick = { tenureMonthsText = months.toString() },
                        modifier = Modifier.weight(1f).height(42.dp),
                        isSolidAccent = isSelected,
                        fontSize = 13
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Loan EMI Result Plate
            NeumorphicPlate(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp, elevation = 4.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text(
                        text = "Loan Repayment Schedule",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Monthly EMI:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.accentEmerald)
                        Text(
                            text = IndianVedicFormatter.formatCurrency(emiResult.monthlyEmi),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = colors.accentEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Interest:", color = colors.textSecondary, fontSize = 13.sp)
                        Text(
                            text = IndianVedicFormatter.formatCurrency(emiResult.totalInterest),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            color = colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Payable:", color = colors.textSecondary, fontSize = 13.sp)
                        Text(
                            text = IndianVedicFormatter.formatCurrency(emiResult.totalPayment),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NeumorphicInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeumorphicColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONCAVE,
                    cornerRadius = 14.dp,
                    elevation = 3.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.lcdWellBackground
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prefix,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentEmerald,
                    modifier = Modifier.padding(end = 8.dp)
                )
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(colors.accentEmerald),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

