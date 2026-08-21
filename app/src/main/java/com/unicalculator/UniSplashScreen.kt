package com.unicalculator

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.R
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import kotlinx.coroutines.delay

@Composable
fun UniSplashScreen(
    isDarkTheme: Boolean,
    onSplashComplete: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Entrance animation
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
        // Splash dwell time
        delay(1200)
        onSplashComplete()
    }

    val bgColor = if (isDarkTheme) Color(0xFF101416) else Color(0xFFF6F3EE)
    val textPrimary = if (isDarkTheme) Color(0xFFE6EDF3) else Color(0xFF1E293B)
    val textSecondary = if (isDarkTheme) Color(0xFF8B949E) else Color(0xFF64748B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale.value)
        ) {
            // 1. Pure 3D Recreated Master Emblem
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .shadow(
                        elevation = if (isDarkTheme) 20.dp else 12.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = if (isDarkTheme) Color(0xFF00FF9D).copy(alpha = 0.25f) else Color(0xFF000000).copy(alpha = 0.15f),
                        spotColor = if (isDarkTheme) RupeeEmeraldGreen else Color(0xFF00A86B).copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo_master),
                    contentDescription = "UniCalculator Master Emblem",
                    modifier = Modifier.size(110.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. Full Wordmark
            Text(
                text = "UniCalculator",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Bharat Accent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(2.dp)
                        .background(Color(0xFFFF9933))
                )
                Text(
                    text = "  BHARAT  ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 3.sp,
                    color = if (isDarkTheme) Color(0xFF00FF9D) else RupeeEmeraldGreen
                )
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(2.dp)
                        .background(Color(0xFFFF9933))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Taglines
            Text(
                text = "भारत का अपना स्मार्ट कैलकुलेटर",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFF9933)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Calculate • Simplify • Grow",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 5. Emerald -> Saffron Progress Beam
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isDarkTheme) Color(0xFF1E252B) else Color(0xFFE2DDD5))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(4.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    RupeeEmeraldGreen,
                                    Color(0xFFFF9933)
                                )
                            )
                        )
                )
            }
        }

        // Bottom National Heritage
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "🇮🇳 Made for Bharat • Built for You 🇮🇳",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = textSecondary
            )
        }
    }
}
