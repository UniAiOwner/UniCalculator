package com.unicalculator

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unicalculator.core.designsystem.component.NeumorphicHapticEngine
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
import kotlin.math.roundToInt

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
    val context = LocalContext.current
    val hapticEngine = remember { NeumorphicHapticEngine(context) }

    // Real-time continuous fractional position for synchronized bottom pill skating
    val currentFractional = pagerState.currentPage + pagerState.currentPageOffsetFraction

    // Real-time Detent Boundary Haptic + Sound Feedback
    var lastDetentPage by remember { mutableIntStateOf(0) }
    val currentRounded = currentFractional.roundToInt().coerceIn(0, 4)
    LaunchedEffect(currentRounded) {
        if (currentRounded != lastDetentPage) {
            hapticEngine.playSkateDetent()
            lastDetentPage = currentRounded
        }
    }

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

                    NeumorphicSlidingBottomBar(
                        tabs = tabs,
                        selectedTab = pagerState.currentPage,
                        fractionalPosition = currentFractional,
                        onFractionalDrag = { fraction ->
                            val targetPage = fraction.toInt().coerceIn(0, 4)
                            val offset = (fraction - targetPage).coerceIn(0f, 1f)
                            coroutineScope.launch {
                                pagerState.scrollToPage(targetPage, offset)
                            }
                        },
                        onDragEnd = { targetTab ->
                            if (targetTab == 4 && pagerState.currentPage != 4) {
                                historyFilter = HistoryFilter.ALL
                            }
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = targetTab,
                                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        onTabSelected = { index ->
                            if (index == 4 && pagerState.currentPage != 4) {
                                historyFilter = HistoryFilter.ALL
                            }
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = index,
                                    animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                                )
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
                                pagerState.animateScrollToPage(
                                    page = 4,
                                    animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        onToggleTheme = onToggleTheme
                    )
                    1 -> GSTProScreen(
                        onNavigateToHistory = {
                            historyFilter = HistoryFilter.GST_PRO
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = 4,
                                    animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        onToggleTheme = onToggleTheme
                    )
                    2 -> CashTallyScreen(
                        onNavigateToHistory = {
                            historyFilter = HistoryFilter.CASH_TALLY
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = 4,
                                    animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        onToggleTheme = onToggleTheme
                    )
                    3 -> BusinessToolsScreen(
                        onNavigateToHistory = {
                            historyFilter = HistoryFilter.TOOLS
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = 4,
                                    animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        onToggleTheme = onToggleTheme,
                        onNavigateToGstPro = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = 1,
                                    animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                                )
                            }
                        }
                    )
                    4 -> HistoryTapeScreen(initialFilter = historyFilter)
                }
            }
        }
    }
}
