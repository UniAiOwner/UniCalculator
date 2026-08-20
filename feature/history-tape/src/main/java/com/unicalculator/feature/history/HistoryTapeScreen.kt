package com.unicalculator.feature.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.DeleteRed
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HistoryFilter(val title: String, val icon: ImageVector) {
    ALL("All History", Icons.Default.History),
    STANDARD("Standard", Icons.Default.Calculate),
    GST_PRO("GST Pro", Icons.AutoMirrored.Filled.ReceiptLong),
    CASH_TALLY("Cash Tally", Icons.Default.CurrencyRupee),
    TOOLS("Tools & Units", Icons.Default.Tune)
}

@Composable
fun HistoryTapeScreen(
    initialFilter: HistoryFilter = HistoryFilter.ALL,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val repository = remember { LocalCalculationHistoryRepository(context) }
    val colors = LocalNeumorphicColors.current
    var selectedFilter by remember { mutableStateOf(initialFilter) }

    val rawHistoryList by repository.historyList.collectAsState()

    val filteredList = remember(selectedFilter, rawHistoryList) {
        when (selectedFilter) {
            HistoryFilter.ALL -> rawHistoryList
            HistoryFilter.STANDARD -> rawHistoryList.filter { it.type == CalculationType.STANDARD_MATH }
            HistoryFilter.GST_PRO -> rawHistoryList.filter {
                it.type == CalculationType.GST_FORWARD || it.type == CalculationType.GST_REVERSE
            }
            HistoryFilter.CASH_TALLY -> rawHistoryList.filter { it.type == CalculationType.CASH_TALLY }
            HistoryFilter.TOOLS -> rawHistoryList.filter {
                it.type == CalculationType.TOOLS_CONVERTER ||
                it.type == CalculationType.LOAN_EMI ||
                it.type == CalculationType.DISCOUNT_STACK ||
                it.type == CalculationType.MARGIN_MARKUP ||
                it.type == CalculationType.BMI_CALCULATOR ||
                it.type == CalculationType.DATE_AGE
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Calculation History",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textPrimary
                )
                Text(
                    text = when (selectedFilter) {
                        HistoryFilter.STANDARD -> "Standard Calculator Tape"
                        HistoryFilter.GST_PRO -> "GST Invoices & Tax Extraction"
                        HistoryFilter.CASH_TALLY -> "Cash Denomination Registers"
                        HistoryFilter.TOOLS -> "Units, Loan & Tools Log"
                        HistoryFilter.ALL -> "Master Audit Ledger"
                    },
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clear Filtered History Button
                if (filteredList.isNotEmpty()) {
                    NeumorphicIconButton(
                        icon = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        onClick = {
                            when (selectedFilter) {
                                HistoryFilter.ALL -> repository.clearAll()
                                HistoryFilter.STANDARD -> repository.deleteByTypes(listOf(CalculationType.STANDARD_MATH))
                                HistoryFilter.GST_PRO -> repository.deleteByTypes(listOf(CalculationType.GST_FORWARD, CalculationType.GST_REVERSE))
                                HistoryFilter.CASH_TALLY -> repository.deleteByTypes(listOf(CalculationType.CASH_TALLY))
                                HistoryFilter.TOOLS -> repository.deleteByTypes(listOf(
                                    CalculationType.TOOLS_CONVERTER,
                                    CalculationType.LOAN_EMI,
                                    CalculationType.DISCOUNT_STACK,
                                    CalculationType.MARGIN_MARKUP,
                                    CalculationType.BMI_CALCULATOR,
                                    CalculationType.DATE_AGE
                                ))
                            }
                            Toast.makeText(context, "${selectedFilter.title} history cleared", Toast.LENGTH_SHORT).show()
                        },
                        size = 36.dp,
                        iconSize = 18.dp,
                        iconTint = DeleteRed
                    )
                }

                // Count Badge
                Box(
                    modifier = Modifier
                        .neumorphic(
                            shape = NeumorphicShape.CONCAVE,
                            cornerRadius = 10.dp,
                            elevation = 2.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.lcdWellBackground
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${filteredList.size} Items",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.accentEmerald
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Category Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HistoryFilter.entries) { filter ->
                val isSelected = selectedFilter == filter
                val activeColor = if (isSelected) RupeeEmeraldGreen else colors.textSecondary

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .neumorphic(
                            shape = if (isSelected) NeumorphicShape.CONCAVE else NeumorphicShape.CONVEX,
                            cornerRadius = 12.dp,
                            elevation = if (isSelected) 2.dp else 4.dp,
                            lightShadowColor = colors.lightHighlight,
                            darkShadowColor = colors.darkShadow,
                            backgroundColor = colors.background
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = filter.icon,
                            contentDescription = filter.title,
                            tint = activeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = filter.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.textPrimary else colors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // History Items List / Empty State
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                NeumorphicPlate(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    cornerRadius = 20.dp,
                    elevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = selectedFilter.icon,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No ${selectedFilter.title} History Yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Calculations performed in ${selectedFilter.title} will be recorded here automatically.",
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { it.id }) { item ->
                    val badgeTitle = when (item.type) {
                        CalculationType.STANDARD_MATH -> "Standard"
                        CalculationType.GST_FORWARD -> "GST (+ Tax)"
                        CalculationType.GST_REVERSE -> "GST (- Tax)"
                        CalculationType.CASH_TALLY -> "Cash Tally"
                        CalculationType.LOAN_EMI -> "Loan EMI"
                        CalculationType.DISCOUNT_STACK -> "Discount"
                        CalculationType.MARGIN_MARKUP -> "Margin & Markup"
                        CalculationType.TOOLS_CONVERTER -> "Unit Converter"
                        CalculationType.BMI_CALCULATOR -> "BMI Health"
                        CalculationType.DATE_AGE -> "Date & Age"
                    }

                    val badgeBg = when (item.type) {
                        CalculationType.STANDARD_MATH -> RupeeEmeraldGreen.copy(alpha = 0.15f)
                        CalculationType.GST_FORWARD, CalculationType.GST_REVERSE -> Color(0xFF3498DB).copy(alpha = 0.15f)
                        CalculationType.CASH_TALLY -> Color(0xFFF39C12).copy(alpha = 0.15f)
                        else -> Color(0xFF9B59B6).copy(alpha = 0.15f)
                    }
                    val badgeColor = when (item.type) {
                        CalculationType.STANDARD_MATH -> RupeeEmeraldGreen
                        CalculationType.GST_FORWARD, CalculationType.GST_REVERSE -> Color(0xFF2980B9)
                        CalculationType.CASH_TALLY -> Color(0xFFD68910)
                        else -> Color(0xFF8E44AD)
                    }

                    NeumorphicPlate(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp,
                        elevation = 3.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            // Top Row: Category Pill + Date + Delete Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(badgeBg, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badgeTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dateFormat.format(Date(item.timestamp)),
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Item",
                                        tint = colors.textSecondary.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable {
                                                repository.deleteById(item.id)
                                            }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Expression / Details
                            Text(
                                text = item.formulaExpression,
                                fontSize = 13.sp,
                                color = colors.textSecondary,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Grand Result + Action Icons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.primaryResult,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = colors.textPrimary,
                                    modifier = Modifier.weight(1f)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = colors.textSecondary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString("${item.formulaExpression} = ${item.primaryResult}"))
                                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                            }
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = colors.accentEmerald,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable {
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, "Calculation:\n${item.formulaExpression}\nResult: ${item.primaryResult}")
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Calculation"))
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
