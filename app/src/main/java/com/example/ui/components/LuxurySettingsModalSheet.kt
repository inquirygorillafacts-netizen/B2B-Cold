package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClientEntity
import com.example.model.CardAnimationStyle
import com.example.ui.theme.EmeraldCallGradient
import com.example.ui.theme.LightGlassBorderGlow
import com.example.ui.theme.LightGlassBorderStroke
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryEmeraldContainer
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryGoldBg
import com.example.ui.theme.LuxuryGoldBorder
import com.example.ui.theme.LuxuryLightCanvasAlt
import com.example.ui.theme.LuxuryLightSurface
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxurySettingsModalSheet(
    selectedStyle: CardAnimationStyle,
    dailyGoal: Int,
    allClients: List<ClientEntity>,
    onSelectStyle: (CardAnimationStyle) -> Unit,
    onSelectDailyGoal: (Int) -> Unit,
    onToggleClient: (String, Boolean) -> Unit,
    onSelectAllClients: (Boolean) -> Unit,
    onResetOnboarding: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSection by remember { mutableIntStateOf(0) }

    // Staged temporary selection so user can pick, test, and tap "APPLY & SAVE SETTINGS"
    var tempSelectedStyle by remember(selectedStyle) { mutableStateOf(selectedStyle) }
    var tempDailyGoal by remember(dailyGoal) { mutableIntStateOf(dailyGoal) }
    var saveSuccessNotification by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        contentColor = LuxuryTextPrimary,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .testTag("luxury_settings_modal")
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DECK STUDIO & CONTROLS",
                        color = LuxuryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Customize Deck Experience",
                        color = LuxuryTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LuxuryTextSecondary
                    )
                }
            }

            // Tabs: 10 Card Physics / Deck Filter / Goals & Tour
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = Color(0xFFF8FAFC),
                contentColor = LuxuryBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                        color = LuxuryBlue,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = {
                        Text(
                            text = "10 Card Physics",
                            fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedSection == 0) LuxuryBlue else LuxuryTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = {
                        Text(
                            text = "Deck Filter",
                            fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedSection == 1) LuxuryBlue else LuxuryTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = {
                        Text(
                            text = "Goals & Tour",
                            fontWeight = if (selectedSection == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedSection == 2) LuxuryBlue else LuxuryTextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedSection) {
                0 -> {
                    // SECTION 0: 10 Card Physics & Animation Styles
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "CHOOSE CARD SWIPE DYNAMICS",
                            color = LuxuryTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(340.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(CardAnimationStyle.values()) { style ->
                                val isSelected = tempSelectedStyle == style
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            tempSelectedStyle = style
                                            saveSuccessNotification = false
                                        }
                                        .testTag("style_item_${style.name}"),
                                    color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) LuxuryBlue else LightGlassBorderStroke
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val icon = getAnimationIcon(style)
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) LuxuryBlue else Color(0xFFE2E8F0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else LuxuryTextSecondary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = style.title,
                                                color = if (isSelected) LuxuryBlue else LuxuryTextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = style.subtitle,
                                                color = LuxuryTextSecondary,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                        }

                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .clip(CircleShape)
                                                    .background(LuxuryBlue),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Explicit SAVE / APPLY ANIMATION BUTTON
                        Button(
                            onClick = {
                                onSelectStyle(tempSelectedStyle)
                                saveSuccessNotification = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("apply_animation_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (saveSuccessNotification) LuxuryEmerald else LuxuryBlue
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (saveSuccessNotification) Icons.Default.Done else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (saveSuccessNotification) "SAVED & APPLIED TO DECK ✓" else "APPLY & SAVE ANIMATION",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // SECTION 1: Client Deck Filter (Tick / Untick)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val activeCount = allClients.count { it.isInRotation }
                            Text(
                                text = "$activeCount OF ${allClients.size} ACTIVE IN DECK",
                                color = LuxuryEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Row {
                                Text(
                                    text = "Select All",
                                    color = LuxuryBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { onSelectAllClients(true) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Text(text = "•", color = LuxuryTextMuted)
                                Text(
                                    text = "Clear All",
                                    color = LuxuryTextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clickable { onSelectAllClients(false) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(allClients, key = { it.id }) { client ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp)),
                                    color = if (client.isInRotation) Color.White else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        1.dp,
                                        if (client.isInRotation) LightGlassBorderStroke else Color(0xFFCBD5E1)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = client.isInRotation,
                                            onCheckedChange = { isChecked ->
                                                onToggleClient(client.id, isChecked)
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = LuxuryEmerald,
                                                uncheckedColor = LuxuryTextMuted,
                                                checkmarkColor = Color.White
                                            )
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = client.name,
                                                color = LuxuryTextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${client.designation} • ${client.company}",
                                                color = LuxuryTextSecondary,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = LuxuryGoldBg,
                                            border = BorderStroke(1.dp, LuxuryGoldBorder)
                                        ) {
                                            Text(
                                                text = client.dealSize,
                                                color = LuxuryGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // SECTION 2: Daily Calling Goal & Replay Onboarding
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "DAILY RELATIONSHIP TARGET",
                            color = LuxuryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Choose your daily call target to maintain executive relationship momentum.",
                            color = LuxuryTextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(5, 8, 10, 15).forEach { goal ->
                                val isSelected = tempDailyGoal == goal
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            tempDailyGoal = goal
                                            onSelectDailyGoal(goal)
                                        }
                                        .testTag("goal_chip_$goal"),
                                    color = if (isSelected) LuxuryBlue else Color(0xFFF1F5F9),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) LuxuryBlue else LightGlassBorderStroke
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$goal Calls",
                                            color = if (isSelected) Color.White else LuxuryTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "5-SLIDE EXECUTIVE ONBOARDING",
                            color = LuxuryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Replay the colorful 5-slide philosophy and reset your VIP rolodex onboarding.",
                            color = LuxuryTextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onResetOnboarding()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("replay_onboarding_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, LuxuryBlue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = LuxuryBlue
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = LuxuryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Replay Colorful 5-Slide Experience",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getAnimationIcon(style: CardAnimationStyle): ImageVector {
    return when (style) {
        CardAnimationStyle.LIQUID_GLASS_STACK -> Icons.Default.Layers
        CardAnimationStyle.IOS_TINDER_SNAP_3D -> Icons.Default.Swipe
        CardAnimationStyle.CUBE_3D_ROTATION -> Icons.Default.ViewInAr
        CardAnimationStyle.DEPTH_FLIP -> Icons.Default.Flip
        CardAnimationStyle.FLY_OUT_PHYSICS_SPRING -> Icons.Default.RocketLaunch
        CardAnimationStyle.FADE_SCALE_MORPH -> Icons.Default.BlurOn
        CardAnimationStyle.VELVET_SLIDE_REVEAL -> Icons.Default.AutoAwesome
        CardAnimationStyle.CARD_PEEL_EFFECT -> Icons.Default.ContentCopy
        CardAnimationStyle.PARALLAX_HOVER_GLIDE -> Icons.Default.Speed
        CardAnimationStyle.SPRING_BOUNCY_SNAP -> Icons.Default.Animation
    }
}
