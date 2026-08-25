package com.unicalculator.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionModelsTest {

    @Test
    fun testProPlanPricingAndPeriods() {
        assertEquals("₹29", ProPlanType.MONTHLY.price)
        assertEquals("/month", ProPlanType.MONTHLY.billingPeriod)

        assertEquals("₹199", ProPlanType.ANNUAL.price)
        assertEquals("/year", ProPlanType.ANNUAL.billingPeriod)
        assertTrue(ProPlanType.ANNUAL.badge?.contains("SAVE 45%") == true)

        assertEquals("₹499", ProPlanType.LIFETIME.price)
        assertEquals("one-time", ProPlanType.LIFETIME.billingPeriod)
        assertTrue(ProPlanType.LIFETIME.badge?.contains("LIFETIME") == true)
    }

    @Test
    fun testSubscriptionStatusEntitlements() {
        val trialActive = SubscriptionStatus.TrialActive(daysRemaining = 30, expiryDateFormatted = "25 Sep 2026")
        assertTrue(trialActive.isProOrTrialActive)
        assertEquals(30, trialActive.daysRemaining)

        val subscribedAnnual = SubscriptionStatus.Subscribed(plan = ProPlanType.ANNUAL, expiryDateFormatted = "25 Aug 2027")
        assertTrue(subscribedAnnual.isProOrTrialActive)
        assertEquals(ProPlanType.ANNUAL, subscribedAnnual.plan)

        val lifetime = SubscriptionStatus.LifetimePro(activationDateFormatted = "25 Aug 2026")
        assertTrue(lifetime.isProOrTrialActive)

        val expired = SubscriptionStatus.TrialExpired(expiredDateFormatted = "25 Aug 2026")
        assertFalse(expired.isProOrTrialActive)
    }
}
