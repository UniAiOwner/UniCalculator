package com.unicalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.unicalculator.core.designsystem.component.NeumorphicSlidingBottomBar
import com.unicalculator.core.designsystem.component.NeumorphicTabItem
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.feature.calculator.GSTProScreen
import com.unicalculator.feature.calculator.StandardCalculatorScreen
import com.unicalculator.feature.cashtally.CashTallyScreen
import com.unicalculator.feature.history.HistoryFilter
import com.unicalculator.feature.history.HistoryTapeScreen
import com.unicalculator.feature.tools.BusinessToolsScreen
import kotlinx.coroutines.launch

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
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()
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
                            NeumorphicTabItem(
                                title = it.title,
                                icon = it.icon
                            )
                        }
                    }

                    // Tab Scrubber pattern:
                    // - fractionalPosition = null → pill manages its own drag state internally
                    // - onFractionalDrag = null → pager content never moves during drag
                    // - onDragEnd → instant scrollToPage (zero-latency snap, no slide animation)
                    // - onTabSelected → instant scrollToPage (zero-latency tap, no slide animation)
                    NeumorphicSlidingBottomBar(
                        tabs = tabs,
                        selectedTab = pagerState.currentPage,
                        fractionalPosition = null,
                        onFractionalDrag = null,
                        onDragEnd = { targetTab ->
                            if (targetTab == 4 && pagerState.currentPage != 4) {
                                historyFilter = HistoryFilter.ALL
                            }
                            coroutineScope.launch {
                                pagerState.scrollToPage(targetTab)
                            }
                        },
                        onTabSelected = { index ->
                            if (index == 4 && pagerState.currentPage != 4) {
                                historyFilter = HistoryFilter.ALL
                            }
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
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
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { page ->
                when (page) {
                    0 -> StandardCalculatorScreen(
                        onNavigateToHistory = {
                            historyFilter = HistoryFilter.STANDARD
                            coroutineScope.launch {
                                pagerState.scrollToPage(4)
                            }
                        },
                        onToggleTheme = onToggleTheme
                    )
                    1 -> GSTProScreen(
                        onNavigateToHistory = {
                            historyFilter = HistoryFilter.GST_PRO
                            coroutineScope.launch {
                                pagerState.scrollToPage(4)
                            }
                        },
                        onToggleTheme = onToggleTheme
                    )
                    2 -> CashTallyScreen(
                        onNavigateToHistory = {
                            historyFilter = HistoryFilter.CASH_TALLY
                            coroutineScope.launch {
                                pagerState.scrollToPage(4)
                            }
                        },
                        onToggleTheme = onToggleTheme
                    )
                    3 -> BusinessToolsScreen(
                        onNavigateToHistory = {
                            historyFilter = HistoryFilter.TOOLS
                            coroutineScope.launch {
                                pagerState.scrollToPage(4)
                            }
                        },
                        onToggleTheme = onToggleTheme,
                        onNavigateToGstPro = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(1)
                            }
                        }
                    )
                    4 -> HistoryTapeScreen(initialFilter = historyFilter)
                }
            }
        }
    }
}
