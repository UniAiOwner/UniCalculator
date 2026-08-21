package com.unicalculator

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.unicalculator.core.common.prefs.UniCalculatorPreferences
import com.unicalculator.core.designsystem.theme.UniCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs = remember { UniCalculatorPreferences.getInstance(applicationContext) }
            val isDarkTheme by prefs.isDarkMode.collectAsState()
            val keepScreenAwake by prefs.keepScreenAwake.collectAsState()
            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(keepScreenAwake) {
                if (keepScreenAwake) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            UniCalculatorTheme(darkTheme = isDarkTheme) {
                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(durationMillis = 400),
                    label = "splashTransition"
                ) { isSplash ->
                    if (isSplash) {
                        UniSplashScreen(
                            isDarkTheme = isDarkTheme,
                            onSplashComplete = { showSplash = false }
                        )
                    } else {
                        UniCalculatorApp(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { prefs.setIsDarkMode(!isDarkTheme) }
                        )
                    }
                }
            }
        }
    }
}
