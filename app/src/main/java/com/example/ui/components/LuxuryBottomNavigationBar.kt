package com.example.ui.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary

@Composable
fun LuxuryBottomNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onOpenDialpad: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("luxury_bottom_navigation_bar"),
        color = Color.White,
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // TAB 1: Deck (Home)
            BottomNavItem(
                label = "Deck",
                icon = if (currentTab == AppTab.HOME) Icons.Filled.Style else Icons.Outlined.Style,
                isSelected = currentTab == AppTab.HOME,
                onClick = { onTabSelected(AppTab.HOME) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_tab_deck")
            )

            // TAB 2: Call Log
            BottomNavItem(
                label = "Call Log",
                icon = if (currentTab == AppTab.CALL_LOG) Icons.Filled.History else Icons.Outlined.History,
                isSelected = currentTab == AppTab.CALL_LOG,
                onClick = { onTabSelected(AppTab.CALL_LOG) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_tab_call_log")
            )

            // CENTER: Prominent Dialpad Quick Action Button
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .clickable { onOpenDialpad() }
                        .testTag("nav_center_dialpad_button")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2563EB),
                                        Color(0xFF1D4ED8)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dialpad,
                            contentDescription = "Open Keypad",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // TAB 3: Clients
            BottomNavItem(
                label = "Clients",
                icon = if (currentTab == AppTab.CONTACTS) Icons.Filled.Phone else Icons.Outlined.Phone,
                isSelected = currentTab == AppTab.CONTACTS,
                onClick = { onTabSelected(AppTab.CONTACTS) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_tab_contacts")
            )

            // TAB 4: Profile
            BottomNavItem(
                label = "Profile",
                icon = if (currentTab == AppTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                isSelected = currentTab == AppTab.PROFILE,
                onClick = { onTabSelected(AppTab.PROFILE) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("nav_tab_profile")
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) LuxuryBlueContainer else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) LuxuryBlue else LuxuryTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) LuxuryBlue else LuxuryTextMuted
        )
    }
}
