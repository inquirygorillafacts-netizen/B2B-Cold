package com.example

import com.example.model.AVAILABLE_SUBSCRIPTION_PLANS
import com.example.model.SubscriptionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionPlanTest {

    @Test
    fun testSubscriptionPlansConfiguration() {
        assertEquals("There should be 3 subscription plans (49, 199, 499)", 3, AVAILABLE_SUBSCRIPTION_PLANS.size)

        val starter = AVAILABLE_SUBSCRIPTION_PLANS.find { it.price == 49 }
        assertNotNull("Starter ₹49 plan must exist", starter)

        val pro = AVAILABLE_SUBSCRIPTION_PLANS.find { it.price == 199 }
        assertNotNull("Pro ₹199 plan must exist", pro)
        assertTrue("Pro ₹199 plan must be marked as popular", pro?.isPopular == true)

        val elite = AVAILABLE_SUBSCRIPTION_PLANS.find { it.price == 499 }
        assertNotNull("Elite ₹499 plan must exist", elite)
    }

    @Test
    fun testDefaultSubscriptionState() {
        val state = SubscriptionState()
        assertTrue("Default trial should be active", state.isTrialActive)
        assertEquals(60, state.trialDaysRemaining)
    }

    @Test
    fun testSnoozeTimestampCalculation() {
        val now = 1700000000000L
        val days = 3
        val expectedSnoozeUntil = now + (days * 24 * 60 * 60 * 1000L)
        assertEquals(now + 259200000L, expectedSnoozeUntil)
    }
}
