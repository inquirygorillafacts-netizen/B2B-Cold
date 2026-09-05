package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClientEntity
import com.example.data.local.VoiceNoteEntity
import com.example.model.CardAnimationStyle
import com.example.ui.theme.EmeraldCallGradient
import com.example.ui.theme.GuiltAmber
import com.example.ui.theme.GuiltAmberContainer
import com.example.ui.theme.LightCardSurfaceGradient
import com.example.ui.theme.LightGlassBorderStroke
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryEmeraldContainer
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryGoldBg
import com.example.ui.theme.LuxuryGoldBorder
import com.example.ui.theme.LuxuryLightCanvasAlt
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppGreenContainer
import com.example.util.CallHelper
import com.example.util.PlaybackState
import com.example.util.RecordingState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun LuxuryClientCardDeck(
    client: ClientEntity?,
    nextClient: ClientEntity?,
    animationStyle: CardAnimationStyle,
    voiceNotes: List<VoiceNoteEntity>,
    playbackState: PlaybackState,
    recordingState: RecordingState,
    onCallClick: (ClientEntity) -> Unit,
    onWhatsAppClick: (ClientEntity) -> Unit,
    onPlayVoiceNote: (VoiceNoteEntity) -> Unit,
    onStopPlayback: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (String, String) -> Unit,
    onCancelRecording: () -> Unit,
    onSwipeNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (client == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No clients available in deck rotation.\nCheck Deck Studio settings to enable clients.",
                color = LuxuryTextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isSwiping by remember { mutableStateOf(false) }

    val swipeProgress = (abs(offsetX.value) / 400f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("luxury_card_deck_container"),
        contentAlignment = Alignment.Center
    ) {
        // BACK CARD (Stacked Next Card Effect)
        if (nextClient != null) {
            val backScale = 0.94f + (swipeProgress * 0.06f)
            val backAlpha = 0.65f + (swipeProgress * 0.35f)
            val backOffsetY = 20f - (swipeProgress * 20f)

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(580.dp)
                    .graphicsLayer {
                        scaleX = backScale
                        scaleY = backScale
                        alpha = backAlpha
                        translationY = backOffsetY
                    }
            ) {
                LuxuryCardSurface(
                    client = nextClient,
                    voiceNotes = emptyList(),
                    playbackState = PlaybackState(),
                    recordingState = RecordingState(),
                    onCallClick = {},
                    onWhatsAppClick = {},
                    onPlayVoiceNote = {},
                    onStopPlayback = {},
                    onStartRecording = {},
                    onStopRecording = { _, _ -> },
                    onCancelRecording = {},
                    isInteractive = false
                )
            }
        }

        // TOP ACTIVE SWIPEABLE CARD with 10 Distinct Real-Time Physics Behaviors
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(580.dp)
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .graphicsLayer {
                    when (animationStyle) {
                        CardAnimationStyle.LIQUID_GLASS_STACK -> {
                            // Smooth slight tilt + progressive alpha morph
                            rotationZ = (offsetX.value / 45f).coerceIn(-18f, 18f)
                            scaleX = 1f - (swipeProgress * 0.05f)
                            scaleY = 1f - (swipeProgress * 0.05f)
                            alpha = 1f - (swipeProgress * 0.35f)
                        }
                        CardAnimationStyle.IOS_TINDER_SNAP_3D -> {
                            // Classic snappy iOS Tinder rotation around bottom anchor
                            rotationZ = (offsetX.value / 25f).coerceIn(-30f, 30f)
                            cameraDistance = 14f * density
                            rotationY = (offsetX.value / 40f).coerceIn(-18f, 18f)
                        }
                        CardAnimationStyle.CUBE_3D_ROTATION -> {
                            // True 3D Cube face rotation: pivot shifts to swiping edge
                            cameraDistance = 8f * density
                            if (offsetX.value >= 0) {
                                transformOrigin = TransformOrigin(1f, 0.5f)
                                rotationY = (offsetX.value / 18f).coerceIn(0f, 65f)
                            } else {
                                transformOrigin = TransformOrigin(0f, 0.5f)
                                rotationY = (offsetX.value / 18f).coerceIn(-65f, 0f)
                            }
                            scaleX = 1f - (swipeProgress * 0.12f)
                        }
                        CardAnimationStyle.DEPTH_FLIP -> {
                            // Deep horizontal 3D flip with steep perspective
                            cameraDistance = 9f * density
                            rotationY = (offsetX.value / 12f).coerceIn(-80f, 80f)
                            scaleX = 1f - (swipeProgress * 0.2f)
                            scaleY = 1f - (swipeProgress * 0.2f)
                        }
                        CardAnimationStyle.FLY_OUT_PHYSICS_SPRING -> {
                            // Dramatic slingshot fly-out with steep vertical gravity pull
                            rotationZ = (offsetX.value / 28f).coerceIn(-28f, 28f)
                            translationY = (abs(offsetX.value) * 0.45f)
                            scaleX = 1f + (swipeProgress * 0.05f)
                        }
                        CardAnimationStyle.FADE_SCALE_MORPH -> {
                            // Minimalist cinematic scale down & fade out
                            rotationZ = 0f
                            scaleX = 1f - (swipeProgress * 0.45f)
                            scaleY = 1f - (swipeProgress * 0.45f)
                            alpha = 1f - (swipeProgress * 0.85f)
                        }
                        CardAnimationStyle.VELVET_SLIDE_REVEAL -> {
                            // Pure horizontal velvet translation with zero rotation
                            rotationZ = 0f
                            rotationY = 0f
                            rotationX = 0f
                            alpha = 1f - (swipeProgress * 0.2f)
                        }
                        CardAnimationStyle.CARD_PEEL_EFFECT -> {
                            // Diagonal page curl effect rotating both X and Y
                            cameraDistance = 12f * density
                            transformOrigin = TransformOrigin(0.5f, 0f)
                            rotationX = (abs(offsetY.value) / 12f + swipeProgress * 25f).coerceIn(0f, 40f)
                            rotationZ = (offsetX.value / 35f).coerceIn(-15f, 15f)
                        }
                        CardAnimationStyle.PARALLAX_HOVER_GLIDE -> {
                            // Multi-axis floating glide with vertical drag follow
                            rotationZ = (offsetX.value / 60f).coerceIn(-12f, 12f)
                            translationY = offsetY.value * 0.7f
                            scaleX = 1f - (swipeProgress * 0.06f)
                        }
                        CardAnimationStyle.SPRING_BOUNCY_SNAP -> {
                            // Bouncy expansion elasticity
                            rotationZ = (offsetX.value / 32f).coerceIn(-24f, 24f)
                            scaleX = 1f + (swipeProgress * 0.12f)
                            scaleY = 1f + (swipeProgress * 0.08f)
                        }
                    }
                }
                .pointerInput(client.id) {
                    detectDragGestures(
                        onDragStart = { isSwiping = true },
                        onDragEnd = {
                            isSwiping = false
                            val threshold = 280f
                            if (abs(offsetX.value) > threshold) {
                                // Trigger dismiss animation & next client
                                coroutineScope.launch {
                                    val targetX = if (offsetX.value > 0) 1200f else -1200f
                                    offsetX.animateTo(
                                        targetValue = targetX,
                                        animationSpec = tween(durationMillis = 200)
                                    )
                                    onSwipeNext()
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                }
                            } else {
                                // Snap back to center with spring physics
                                coroutineScope.launch {
                                    launch {
                                        offsetX.animateTo(
                                            0f,
                                            spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                    launch {
                                        offsetY.animateTo(
                                            0f,
                                            spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            isSwiping = false
                            coroutineScope.launch {
                                offsetX.animateTo(0f)
                                offsetY.animateTo(0f)
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                                offsetY.snapTo(offsetY.value + dragAmount.y * 0.35f)
                            }
                        }
                    )
                }
        ) {
            LuxuryCardSurface(
                client = client,
                voiceNotes = voiceNotes,
                playbackState = playbackState,
                recordingState = recordingState,
                onCallClick = { onCallClick(client) },
                onWhatsAppClick = { onWhatsAppClick(client) },
                onPlayVoiceNote = onPlayVoiceNote,
                onStopPlayback = onStopPlayback,
                onStartRecording = onStartRecording,
                onStopRecording = onStopRecording,
                onCancelRecording = onCancelRecording,
                isInteractive = true
            )
        }
    }
}

@Composable
fun LuxuryCardSurface(
    client: ClientEntity,
    voiceNotes: List<VoiceNoteEntity>,
    playbackState: PlaybackState,
    recordingState: RecordingState,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onPlayVoiceNote: (VoiceNoteEntity) -> Unit,
    onStopPlayback: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (String, String) -> Unit,
    onCancelRecording: () -> Unit,
    isInteractive: Boolean
) {
    val daysAgo = CallHelper.formatDaysAgo(client.lastContactedTimestamp)
    val initials = remember(client.name) {
        val parts = client.name.trim().split(Regex("\\s+"))
        if (parts.size >= 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()) {
            "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        } else {
            client.name.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        }
    }

    var showVoiceRecorderDialog by remember { mutableStateOf(false) }
    var recordingSummaryInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
            .testTag("luxury_card_${client.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LightGlassBorderStroke),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightCardSurfaceGradient)
        ) {
            // Ambient subtle accent radial glow in top right
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                LuxuryBlue.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP: Avatar, Category Tag & Deal Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Avatar with Clean High-End Border
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2563EB),
                                        Color(0xFF1D4ED8)
                                    )
                                )
                            )
                            .border(2.dp, Color(0xFFDBEAFE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        // Category Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, LightGlassBorderStroke)
                        ) {
                            Text(
                                text = client.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = LuxuryTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Deal Size Tag (Bold Emerald Green Pill)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LuxuryEmeraldContainer,
                            border = BorderStroke(1.dp, LuxuryEmerald.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = client.dealSize,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                color = LuxuryEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // MIDDLE: Client Name, Designation, and THE GUILT METRIC
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = LuxuryTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${client.designation} • ${client.company}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = LuxuryTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // THE GUILT METRIC BADGE (Attention-grabbing Amber highlight)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = GuiltAmberContainer,
                        border = BorderStroke(1.dp, GuiltAmber.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = GuiltAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "LAST CONTACT: $daysAgo".uppercase(),
                                    color = GuiltAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Reignite connection before this relationship goes cold.",
                                    color = LuxuryTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // EMBEDDED VOICE NOTE & AUDIO RECALL
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, LightGlassBorderStroke)
                ) {
                    val latestNote = voiceNotes.firstOrNull()
                    val isPlaying = latestNote != null &&
                            playbackState.isPlaying &&
                            playbackState.currentVoiceNoteId == latestNote.id

                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = LuxuryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AUDIO MEMORY NOTE",
                                    color = LuxuryBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }

                            if (isInteractive) {
                                Text(
                                    text = "+ Add Note",
                                    color = LuxuryBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable {
                                            onStartRecording()
                                            showVoiceRecorderDialog = true
                                        }
                                        .padding(2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (latestNote != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isPlaying) onStopPlayback() else onPlayVoiceNote(latestNote)
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlaying) LuxuryEmerald else LuxuryBlue)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play note",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = latestNote.summary,
                                        color = LuxuryTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${latestNote.durationSeconds}s audio memory",
                                        color = LuxuryTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No audio summary recorded yet. Tap + Add Note after your call to save context.",
                                color = LuxuryTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // PRIMARY ACTIONS (Call Now + WhatsApp Chat)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Call Button (Dominant Emerald Green Gradient)
                    Button(
                        onClick = onCallClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("luxury_card_call_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(EmeraldCallGradient, RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CALL NOW",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    // WhatsApp Quick Chat Button
                    Surface(
                        onClick = onWhatsAppClick,
                        modifier = Modifier
                            .size(56.dp)
                            .testTag("luxury_card_whatsapp_button"),
                        shape = RoundedCornerShape(18.dp),
                        color = WhatsAppGreenContainer,
                        border = BorderStroke(1.dp, WhatsAppGreen.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "WhatsApp",
                                tint = WhatsAppGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Hint footer: Swipe gesture prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "← Swipe Card to Skip / Rotate Next →",
                        color = LuxuryTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }

    if (showVoiceRecorderDialog) {
        RecordVoiceNoteDialog(
            recordingState = recordingState,
            clientName = client.name,
            summaryInput = recordingSummaryInput,
            onSummaryChange = { recordingSummaryInput = it },
            onSave = {
                onStopRecording(client.id, recordingSummaryInput)
                recordingSummaryInput = ""
                showVoiceRecorderDialog = false
            },
            onCancel = {
                onCancelRecording()
                recordingSummaryInput = ""
                showVoiceRecorderDialog = false
            }
        )
    }
}
