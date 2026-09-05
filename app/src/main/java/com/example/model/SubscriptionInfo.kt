package com.example.model

data class SubscriptionPlan(
    val id: String,
    val title: String,
    val price: Int,
    val durationText: String,
    val description: String,
    val isPopular: Boolean = false,
    val savingsBadge: String? = null
)

val AVAILABLE_SUBSCRIPTION_PLANS = listOf(
    SubscriptionPlan(
        id = "starter_49",
        title = "Starter Plan",
        price = 49,
        durationText = "1 Month Access",
        description = "Essential client cards & smart contact dialer",
        isPopular = false,
        savingsBadge = null
    ),
    SubscriptionPlan(
        id = "pro_199",
        title = "Pro Executive",
        price = 199,
        durationText = "3 Months Access",
        description = "Unlimited client rotations, 1-hour cache sync, voice notes & VIP features",
        isPopular = true,
        savingsBadge = "MOST POPULAR • BEST VALUE"
    ),
    SubscriptionPlan(
        id = "elite_499",
        title = "Elite Unlimited",
        price = 499,
        durationText = "1 Year Access",
        description = "Complete corporate rolodex, priority support & unlimited cold calling",
        isPopular = false,
        savingsBadge = "MAX SAVINGS 60%"
    )
)

data class SubscriptionState(
    val isSubscribed: Boolean = false,
    val activePlanName: String = "Free Trial",
    val activePrice: Int = 0,
    val isTrialActive: Boolean = true,
    val trialDaysRemaining: Int = 60,
    val lastTransactionId: String? = null
)
