package com.unicalculator.core.designsystem.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.res.painterResource
import com.unicalculator.core.designsystem.R
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.common.prefs.UniCalculatorPreferences
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.GstSaffronAmber
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.SubscriptionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUniCalculatorSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    val colors = LocalNeumorphicColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "About",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = colors.textPrimary
                    )
                )
                NeumorphicIconButton(
                    icon = Icons.Default.Close,
                    contentDescription = "Close",
                    onClick = onDismiss,
                    size = 36.dp
                )
            }

            // 1. Master Brand Hero Lockup (UniCalculator Bharat)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 24.dp,
                        elevation = 6.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.background
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo_master),
                    contentDescription = "UniCalculator Master Logo",
                    modifier = Modifier.size(68.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "UniCalculator",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Default,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Calculate • Simplify • Grow",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "v1.0.0 Pro Edition",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = GstSaffronAmber
                )
            }

            // 2. Pro Status & 30-Day Free Trial Card
            var showSubscriptionSheet by remember { mutableStateOf(false) }
            val subscriptionStatus by prefs.subscriptionStatus.collectAsState()

            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = if (colors.isDark) Color(0xFFFFD700) else Color(0xFFD4AF37),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "UniCalculator Pro",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        when (val status = subscriptionStatus) {
                            is SubscriptionStatus.TrialActive -> {
                                Text(
                                    text = "🎁 ${status.daysRemaining} Days Left",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen
                                )
                            }
                            is SubscriptionStatus.Subscribed -> {
                                Text(
                                    text = "👑 ${status.plan.title}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen
                                )
                            }
                            is SubscriptionStatus.LifetimePro -> {
                                Text(
                                    text = "👑 Lifetime VIP",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (colors.isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
                                )
                            }
                            is SubscriptionStatus.TrialExpired -> {
                                Text(
                                    text = "Trial Expired",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF6B6B)
                                )
                            }
                        }
                    }

                    Text(
                        text = when (val status = subscriptionStatus) {
                            is SubscriptionStatus.TrialActive -> "You have 100% Free Full-Access to all 5 Workstations until ${status.expiryDateFormatted}."
                            is SubscriptionStatus.Subscribed -> "All Pro workstations unlocked until ${status.expiryDateFormatted}."
                            is SubscriptionStatus.LifetimePro -> "All Pro workstations permanently unlocked."
                            is SubscriptionStatus.TrialExpired -> "Standard Calculator is Free Forever! Upgrade to Pro for GST, Cash Tally & Tools."
                        },
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        lineHeight = 15.sp
                    )

                    NeumorphicButton(
                        text = "✨ View Pro Plans & Pricing",
                        onClick = { showSubscriptionSheet = true },
                        accentColor = RupeeEmeraldGreen,
                        fontSize = 11,
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    )
                }
            }

            if (showSubscriptionSheet) {
                NeumorphicSubscriptionSheet(onDismiss = { showSubscriptionSheet = false })
            }

            // 3. Publisher & Engineering Bio Card
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🏢 Publisher & Engineering",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "UniCore Technologies",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen
                    )
                    Text(
                        text = "📧 theunicoretech@gmail.com",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "Architect & Lead Engineer: Shoeb Ahmad\nEngineered with mathematical precision for Financial, GST, Retail & Scientific calculations.",
                        fontSize = 11.5.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            // 3. The 5 Core Workstations
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "⚡ 5 Dedicated Workstations",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    WorkstationRow(
                        icon = Icons.Default.Calculate,
                        title = "Standard Calculator",
                        description = "Exact BigDecimal arithmetic, repeated '=', and operator chaining."
                    )
                    WorkstationRow(
                        icon = Icons.Default.ReceiptLong,
                        title = "GST Pro Invoicing Engine",
                        description = "Forward '+GST' & Reverse '−GST' MRP extraction with live 50/50 tax splits."
                    )
                    WorkstationRow(
                        icon = Icons.Default.CurrencyRupee,
                        title = "Cash Tally (रोकड़ खाता)",
                        description = "Full RBI currency spectrum (₹500 to ₹1) & 1-tap WhatsApp closing slips."
                    )
                    WorkstationRow(
                        icon = Icons.Default.Tune,
                        title = "16 Business & Vedic Tools",
                        description = "Traditional Indian units (Tola, Bigha), Loan EMI, Margin, and Live Forex sync."
                    )
                    WorkstationRow(
                        icon = Icons.Default.History,
                        title = "Smart History Tape",
                        description = "Isolated audit tape & statement exports across all calculations."
                    )
                }
            }

            // 4. Privacy & Offline-First Security
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 18.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = RupeeEmeraldGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "100% Offline-First & Private",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Zero tracking • Zero ads • Zero telemetry. All calculation data stays strictly on your device.",
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // 5. Official Commercial Actions (Contact & Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicButton(
                    text = "✉️ Support",
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:theunicoretech@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "[UniCalculator] Feedback & Support")
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (e: Exception) {
                            val chooser = Intent.createChooser(emailIntent, "Send Email")
                            context.startActivity(chooser)
                        }
                    },
                    accentColor = RupeeEmeraldGreen,
                    fontSize = 12,
                    modifier = Modifier.weight(1f).height(46.dp)
                )

                NeumorphicButton(
                    text = "📤 Share App",
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Download UniCalculator — Professional 5-in-1 Calculator Suite by UniCore Technologies!"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share UniCalculator"))
                    },
                    fontSize = 12,
                    modifier = Modifier.weight(1f).height(46.dp)
                )
            }

            // 6. Copyright & Legal Footnote
            Text(
                text = "© 2026 UniCore Technologies • All rights reserved.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WorkstationRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    val colors = LocalNeumorphicColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(RupeeEmeraldGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RupeeEmeraldGreen,
                modifier = Modifier.size(16.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = description,
                fontSize = 10.5.sp,
                color = colors.textSecondary,
                lineHeight = 14.sp
            )
        }
    }
}
