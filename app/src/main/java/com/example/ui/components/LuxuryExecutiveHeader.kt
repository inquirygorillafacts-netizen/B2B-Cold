package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.ui.theme.LightGlassBorderStroke
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary

@Composable
fun LuxuryExecutiveHeader(
    callsDoneToday: Int,
    dailyGoal: Int,
    onOpenSettings: () -> Unit,
    onOpenDialpad: () -> Unit,
    onRotateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (dailyGoal > 0) (callsDoneToday.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("luxury_executive_header"),
        color = Color.White,
        border = BorderStroke(0.5.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Daily Progress Ring & Goal Indicator
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, LightGlassBorderStroke)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFFE2E8F0),
                            strokeWidth = 2.5.dp,
                        )
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = if (callsDoneToday >= dailyGoal) LuxuryEmerald else LuxuryBlue,
                            strokeWidth = 2.5.dp,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "$callsDoneToday / $dailyGoal CALLS",
                            color = LuxuryTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (callsDoneToday >= dailyGoal) "GOAL REACHED" else "DAILY TARGET",
                            color = if (callsDoneToday >= dailyGoal) LuxuryEmerald else LuxuryTextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // RIGHT: Clean Executive Quick Action Icons in corner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Next Card Rotate Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, LightGlassBorderStroke),
                    modifier = Modifier.clip(CircleShape)
                ) {
                    IconButton(
                        onClick = onRotateNext,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("rotate_deck_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Next Card",
                            tint = LuxuryTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Quick Dialpad Keypad Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, LightGlassBorderStroke),
                    modifier = Modifier.clip(CircleShape)
                ) {
                    IconButton(
                        onClick = onOpenDialpad,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_dialpad_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dialpad,
                            contentDescription = "Keypad",
                            tint = LuxuryTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Deck Settings Studio Button
                Surface(
                    shape = CircleShape,
                    color = LuxuryBlueContainer,
                    border = BorderStroke(1.dp, LuxuryBlue.copy(alpha = 0.25f)),
                    modifier = Modifier.clip(CircleShape)
                ) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Deck Studio",
                            tint = LuxuryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
