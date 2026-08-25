package com.unicalculator.core.designsystem.component

import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.common.prefs.UniCalculatorPreferences
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.model.ProPlanType
import com.unicalculator.core.model.SubscriptionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeumorphicSubscriptionSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val prefs = remember { UniCalculatorPreferences.getInstance(context) }
    val subscriptionStatus by prefs.subscriptionStatus.collectAsState()
    var selectedPlan by remember { mutableStateOf(ProPlanType.ANNUAL) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeumorphicPlate(
                        modifier = Modifier.size(42.dp),
                        shape = NeumorphicShape.CONVEX,
                        cornerRadius = 14.dp
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(42.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Pro Crown",
                                tint = if (colors.isDark) Color(0xFFFFD700) else Color(0xFFD4AF37),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "UniCalculator Pro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "5-in-1 Professional Suite",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                NeumorphicPlate(
                    modifier = Modifier.size(38.dp),
                    shape = NeumorphicShape.CONVEX,
                    cornerRadius = 12.dp
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Status Banner Card
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONCAVE,
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when (val status = subscriptionStatus) {
                        is SubscriptionStatus.TrialActive -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎁 30-Day Free Trial Active",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen
                                )
                                Text(
                                    text = "${status.daysRemaining} Days Left",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (colors.isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
                                )
                            }
                            Text(
                                text = "All 5 workstations and features are 100% unlocked & free until ${status.expiryDateFormatted}.",
                                fontSize = 11.5.sp,
                                color = colors.textSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        is SubscriptionStatus.Subscribed -> {
                            Text(
                                text = "👑 Active Subscriber: ${status.plan.title}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen
                            )
                            Text(
                                text = "Your subscription is valid until ${status.expiryDateFormatted}.",
                                fontSize = 11.5.sp,
                                color = colors.textSecondary
                            )
                        }
                        is SubscriptionStatus.LifetimePro -> {
                            Text(
                                text = "👑 Lifetime Vyapar Pro Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen
                            )
                            Text(
                                text = "All current & future workstations unlocked permanently.",
                                fontSize = 11.5.sp,
                                color = colors.textSecondary
                            )
                        }
                        is SubscriptionStatus.TrialExpired -> {
                            Text(
                                text = "⚠️ 30-Day Free Trial Concluded",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6B6B)
                            )
                            Text(
                                text = "Standard Calculator remains 100% Free Forever! Upgrade to Pro below to unlock GST Pro, Cash Tally & Business Tools.",
                                fontSize = 11.5.sp,
                                color = colors.textSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Pro Features Checklist Plate
            NeumorphicPlate(
                modifier = Modifier.fillMaxWidth(),
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚡ What's Included in UniCalculator Pro:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    FeatureRow("🧾 GST Pro Invoicing Engine", "Forward & Reverse 50/50 tax split with invoice copies")
                    FeatureRow("💵 Cash Tally (रोकड़ खाता)", "Full RBI currency spectrum & 1-tap WhatsApp closing slips")
                    FeatureRow("💼 16 Business & Vedic Converters", "Loan EMI, Margin/Markup, Tola/Ratti Gold & Bigha Land")
                    FeatureRow("📜 Smart Multi-Tab Audit Tape", "Unlimited persistent SQLite tape & statement exports")
                    FeatureRow("🛡️ 100% Offline-First & Private", "Zero ads, zero tracking, total financial privacy")
                }
            }

            // Plan Selection Cards
            Text(
                text = "Choose Your Pro Plan (Nominal Pricing)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProPlanType.entries.forEach { plan ->
                    val isSelected = selectedPlan == plan
                    PlanCard(
                        plan = plan,
                        isSelected = isSelected,
                        onClick = { selectedPlan = plan }
                    )
                }
            }

            // Primary Action Button
            val isTrialActive = subscriptionStatus is SubscriptionStatus.TrialActive
            NeumorphicButton(
                text = if (isTrialActive) "🚀 Continue Free Trial (${selectedPlan.title})" else "✨ Unlock ${selectedPlan.title} (${selectedPlan.price})",
                onClick = {
                    if (isTrialActive) {
                        Toast.makeText(context, "Enjoy your 1-Month 100% Free Full-Access Trial!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    } else {
                        prefs.activateProPlan(selectedPlan)
                        Toast.makeText(context, "🎉 Welcome to UniCalculator ${selectedPlan.title}!", Toast.LENGTH_LONG).show()
                        onDismiss()
                    }
                },
                accentColor = RupeeEmeraldGreen,
                fontSize = 13,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )

            // Secondary Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔄 Restore Purchases",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary,
                    modifier = Modifier.clickable {
                        prefs.restorePurchases { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) onDismiss()
                        }
                    }
                )

                Text(
                    text = "Continue Free Calculator",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            // Trust Footnote
            Text(
                text = "© 2026 UniCore Technologies • Cancel Anytime • 100% Secure",
                fontSize = 10.5.sp,
                color = colors.textSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    title: String,
    subtitle: String
) {
    val colors = LocalNeumorphicColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = RupeeEmeraldGreen,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = colors.textSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: ProPlanType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalNeumorphicColors.current

    NeumorphicPlate(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = NeumorphicShape.CONVEX,
        elevation = if (isSelected) 5.dp else 2.dp,
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Custom Radio Dot
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) RupeeEmeraldGreen else colors.textSecondary.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plan.title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = plan.description,
                            fontSize = 10.5.sp,
                            color = colors.textSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = plan.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) (if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen) else colors.textPrimary
                    )
                    Text(
                        text = plan.billingPeriod,
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                }
            }

            val badge = plan.badge
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (colors.isDark) Color(0xFF1E3A2B) else Color(0xFFE8F5E9)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) Color(0xFF00FF9D) else RupeeEmeraldGreen
                    )
                }
            }
        }
    }
}
