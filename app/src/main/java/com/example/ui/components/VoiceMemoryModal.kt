package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClientEntity
import com.example.data.local.VoiceNoteEntity
import com.example.util.PlaybackState
import com.example.util.RecordingState

@Composable
fun VoiceMemoryModal(
    client: ClientEntity,
    voiceNotes: List<VoiceNoteEntity>,
    playbackState: PlaybackState,
    recordingState: RecordingState,
    onPlayVoiceNote: (VoiceNoteEntity) -> Unit,
    onStopPlayback: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (String) -> Unit,
    onCancelRecording: () -> Unit,
    onDeleteVoiceNote: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var noteToDelete by remember { mutableStateOf<VoiceNoteEntity?>(null) }
    var isManualRecording by remember { mutableStateOf(false) }

    // Waveform bar heights (matching audio design in screenshot 3)
    val waveformHeights = remember {
        listOf(10, 16, 22, 14, 26, 32, 20, 28, 18, 24, 12, 18, 28, 22, 16, 12, 20, 14)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VOICE MEMORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB47818),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = client.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_memory_modal_content")
            ) {
                // Recording count indicator (1 to 5)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recorded Memories (${voiceNotes.size}/5)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                    if (voiceNotes.size >= 5) {
                        Text(
                            text = "Limit Reached",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFDC2626)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of existing recordings (up to 5)
                if (voiceNotes.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No voice memories yet",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Record up to 5 voice notes for this client",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (voiceNotes.size > 2) 220.dp else 120.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(voiceNotes) { index, note ->
                            val isPlaying = playbackState.isPlaying && playbackState.currentVoiceNoteId == note.id
                            val progress = if (isPlaying) playbackState.progress else 0f

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (isPlaying) Color(0xFFC49746) else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("voice_note_item_$index")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Play / Pause Circle (Matching dark emerald in Screenshot 3)
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF143527),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                if (isPlaying) onStopPlayback() else onPlayVoiceNote(note)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = if (isPlaying) "Pause" else "Play",
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Waveform Visualizer & Duration (Screenshot 3 style)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Memory #${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = String.format("0:%02d", note.durationSeconds),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Waveform Bars
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            waveformHeights.forEachIndexed { barIndex, height ->
                                                val barProgress = barIndex.toFloat() / waveformHeights.size.toFloat()
                                                val isBarPlayed = progress >= barProgress

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(height.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(
                                                            if (isBarPlayed) Color(0xFFC49746) else Color(0xFFE2E8F0)
                                                        )
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Delete Button with confirmation requirement!
                                    IconButton(
                                        onClick = { noteToDelete = note },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .testTag("delete_voice_note_$index")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete voice memory",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom: "Hold to record what matters next."
                Text(
                    text = "Hold to record what matters next.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Big Record Button (Screenshot 3 style)
                if (voiceNotes.size < 5) {
                    val isRecording = recordingState.isRecording

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isRecording) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.5.dp,
                            if (isRecording) Color(0xFFEF4444) else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        onStartRecording()
                                        tryAwaitRelease()
                                        onStopRecording("Executive Voice Memory")
                                    }
                                )
                            }
                            .testTag("hold_to_record_button")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = if (isRecording) Color(0xFFEF4444) else Color(0xFFB47818),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRecording) {
                                    String.format("Recording 0:%02d (Release to Save)", recordingState.elapsedSeconds)
                                } else {
                                    "Hold to record"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isRecording) Color(0xFFDC2626) else Color(0xFF0F172A)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Max 5 recordings saved. Delete an older recording to record a new one.",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Done", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )

    // CONFIRMATION DIALOG FOR DELETE (As explicitly requested by user!)
    if (noteToDelete != null) {
        val target = noteToDelete!!
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = {
                Text(
                    text = "Delete Voice Memory?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete this recording for ${client.name}? This action cannot be undone.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteVoiceNote(target.id)
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text(text = "Cancel", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}
