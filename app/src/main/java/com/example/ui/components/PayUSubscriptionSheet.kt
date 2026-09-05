package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AVAILABLE_SUBSCRIPTION_PLANS
import com.example.model.SubscriptionPlan
import com.example.model.SubscriptionState
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryGoldBg
import com.example.ui.theme.LuxuryGoldBorder
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayUSubscriptionSheet(
    subscriptionState: SubscriptionState,
    onActivatePlan: (SubscriptionPlan, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Default to the popular 199 plan as requested
    var selectedPlan by remember {
        mutableStateOf(AVAILABLE_SUBSCRIPTION_PLANS.firstOrNull { it.isPopular } ?: AVAILABLE_SUBSCRIPTION_PLANS[0])
    }
    var selectedPaymentMethod by remember { mutableStateOf("UPI") } // UPI, CARD, NETBANKING
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentSuccessTxId by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        contentColor = LuxuryTextPrimary,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
                .testTag("payu_subscription_sheet")
        ) {
            // Header Row with PayU badge & Close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF00B9F5).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF00B9F5).copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "PayU GATEWAY",
                            color = Color(0xFF007298),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "256-bit SSL Secure",
                        color = LuxuryTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LuxuryTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Free Trial Banner (2 Months Free Trial)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (subscriptionState.isSubscribed) Color(0xFFF0FDF4) else Color(0xFFEFF6FF),
                border = BorderStroke(
                    1.dp,
                    if (subscriptionState.isSubscribed) LuxuryEmerald.copy(alpha = 0.4f) else LuxuryBlue.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (subscriptionState.isSubscribed) LuxuryEmerald.copy(alpha = 0.15f)
                                else LuxuryBlue.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (subscriptionState.isSubscribed) Icons.Default.Verified else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (subscriptionState.isSubscribed) LuxuryEmerald else LuxuryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (subscriptionState.isSubscribed) "Active Subscription: ${subscriptionState.activePlanName}"
                            else "2-Month Free Trial Active",
                            color = LuxuryTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (subscriptionState.isSubscribed) "Full corporate cold calling deck unlocked"
                            else "${subscriptionState.trialDaysRemaining} days remaining of full free access. Upgrade anytime!",
                            color = LuxuryTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SELECT MEMBERSHIP PLAN",
                color = LuxuryTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 3 Subscription Plans (₹49, ₹199 Popular, ₹499)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AVAILABLE_SUBSCRIPTION_PLANS.forEach { plan ->
                    val isSelected = selectedPlan.id == plan.id

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                selectedPlan = plan
                                paymentSuccessTxId = null
                            }
                            .testTag("plan_card_${plan.id}"),
                        color = when {
                            isSelected && plan.isPopular -> Color(0xFFFFFBEB)
                            isSelected -> Color(0xFFF0FDF4)
                            else -> Color(0xFFF8FAFC)
                        },
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            when {
                                isSelected && plan.isPopular -> Color(0xFFF59E0B)
                                isSelected -> LuxuryEmerald
                                plan.isPopular -> Color(0xFFFCD34D)
                                else -> Color(0xFFE2E8F0)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            if (plan.savingsBadge != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (plan.isPopular) Color(0xFFF59E0B) else LuxuryBlue,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = plan.savingsBadge,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = plan.title,
                                        color = LuxuryTextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = plan.durationText,
                                        color = if (plan.isPopular) Color(0xFFB45309) else LuxuryTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "₹${plan.price}",
                                        color = if (plan.isPopular) Color(0xFFB45309) else LuxuryTextPrimary,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = " only",
                                        color = LuxuryTextMuted,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = plan.description,
                                color = LuxuryTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Payment Methods Selection (PayU Gateway)
            Text(
                text = "PAYMENT METHOD (PayU SECURED)",
                color = LuxuryTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("UPI", "UPI / GPay", Icons.Default.QrCode),
                    Triple("CARD", "Cards", Icons.Default.CreditCard),
                    Triple("NETBANKING", "NetBanking", Icons.Default.Security)
                ).forEach { (key, label, icon) ->
                    val isSelected = selectedPaymentMethod == key
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPaymentMethod = key },
                        color = if (isSelected) Color(0xFF007298).copy(alpha = 0.1f) else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF007298) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color(0xFF007298) else LuxuryTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF007298) else LuxuryTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment success or Checkout Button
            if (paymentSuccessTxId != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, LuxuryEmerald)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = LuxuryEmerald,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Payment Completed Successfully!",
                            color = LuxuryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Plan: ${selectedPlan.title} (₹${selectedPlan.price})",
                            color = LuxuryTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "PayU TxID: $paymentSuccessTxId",
                            color = LuxuryTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        isProcessingPayment = true
                        coroutineScope.launch {
                            delay(1400) // Realistic PayU gateway transaction processing
                            val txId = "PAYU_" + UUID.randomUUID().toString().take(8).uppercase()
                            paymentSuccessTxId = txId
                            isProcessingPayment = false
                            onActivatePlan(selectedPlan, txId)
                        }
                    },
                    enabled = !isProcessingPayment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("payu_checkout_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPlan.isPopular) Color(0xFF059669) else LuxuryBlue
                    )
                ) {
                    if (isProcessingPayment) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Connecting PayU Gateway...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pay ₹${selectedPlan.price} via PayU Gateway",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
