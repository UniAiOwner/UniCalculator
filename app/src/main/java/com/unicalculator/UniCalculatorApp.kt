package com.unicalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.feature.calculator.StandardCalculatorScreen
import com.unicalculator.feature.cashtally.CashTallyScreen
import com.unicalculator.feature.history.HistoryTapeScreen
import com.unicalculator.feature.tools.BusinessToolsScreen

enum class MainTab(val title: String, val icon: ImageVector) {
    STANDARD("Standard", Icons.Default.Calculate),
    GST_PRO("GST Pro", Icons.Default.ReceiptLong),
    CASH_TALLY("Cash Tally", Icons.Default.CurrencyRupee),
    TOOLS("Tools", Icons.Default.Tune),
    HISTORY("History", Icons.Default.History)
}

@Composable
fun UniCalculatorApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val colors = LocalNeumorphicColors.current

    Scaffold(
        bottomBar = {
            // Sculpted Neumorphic Bottom Navigation Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 24.dp,
                        elevation = 6.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainTab.entries.forEachIndexed { index, tab ->
                        val isSelected = selectedTab == index
                        val activeColor = if (isSelected) RupeeEmeraldGreen else colors.textSecondary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .neumorphic(
                                    shape = if (isSelected) NeumorphicShape.CONCAVE else NeumorphicShape.FLAT,
                                    cornerRadius = 16.dp,
                                    elevation = if (isSelected) 3.dp else 0.dp,
                                    lightShadowColor = colors.lightHighlight,
                                    darkShadowColor = colors.darkShadow,
                                    backgroundColor = colors.background
                                )
                                .clickable { selectedTab = index }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = activeColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = activeColor
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> StandardCalculatorScreen()
                1 -> StandardCalculatorScreen() // GST Pro is unified with quick forward/reverse slabs
                2 -> CashTallyScreen()
                3 -> BusinessToolsScreen()
                4 -> HistoryTapeScreen()
            }
        }
    }
}
