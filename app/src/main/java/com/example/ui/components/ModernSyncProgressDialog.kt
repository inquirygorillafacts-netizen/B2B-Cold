package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.SyncProgressState

@Composable
fun ModernSyncProgressDialog(
    syncState: SyncProgressState,
    onDismiss: () -> Unit
) {
    if (!syncState.isSyncing) return

    val animatedProgress by animateFloatAsState(
        targetValue = syncState.progressPercent / 100f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "syncProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulseSync")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val syncRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Dialog(
        onDismissRequest = {
            if (syncState.isCompleted) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = syncState.isCompleted,
            dismissOnClickOutside = syncState.isCompleted
        )
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 24.dp,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Glowing Circular Progress Indicator
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(if (syncState.isCompleted) 1f else pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Soft background glow ring
                    Box(
                        modifier = Modifier
                            .size(126.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = if (syncState.isCompleted) {
                                        listOf(Color(0xFF10B981).copy(alpha = 0.2f), Color.Transparent)
                                    } else {
                                        listOf(Color(0xFF3B82F6).copy(alpha = 0.2f), Color.Transparent)
                                    }
                                )
                            )
                    )

                    // Track Ring
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(112.dp),
                        color = Color(0xFFF1F5F9),
                        strokeWidth = 9.dp,
                        strokeCap = StrokeCap.Round
                    )

                    // Active Progress Ring
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(112.dp),
                        color = if (syncState.isCompleted) Color(0xFF10B981) else Color(0xFF2563EB),
                        strokeWidth = 9.dp,
                        strokeCap = StrokeCap.Round
                    )

                    // Central Counter / Status Icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (syncState.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(42.dp)
                            )
                        } else {
                            Text(
                                text = "${syncState.progressPercent}%",
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Heading
                Text(
                    text = if (syncState.isCompleted) "Sync Completed!" else "Synchronizing Contacts...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Live status description
                Text(
                    text = syncState.statusMessage.ifBlank { "Optimizing executive card deck..." },
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Smooth linear progress bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (syncState.isCompleted) Color(0xFF10B981) else Color(0xFF2563EB),
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(22.dp))

                if (syncState.isCompleted) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text(
                            text = "Done",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(syncRotation)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Updating database in real time",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
