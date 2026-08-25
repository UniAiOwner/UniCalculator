package com.unicalculator.core.model

enum class ProPlanType(
    val title: String,
    val price: String,
    val billingPeriod: String,
    val description: String,
    val badge: String? = null
) {
    MONTHLY(
        title = "Monthly Pro",
        price = "₹29",
        billingPeriod = "/month",
        description = "Flexible monthly billing for active shopkeepers & users",
        badge = null
    ),
    ANNUAL(
        title = "Annual Pro",
        price = "₹199",
        billingPeriod = "/year",
        description = "Just ~₹16.50/month • Best value for Indian Vyaparis",
        badge = "⭐ SAVE 45% • BEST VALUE"
    ),
    LIFETIME(
        title = "Lifetime Vyapar Pro",
        price = "₹499",
        billingPeriod = "one-time",
        description = "Pay once, own all current & future workstations forever",
        badge = "👑 VIP LIFETIME"
    )
}

sealed interface SubscriptionStatus {
    val isProOrTrialActive: Boolean

    data class TrialActive(
        val daysRemaining: Int,
        val expiryDateFormatted: String,
        val totalTrialDays: Int = 30
    ) : SubscriptionStatus {
        override val isProOrTrialActive: Boolean = true
    }

    data class Subscribed(
        val plan: ProPlanType,
        val expiryDateFormatted: String,
        val isAutoRenewing: Boolean = true
    ) : SubscriptionStatus {
        override val isProOrTrialActive: Boolean = true
    }

    data class LifetimePro(
        val activationDateFormatted: String
    ) : SubscriptionStatus {
        override val isProOrTrialActive: Boolean = true
    }

    data class TrialExpired(
        val expiredDateFormatted: String
    ) : SubscriptionStatus {
        override val isProOrTrialActive: Boolean = false
    }
}
