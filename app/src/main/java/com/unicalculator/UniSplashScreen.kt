package com.unicalculator

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.R
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UniSplashScreen(
    isDarkTheme: Boolean,
    onSplashComplete: () -> Unit
) {
    val masterScale = remember { Animatable(0.75f) }
    val masterAlpha = remember { Animatable(0f) }
    val orbitScale = remember { Animatable(0.4f) }
    val featuresAlpha = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0.1f) }

    LaunchedEffect(Unit) {
        // Staggered Spring Entrance
        launch {
            masterScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            masterAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        }
        launch {
            delay(150)
            orbitScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            delay(300)
            featuresAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
        }
        launch {
            progressAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
            )
        }

        delay(1900)
        onSplashComplete()
    }

    val bgColor = if (isDarkTheme) Color(0xFF0F1417) else Color(0xFFFAF7F0)
    val textPrimary = if (isDarkTheme) Color(0xFFE6EDF3) else Color(0xFF101828)
    val textSecondary = if (isDarkTheme) Color(0xFF8B949E) else Color(0xFF64748B)
    val tileBg = if (isDarkTheme) Color(0xFF1B2228) else Color(0xFFF3EFE6)
    val cardBorder = if (isDarkTheme) Color(0xFF263238) else Color(0xFFE8E2D5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        // 1. Subtle Heritage Architectural Skyline at the bottom
        Image(
            painter = painterResource(id = R.drawable.heritage_skyline),
            contentDescription = "Indian Heritage Skyline",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(if (isDarkTheme) 0.20f else 0.40f)
        )

        // 2. Central Content Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .scale(masterScale.value)
                .alpha(masterAlpha.value)
        ) {
            // A. Hero Visual Area: 5 Orbit Floating Tokens + Central 3D Emblem
            Box(
                modifier = Modifier
                    .size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Soft Photon Halo Aura behind the hero emblem
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isDarkTheme) Color(0xFF00FF9D).copy(alpha = 0.25f) else Color(0xFF00A86B).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 5 Floating Radial Orbit Tokens
                val orbitTokens = listOf(
                    OrbitToken(text = "₹", angle = -42f, xOffset = (-85).dp, yOffset = (-35).dp),
                    OrbitToken(text = "%", angle = -20f, xOffset = (-48).dp, yOffset = (-80).dp),
                    OrbitToken(icon = Icons.Default.Calculate, angle = 0f, xOffset = 0.dp, yOffset = (-95).dp),
                    OrbitToken(text = "₹", angle = 20f, xOffset = 48.dp, yOffset = (-80).dp),
                    OrbitToken(icon = Icons.AutoMirrored.Filled.ReceiptLong, angle = 42f, xOffset = 85.dp, yOffset = (-35).dp)
                )

                orbitTokens.forEach { token ->
                    Box(
                        modifier = Modifier
                            .offset(x = token.xOffset, y = token.yOffset)
                            .scale(orbitScale.value)
                            .rotate(token.angle)
                            .size(38.dp)
                            .shadow(
                                elevation = if (isDarkTheme) 8.dp else 4.dp,
                                shape = RoundedCornerShape(10.dp),
                                ambientColor = Color.Black.copy(alpha = 0.15f)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(tileBg)
                            .border(1.dp, cardBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (token.text != null) {
                            Text(
                                text = token.text,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color(0xFF80E5A8) else Color(0xFF2E6F40)
                            )
                        } else if (token.icon != null) {
                            Icon(
                                imageVector = token.icon,
                                contentDescription = null,
                                tint = if (isDarkTheme) Color(0xFF80E5A8) else Color(0xFF2E6F40),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Central 3D Master Reconstructed Emblem
                Image(
                    painter = painterResource(id = R.drawable.app_logo_master),
                    contentDescription = "UniCalculator Master 3D Emblem",
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(
                            elevation = if (isDarkTheme) 20.dp else 12.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = if (isDarkTheme) Color(0xFF00FF9D).copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f),
                            spotColor = if (isDarkTheme) RupeeEmeraldGreen else Color(0xFF00A86B).copy(alpha = 0.35f)
                        )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // B. Flagship Wordmark
            Text(
                text = "UniCalculator",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Default,
                letterSpacing = 0.5.sp,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Professional Calculator Suite",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = if (isDarkTheme) Color(0xFF00FF9D) else RupeeEmeraldGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Calculate • Simplify • Grow",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // D. 5-Workstation Feature Badges Row (as seen in master mockup)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(featuresAlpha.value),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureBadgeItem(
                    title = "Standard\nCalculator",
                    icon = Icons.Default.Calculate,
                    iconColor = Color(0xFF00A86B),
                    textColor = textSecondary
                )
                FeatureBadgeItem(
                    title = "GST\nPro",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    iconColor = Color(0xFF0284C7),
                    textColor = textSecondary
                )
                FeatureBadgeItem(
                    title = "Cash\nTally",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconColor = Color(0xFFEA580C),
                    textColor = textSecondary
                )
                FeatureBadgeItem(
                    title = "Powerful\nTools",
                    icon = Icons.Default.GridView,
                    iconColor = Color(0xFF8B5CF6),
                    textColor = textSecondary
                )
                FeatureBadgeItem(
                    title = "Smart\nHistory",
                    icon = Icons.Default.History,
                    iconColor = Color(0xFF10B981),
                    textColor = textSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // E. Emerald -> Saffron Smooth Progress Beam
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (isDarkTheme) Color(0xFF1E252B) else Color(0xFFE2DDD5))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnim.value)
                        .height(4.5.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(3.dp))
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

        // 3. Bottom National Heritage Glass Pill
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .shadow(
                    elevation = if (isDarkTheme) 6.dp else 3.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDarkTheme) Color(0xFF1A2228).copy(alpha = 0.9f) else Color(0xFFF5EFE4).copy(alpha = 0.9f))
                .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⚡ Powered by UniCore Technologies",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.4.sp,
                color = textPrimary
            )
        }
    }
}

private data class OrbitToken(
    val text: String? = null,
    val icon: ImageVector? = null,
    val angle: Float,
    val xOffset: androidx.compose.ui.unit.Dp,
    val yOffset: androidx.compose.ui.unit.Dp
)

@Composable
private fun FeatureBadgeItem(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(62.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp), ambientColor = Color.Black.copy(alpha = 0.08f))
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.12f))
                .border(1.dp, iconColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = title,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            lineHeight = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

