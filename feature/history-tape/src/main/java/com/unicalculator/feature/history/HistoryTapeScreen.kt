package com.unicalculator.feature.history

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.CalculationType

enum class HistoryFilter(val title: String, val icon: ImageVector, val type: CalculationType?) {
    STANDARD("Standard", Icons.Default.Calculate, CalculationType.STANDARD_MATH),
    GST_PRO("GST Pro", Icons.Default.ReceiptLong, CalculationType.GST_FORWARD),
    CASH_TALLY("Cash Tally", Icons.Default.CurrencyRupee, CalculationType.CASH_TALLY),
    ALL("All History", Icons.Default.History, null)
}

data class HistoryTapeEntry(
    val id: Long,
    val type: CalculationType,
    val title: String,
    val expression: String,
    val result: String,
    val dateLabel: String = "Today",
    val memoNote: String? = null
)

@Composable
fun HistoryTapeScreen(
    initialFilter: HistoryFilter = HistoryFilter.ALL,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeumorphicColors.current
    var selectedFilter by remember { mutableStateOf(initialFilter) }

    // Multi-module calculation history store
    val allHistory = remember {
        listOf(
            HistoryTapeEntry(
                id = 1,
                type = CalculationType.STANDARD_MATH,
                title = "Standard Calculation",
                expression = "50,000 − 1,200",
                result = "₹ 48,800",
                dateLabel = "Today 02:25 PM"
            ),
            HistoryTapeEntry(
                id = 2,
                type = CalculationType.STANDARD_MATH,
                title = "Standard Calculation",
                expression = "10,000 × 5",
                result = "₹ 50,000",
                dateLabel = "Today 02:24 PM"
            ),
            HistoryTapeEntry(
                id = 3,
                type = CalculationType.STANDARD_MATH,
                title = "Standard Calculation",
                expression = "2,500 + 7,500",
                result = "₹ 10,000",
                dateLabel = "Today 02:22 PM"
            ),
            HistoryTapeEntry(
                id = 4,
                type = CalculationType.GST_FORWARD,
                title = "GST Pro (18% Intra-State)",
                expression = "1,25,000 + 18% GST (CGST: ₹11,250 | SGST: ₹11,250)",
                result = "₹ 1,47,500.00",
                dateLabel = "Today 01:15 PM"
            ),
            HistoryTapeEntry(
                id = 5,
                type = CalculationType.GST_REVERSE,
                title = "GST Pro Reverse Tax",
                expression = "45,000 − 18% Reverse GST (Base: ₹38,135.59)",
                result = "₹ 38,135.59",
                dateLabel = "Today 12:40 PM"
            ),
            HistoryTapeEntry(
                id = 6,
                type = CalculationType.CASH_TALLY,
                title = "Cash Closing Session",
                expression = "Closing Breakdown: 412 Notes (₹500: 250, ₹200: 80, ₹100: 150...)",
                result = "₹ 1,84,650.00",
                dateLabel = "Yesterday"
            )
        )
    }

    val filteredList = remember(selectedFilter, allHistory) {
        when (selectedFilter) {
            HistoryFilter.ALL -> allHistory
            HistoryFilter.STANDARD -> allHistory.filter { it.type == CalculationType.STANDARD_MATH }
            HistoryFilter.GST_PRO -> allHistory.filter { it.type == CalculationType.GST_FORWARD || it.type == CalculationType.GST_REVERSE }
            HistoryFilter.CASH_TALLY -> allHistory.filter { it.type == CalculationType.CASH_TALLY }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Calculation History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = when (selectedFilter) {
                        HistoryFilter.STANDARD -> "Standard Calculator Audit Trail"
                        HistoryFilter.GST_PRO -> "GST Tax Invoices & Breakdowns"
                        HistoryFilter.CASH_TALLY -> "Cash Denomination Tally Sessions"
                        HistoryFilter.ALL -> "Unified Master Audit Ledger"
                    },
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }

            // Total Count Badge
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
                    text = "${filteredList.size} Entries",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.accentEmerald
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Neumorphic Category Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HistoryFilter.entries) { filter ->
                val isSelected = selectedFilter == filter
                val activeColor = if (isSelected) RupeeEmeraldGreen else colors.textSecondary

                Box(
                    modifier = Modifier
                        .height(38.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

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
                            text = "Calculations performed in ${selectedFilter.title} will appear here automatically.",
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { item ->
                    val badgeBg = when (item.type) {
                        CalculationType.STANDARD_MATH -> RupeeEmeraldGreen.copy(alpha = 0.15f)
                        CalculationType.GST_FORWARD, CalculationType.GST_REVERSE -> Color(0xFF3498DB).copy(alpha = 0.15f)
                        CalculationType.CASH_TALLY -> Color(0xFFF39C12).copy(alpha = 0.15f)
                        else -> colors.textSecondary.copy(alpha = 0.15f)
                    }
                    val badgeColor = when (item.type) {
                        CalculationType.STANDARD_MATH -> RupeeEmeraldGreen
                        CalculationType.GST_FORWARD, CalculationType.GST_REVERSE -> Color(0xFF2980B9)
                        CalculationType.CASH_TALLY -> Color(0xFFD68910)
                        else -> colors.textSecondary
                    }

                    NeumorphicPlate(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp,
                        elevation = 3.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            // Top Row: Category Pill + Date
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
                                        text = item.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor
                                    )
                                }
                                Text(
                                    text = item.dateLabel,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Expression / Details
                            Text(
                                text = item.expression,
                                fontSize = 13.sp,
                                color = colors.textSecondary,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Grand Result
                            Text(
                                text = item.result,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

