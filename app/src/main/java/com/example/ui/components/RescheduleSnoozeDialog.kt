package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClientEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SnoozeOption(
    val days: Int,
    val title: String,
    val badge: String? = null,
    val isRecommended: Boolean = false
)

fun calculateSnoozeDateString(days: Int): String {
    val cal = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, days)
    }
    val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    return formatter.format(cal.time)
}

fun calculateSnoozeDateShort(days: Int): String {
    val cal = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, days)
    }
    val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return formatter.format(cal.time)
}

@Composable
fun RescheduleSnoozeDialog(
    client: ClientEntity,
    onDismiss: () -> Unit,
    onConfirmSnooze: (days: Int) -> Unit
) {
    val snoozeOptions = remember {
        listOf(
            SnoozeOption(days = 1, title = "In 1 Day", badge = "Tomorrow"),
            SnoozeOption(days = 2, title = "In 2 Days"),
            SnoozeOption(days = 3, title = "In 3 Days", badge = "POPULAR", isRecommended = true),
            SnoozeOption(days = 5, title = "In 5 Days"),
            SnoozeOption(days = 7, title = "In 7 Days (1 Week)", badge = "RECOMMENDED"),
            SnoozeOption(days = 14, title = "In 14 Days (2 Weeks)"),
            SnoozeOption(days = 30, title = "In 30 Days (1 Month)")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RESCHEDULE FOLLOW-UP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309),
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = client.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select when this card should reappear in your daily rotation. Card will disappear until that exact date:",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(snoozeOptions) { opt ->
                        val targetDateText = calculateSnoozeDateString(opt.days)
                        val isHighPriority = opt.days == 7 || opt.isRecommended

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onConfirmSnooze(opt.days)
                                    onDismiss()
                                }
                                .testTag("snooze_option_${opt.days}"),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isHighPriority) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                            border = BorderStroke(
                                1.2.dp,
                                if (isHighPriority) Color(0xFFF59E0B) else Color(0xFFE2E8F0)
                            ),
                            shadowElevation = if (isHighPriority) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = opt.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isHighPriority) Color(0xFF92400E) else Color(0xFF0F172A)
                                        )

                                        if (opt.badge != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (opt.days == 7) Color(0xFF0D8267) else Color(0xFFD97706)
                                            ) {
                                                Text(
                                                    text = opt.badge,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    // SMALL TEXT SHOWING EXACT TARGET DATE AS REQUESTED!
                                    Text(
                                        text = "Reappears on $targetDateText",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isHighPriority) Color(0xFFB45309) else Color(0xFF64748B)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isHighPriority) Color(0xFFFDE68A) else Color(0xFFE2E8F0),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (isHighPriority) Color(0xFFB45309) else Color(0xFF64748B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
