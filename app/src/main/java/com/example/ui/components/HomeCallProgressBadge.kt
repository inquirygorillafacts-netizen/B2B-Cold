package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightGlassBorderStroke
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary

/**
 * Clean, subtle, corner floating badge displayed ONLY on the Home Deck page.
 * Shows how many calls remain to hit the daily target.
 * No bulky header, no buttons, no obstruction.
 */
@Composable
fun HomeCallProgressBadge(
    callsDoneToday: Int,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    val remaining = (dailyGoal - callsDoneToday).coerceAtLeast(0)
    val progress = if (dailyGoal > 0) (callsDoneToday.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, LightGlassBorderStroke),
        shadowElevation = 3.dp,
        modifier = modifier.testTag("home_call_progress_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
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
                    text = if (callsDoneToday >= dailyGoal) "GOAL COMPLETED" else "$remaining CALLS LEFT",
                    color = if (callsDoneToday >= dailyGoal) LuxuryEmerald else LuxuryTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "$callsDoneToday of $dailyGoal done today",
                    color = LuxuryTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
