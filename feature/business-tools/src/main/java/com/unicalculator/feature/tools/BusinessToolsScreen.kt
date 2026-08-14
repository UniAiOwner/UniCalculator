package com.unicalculator.feature.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.designsystem.component.NeumorphicGstPill
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.math.CommercialCalculatorEngine
import java.math.BigDecimal

@Composable
fun BusinessToolsScreen(modifier: Modifier = Modifier) {
    val colors = LocalNeumorphicColors.current
    var selectedTool by remember { mutableStateOf(0) } // 0: Margin/Markup, 1: Loan EMI

    var costPrice by remember { mutableStateOf(BigDecimal("1200")) }
    var sellingPrice by remember { mutableStateOf(BigDecimal("1600")) }
    var principal by remember { mutableStateOf(BigDecimal("500000")) }
    var interestRate by remember { mutableStateOf(BigDecimal("8.5")) }
    var tenureMonths by remember { mutableStateOf(36) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTool == 0) {
            val marginResult = CommercialCalculatorEngine.calculateMarginMarkup(costPrice, sellingPrice)

            NeumorphicPlate(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Margin & Markup Solver", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cost Price (CP):", color = colors.textSecondary)
                        Text(IndianVedicFormatter.formatCurrency(costPrice), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Selling Price (SP):", color = colors.textSecondary)
                        Text(IndianVedicFormatter.formatCurrency(sellingPrice), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gross Profit:", fontWeight = FontWeight.Bold, color = colors.accentEmerald)
                        Text(IndianVedicFormatter.formatCurrency(marginResult.grossProfit), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.accentEmerald)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Profit Margin (on SP):", color = colors.textPrimary)
                        Text("${marginResult.profitMarginPercent} %", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.accentEmerald)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Markup (on CP):", color = colors.textPrimary)
                        Text("${marginResult.markupPercent} %", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.accentAmber)
                    }
                }
            }
        } else {
            val emiResult = CommercialCalculatorEngine.calculateLoanEmi(principal, interestRate, tenureMonths)

            NeumorphicPlate(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Loan EMI Calculator", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Loan Amount:", color = colors.textSecondary)
                        Text(IndianVedicFormatter.formatCurrency(principal), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Interest Rate:", color = colors.textSecondary)
                        Text("$interestRate % p.a.", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tenure:", color = colors.textSecondary)
                        Text("$tenureMonths Months", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Monthly EMI:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.accentEmerald)
                        Text(IndianVedicFormatter.formatCurrency(emiResult.monthlyEmi), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.accentEmerald)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Interest:", color = colors.textSecondary)
                        Text(IndianVedicFormatter.formatCurrency(emiResult.totalInterest), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = colors.textPrimary)
                    }
                }
            }
        }
    }
}
