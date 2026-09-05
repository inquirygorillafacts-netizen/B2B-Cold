package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.draw.clip
import com.example.model.SubscriptionState
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary

@Composable
fun LuxuryExecutiveHeader(
    callsDoneToday: Int,
    dailyGoal: Int,
    subscriptionState: SubscriptionState,
    onOpenSettings: () -> Unit,
    onOpenSubscription: () -> Unit,
    onSyncClick: () -> Unit = {},
    onOpenDeckStudio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val remaining = (dailyGoal - callsDoneToday).coerceAtLeast(0)
    val progress = if (dailyGoal > 0) (callsDoneToday.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("luxury_executive_header"),
        color = Color.White.copy(alpha = 0.98f),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: TODAY'S MOMENTUM + 3 / 10 + Gold Progress Bar (Matching Screenshot 2)
            Column(
                modifier = Modifier.testTag("today_momentum_header")
            ) {
                Text(
                    text = "TODAY'S MOMENTUM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$callsDoneToday / $dailyGoal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    // Sleek horizontal progress bar with gold fill
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFD97706))
                        )
                    }
                }
            }

            // RIGHT: Actions (VIP PRO Badge + Sync Tray + Deck Studio Tune + Settings)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 0. VIP PRO Pill Button (Image 1 match)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.2.dp, Color(0xFF16A34A)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpenSubscription() }
                        .testTag("header_vip_pro_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP Pro",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VIP PRO",
                            color = Color(0xFF16A34A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // 1. Sync Button (tray / sync icon)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    IconButton(
                        onClick = onSyncClick,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("header_sync_contacts_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync Contacts",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // 2. Deck Studio Button (10-Card Physics Dynamics)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    IconButton(
                        onClick = onOpenDeckStudio,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("header_deck_studio_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "10 Card Physics",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // 3. Settings Page Launcher Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LuxuryBlueContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, LuxuryBlue.copy(alpha = 0.3f)),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Open Settings",
                            tint = LuxuryBlue,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
