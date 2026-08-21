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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.feature.calculator.GSTProScreen
import com.unicalculator.feature.calculator.StandardCalculatorScreen
import com.unicalculator.feature.cashtally.CashTallyScreen
import com.unicalculator.feature.history.HistoryFilter
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
fun UniCalculatorApp(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var historyFilter by remember { mutableStateOf(HistoryFilter.ALL) }
    val colors = LocalNeumorphicColors.current

    Scaffold(
        bottomBar = {
            // Sculpted Neumorphic Sliding Bottom Navigation Bar with Edge-to-Edge Safe Padding
            Surface(
                color = colors.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    val tabs = remember {
                        MainTab.entries.map {
                            com.unicalculator.core.designsystem.component.NeumorphicTabItem(
                                title = it.title,
                                icon = it.icon
                            )
                        }
                    }

                    com.unicalculator.core.designsystem.component.NeumorphicSlidingBottomBar(
                        tabs = tabs,
                        selectedTab = selectedTab,
                        onTabSelected = { index ->
                            if (index == 4 && selectedTab != 4) {
                                historyFilter = HistoryFilter.ALL
                            }
                            selectedTab = index
                        }
                    )
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
                0 -> StandardCalculatorScreen(
                    onNavigateToHistory = {
                        historyFilter = HistoryFilter.STANDARD
                        selectedTab = 4
                    },
                    onToggleTheme = onToggleTheme
                )
                1 -> GSTProScreen(
                    onNavigateToHistory = {
                        historyFilter = HistoryFilter.GST_PRO
                        selectedTab = 4
                    },
                    onToggleTheme = onToggleTheme
                )
                2 -> CashTallyScreen(
                    onNavigateToHistory = {
                        historyFilter = HistoryFilter.CASH_TALLY
                        selectedTab = 4
                    },
                    onToggleTheme = onToggleTheme
                )
                3 -> BusinessToolsScreen(
                    onNavigateToHistory = {
                        historyFilter = HistoryFilter.TOOLS
                        selectedTab = 4
                    },
                    onToggleTheme = onToggleTheme,
                    onNavigateToGstPro = { selectedTab = 1 }
                )
                4 -> HistoryTapeScreen(initialFilter = historyFilter)
            }
        }
    }
}
