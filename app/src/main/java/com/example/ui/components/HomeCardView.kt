package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.UserProfileData
import com.example.data.local.ClientEntity
import com.example.data.local.VoiceNoteEntity
import com.example.util.CallHelper
import com.example.util.PlaybackState
import com.example.util.RecordingState

val WhatsAppGreen = Color(0xFF25D366)
val CallPrimary = Color(0xFF6750A4)
val CallActionGreen = Color(0xFF16A34A)

@Composable
fun HomeCardView(
    client: ClientEntity?,
    userProfile: UserProfileData,
    rotationCount: Int,
    voiceNotes: List<VoiceNoteEntity>,
    playbackState: PlaybackState,
    recordingState: RecordingState,
    onCallClick: (ClientEntity) -> Unit,
    onWhatsAppClick: (ClientEntity) -> Unit,
    onSmsClick: (ClientEntity) -> Unit,
    onSkipClick: () -> Unit,
    onPlayVoiceNote: (VoiceNoteEntity) -> Unit,
    onStopPlayback: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (clientId: String, summary: String) -> Unit,
    onCancelRecording: () -> Unit,
    onOpenSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showRecordDialog by remember { mutableStateOf(false) }
    var recordingSummaryInput by remember { mutableStateOf("") }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartRecording()
            showRecordDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("home_card_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Daily Relationship Goal Header
        DailyGoalTrackerCard(
            callsMadeToday = userProfile.callsMadeToday,
            dailyGoal = userProfile.dailyCallGoal
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (client == null || rotationCount == 0) {
            // Empty rotation pool state
            EmptyRotationCard(onOpenSettingsClick = onOpenSettingsClick)
        } else {
            // Primary Focus: Random Client Card
            RandomClientCard(
                client = client,
                voiceNotes = voiceNotes,
                playbackState = playbackState,
                onPlayVoiceNote = onPlayVoiceNote,
                onStopPlayback = onStopPlayback,
                onRecordClick = {
                    val hasMicPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasMicPermission) {
                        onStartRecording()
                        showRecordDialog = true
                    } else {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onCallClick = { onCallClick(client) },
                onWhatsAppClick = { onWhatsAppClick(client) },
                onSmsClick = { onSmsClick(client) },
                onSkipClick = onSkipClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer info with settings shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card pool: $rotationCount active client${if (rotationCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = onOpenSettingsClick,
                    modifier = Modifier.testTag("home_edit_rotation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Edit Card Pool",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Card Pool Settings",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Recording Voice Note Dialog
    if (showRecordDialog) {
        RecordVoiceNoteDialog(
            recordingState = recordingState,
            clientName = client?.name ?: "Client",
            summaryInput = recordingSummaryInput,
            onSummaryChange = { recordingSummaryInput = it },
            onSave = {
                if (client != null) {
                    onStopRecording(client.id, recordingSummaryInput)
                }
                recordingSummaryInput = ""
                showRecordDialog = false
            },
            onCancel = {
                onCancelRecording()
                recordingSummaryInput = ""
                showRecordDialog = false
            }
        )
    }
}

@Composable
fun DailyGoalTrackerCard(
    callsMadeToday: Int,
    dailyGoal: Int
) {
    val progress = (callsMadeToday.toFloat() / dailyGoal.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val isGoalCompleted = callsMadeToday >= dailyGoal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_goal_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY RELATIONSHIP CALLS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isGoalCompleted) "Target Achieved! Great work." else "Stay off feeds, build relationships",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isGoalCompleted) CallActionGreen else MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isGoalCompleted) CallActionGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "$callsMadeToday / $dailyGoal Calls",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isGoalCompleted) CallActionGreen else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (isGoalCompleted) CallActionGreen else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun RandomClientCard(
    client: ClientEntity,
    voiceNotes: List<VoiceNoteEntity>,
    playbackState: PlaybackState,
    onPlayVoiceNote: (VoiceNoteEntity) -> Unit,
    onStopPlayback: () -> Unit,
    onRecordClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onSmsClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    // Generate coordinated pastel avatar color pair
    val (avatarBg, avatarText, initials) = remember(client.name) {
        val pairs = listOf(
            Pair(Color(0xFFFFD8E4), Color(0xFF31111D)),
            Pair(Color(0xFFD3E3FD), Color(0xFF041E49)),
            Pair(Color(0xFFE8DEF8), Color(0xFF1D192B)),
            Pair(Color(0xFFEADDFF), Color(0xFF21005D)),
            Pair(Color(0xFFC4EED0), Color(0xFF072711)),
            Pair(Color(0xFFFFDCC2), Color(0xFF2E1500))
        )
        val index = Math.abs(client.name.hashCode()) % pairs.size
        val words = client.name.trim().split(Regex("\\s+"))
        val inits = if (words.size >= 2 && words[0].isNotEmpty() && words[1].isNotEmpty()) {
            "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
        } else {
            client.name.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        }
        Triple(pairs[index].first, pairs[index].second, inits)
    }

    val daysAgoText = CallHelper.formatDaysAgo(client.lastContactedTimestamp)
    val latestVoiceNote = voiceNotes.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("random_client_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.2.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Tag: Category & Deal Size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = client.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Pipeline: ${client.dealSize}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar with initials
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = avatarText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Client Name & Company
            Text(
                text = client.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${client.designation} • ${client.company}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = client.number,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Days Since Last Contact Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Last Contacted",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Last Contact: $daysAgoText",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Voice Notes Section (Core Feature A)
            VoiceNoteCardSection(
                latestVoiceNote = latestVoiceNote,
                playbackState = playbackState,
                onPlayClick = {
                    if (latestVoiceNote != null) {
                        onPlayVoiceNote(latestVoiceNote)
                    }
                },
                onStopClick = onStopPlayback,
                onRecordNewClick = onRecordClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions: Call, WhatsApp, SMS, Skip (Core Feature B)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Action (In-app dialer/direct call)
                Button(
                    onClick = onCallClick,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("card_action_call"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CallActionGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Call",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // WhatsApp Action
                Button(
                    onClick = onWhatsAppClick,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("card_action_whatsapp"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SMS Action
                OutlinedButton(
                    onClick = onSmsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("card_action_sms"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "SMS",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SMS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Skip Action (Loads another random card)
                OutlinedButton(
                    onClick = onSkipClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("card_action_skip"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip to next",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceNoteCardSection(
    latestVoiceNote: VoiceNoteEntity?,
    playbackState: PlaybackState,
    onPlayClick: () -> Unit,
    onStopClick: () -> Unit,
    onRecordNewClick: () -> Unit
) {
    val isPlayingThis = playbackState.isPlaying && playbackState.currentVoiceNoteId == latestVoiceNote?.id

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_notes_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Voice Note",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LAST CALL VOICE NOTE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Record New Note Button
                TextButton(
                    onClick = onRecordNewClick,
                    modifier = Modifier.testTag("record_voice_note_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Record",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Record Note",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (latestVoiceNote != null) {
                Spacer(modifier = Modifier.height(8.dp))

                // Transcription / Summary Box
                Text(
                    text = "\"${latestVoiceNote.summary}\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Audio Player Controller
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = {
                            if (isPlayingThis) onStopClick() else onPlayClick()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("play_pause_voice_note_button"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlayingThis) "Pause" else "Play",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Waveform progress bar
                        LinearProgressIndicator(
                            progress = {
                                if (isPlayingThis) playbackState.progress else 0f
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val curSec = if (isPlayingThis) playbackState.currentPositionSeconds else 0
                            val totalSec = latestVoiceNote.durationSeconds
                            Text(
                                text = "0:${String.format("%02d", curSec)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "0:${String.format("%02d", totalSec)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "No voice notes recorded yet for this client. Tap 'Record Note' to save insights after your call.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecordVoiceNoteDialog(
    recordingState: RecordingState,
    clientName: String,
    summaryInput: String,
    onSummaryChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        )
    )

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Record Voice Note",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Recording audio for $clientName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Visual pulsating recording indicator
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDC2626).copy(alpha = pulseAlpha)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Recording",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "0:${String.format("%02d", recordingState.elapsedSeconds)}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFDC2626)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = summaryInput,
                    onValueChange = onSummaryChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Quick Summary Note") },
                    placeholder = { Text("e.g., Client budget tight, review in March") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Stop & Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EmptyRotationCard(
    onOpenSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .testTag("empty_rotation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Clients in Card Rotation",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "You have unchecked all clients in Settings. Enable clients in your card pool to receive daily relationship call cards.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onOpenSettingsClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Open Card Pool Settings")
            }
        }
    }
}
