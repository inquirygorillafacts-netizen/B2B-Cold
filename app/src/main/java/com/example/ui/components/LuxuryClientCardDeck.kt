package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ClientEntity
import com.example.data.local.VoiceNoteEntity
import com.example.model.CardAnimationStyle
import com.example.ui.theme.HeroCallButtonGradient
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary
import com.example.ui.theme.RomanticAvatarGradient
import com.example.ui.theme.RomanticChampagneBorder
import com.example.ui.theme.RomanticChampagnePill
import com.example.ui.theme.RomanticChampagneText
import com.example.ui.theme.RomanticTouchpointBg
import com.example.ui.theme.RomanticTouchpointText
import com.example.util.CallHelper
import com.example.util.CallLogHelper
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
    onSnoozeClick: (ClientEntity, Int) -> Unit,
    onPlayVoiceNote: (VoiceNoteEntity) -> Unit,
    onStopPlayback: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (String, String) -> Unit,
    onCancelRecording: () -> Unit,
    onDeleteVoiceNote: (String) -> Unit = {},
    onSwipeNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (client == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = BorderStroke(1.2.dp, RomanticChampagneBorder),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(RomanticAvatarGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Deck Rotation Completed",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = LuxuryTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All active cards have been cycled. Manage your rotation in Settings or wait for scheduled follow-ups.",
                        color = LuxuryTextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isSwiping by remember { mutableStateOf(false) }
    var showSnoozeDialog by remember { mutableStateOf(false) }
    var showVoiceMemoryModal by remember { mutableStateOf(false) }

    val showRightStamp by remember { derivedStateOf { offsetX.value > 40f } }
    val showLeftStamp by remember { derivedStateOf { offsetX.value < -40f } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("luxury_card_deck_container"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP CARD STACK AREA
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // LAYER 2: BACKGROUND DEPTH CARD (Hardware-accelerated reactive depth)
            if (nextClient != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .height(515.dp)
                        .graphicsLayer {
                            val dragProgress = (abs(offsetX.value) / 360f).coerceIn(0f, 1f)
                            val backScale = when (animationStyle) {
                                CardAnimationStyle.CUBE_3D_ROTATION -> 0.90f + (dragProgress * 0.10f)
                                CardAnimationStyle.FADE_SCALE_MORPH -> 0.88f + (dragProgress * 0.12f)
                                CardAnimationStyle.DEPTH_FLIP -> 0.91f + (dragProgress * 0.09f)
                                CardAnimationStyle.LIQUID_GLASS_STACK -> 0.93f + (dragProgress * 0.07f)
                                else -> 0.94f + (dragProgress * 0.06f)
                            }
                            val backAlpha = 0.82f + (dragProgress * 0.18f)
                            val backOffsetY = 14f - (dragProgress * 14f)

                            scaleX = backScale
                            scaleY = backScale
                            alpha = backAlpha
                            translationY = backOffsetY
                        }
                ) {
                    RomanticCardSurface(
                        client = nextClient,
                        voiceNotes = emptyList(),
                        onOpenVoiceMemory = {},
                        isInteractive = false
                    )
                }
            }

            // LAYER 1: HERO ACTIVE CARD (Silky 60/120fps hardware-accelerated 10 card dynamics)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(520.dp)
                    .graphicsLayer {
                        translationX = offsetX.value
                        translationY = offsetY.value
                        val dragProgress = (abs(offsetX.value) / 360f).coerceIn(0f, 1f)

                        when (animationStyle) {
                            CardAnimationStyle.LIQUID_GLASS_STACK -> {
                                rotationZ = (offsetX.value / 32f).coerceIn(-12f, 12f)
                                scaleX = 1f - (dragProgress * 0.04f)
                                scaleY = 1f - (dragProgress * 0.04f)
                                alpha = 1f - (dragProgress * 0.15f)
                            }
                            CardAnimationStyle.IOS_TINDER_SNAP_3D -> {
                                rotationZ = (offsetX.value / 20f).coerceIn(-22f, 22f)
                                cameraDistance = 18f * density
                                rotationY = (offsetX.value / 38f).coerceIn(-14f, 14f)
                                scaleX = 1f - (dragProgress * 0.03f)
                                scaleY = 1f - (dragProgress * 0.03f)
                            }
                            CardAnimationStyle.CUBE_3D_ROTATION -> {
                                cameraDistance = 16f * density
                                rotationY = (offsetX.value / 240f) * -42f
                                scaleX = 1f - (dragProgress * 0.10f)
                                scaleY = 1f - (dragProgress * 0.10f)
                            }
                            CardAnimationStyle.DEPTH_FLIP -> {
                                cameraDistance = 20f * density
                                rotationY = (offsetX.value / 260f) * 55f
                                scaleX = 1f - (dragProgress * 0.08f)
                                scaleY = 1f - (dragProgress * 0.08f)
                                alpha = 1f - (dragProgress * 0.22f)
                            }
                            CardAnimationStyle.FLY_OUT_PHYSICS_SPRING -> {
                                rotationZ = (offsetX.value / 22f).coerceIn(-24f, 24f)
                                translationY = offsetY.value - (dragProgress * 45f)
                                scaleX = 1f + (dragProgress * 0.04f)
                                scaleY = 1f + (dragProgress * 0.04f)
                            }
                            CardAnimationStyle.FADE_SCALE_MORPH -> {
                                scaleX = 1f - (dragProgress * 0.20f)
                                scaleY = 1f - (dragProgress * 0.20f)
                                alpha = 1f - (dragProgress * 0.50f)
                            }
                            CardAnimationStyle.VELVET_SLIDE_REVEAL -> {
                                rotationZ = 0f
                                scaleX = 1f
                                scaleY = 1f
                                alpha = 1f - (dragProgress * 0.15f)
                            }
                            CardAnimationStyle.CARD_PEEL_EFFECT -> {
                                cameraDistance = 16f * density
                                rotationZ = (offsetX.value / 20f).coerceIn(-16f, 16f)
                                rotationX = (dragProgress * 16f)
                                scaleX = 1f - (dragProgress * 0.06f)
                                scaleY = 1f - (dragProgress * 0.06f)
                            }
                            CardAnimationStyle.PARALLAX_HOVER_GLIDE -> {
                                cameraDistance = 22f * density
                                rotationY = (offsetX.value / 26f).coerceIn(-15f, 15f)
                                rotationX = -(offsetY.value / 26f).coerceIn(-15f, 15f)
                                scaleX = 1.02f - (dragProgress * 0.04f)
                                scaleY = 1.02f - (dragProgress * 0.04f)
                            }
                            CardAnimationStyle.SPRING_BOUNCY_SNAP -> {
                                rotationZ = (offsetX.value / 16f).coerceIn(-25f, 25f)
                                scaleX = 1f - (dragProgress * 0.05f)
                                scaleY = 1f + (dragProgress * 0.03f)
                            }
                        }
                    }
                    .pointerInput(client.id) {
                        detectDragGestures(
                            onDragStart = { isSwiping = true },
                            onDragEnd = {
                                isSwiping = false
                                val threshold = 220f
                                if (abs(offsetX.value) > threshold) {
                                    val isRight = offsetX.value > 0
                                    coroutineScope.launch {
                                        val targetX = if (isRight) 1300f else -1300f
                                        val exitDuration = when (animationStyle) {
                                            CardAnimationStyle.FLY_OUT_PHYSICS_SPRING -> 140
                                            CardAnimationStyle.SPRING_BOUNCY_SNAP -> 160
                                            else -> 190
                                        }
                                        offsetX.animateTo(
                                            targetValue = targetX,
                                            animationSpec = tween(durationMillis = exitDuration, easing = FastOutSlowInEasing)
                                        )
                                        // Swipe cycles to next contact card smoothly
                                        onSwipeNext()
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)
                                    }
                                } else {
                                    coroutineScope.launch {
                                        val springSpec = when (animationStyle) {
                                            CardAnimationStyle.SPRING_BOUNCY_SNAP ->
                                                spring<Float>(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMediumLow)
                                            CardAnimationStyle.IOS_TINDER_SNAP_3D ->
                                                spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                                            CardAnimationStyle.FLY_OUT_PHYSICS_SPRING ->
                                                spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessHigh)
                                            else ->
                                                spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                                        }
                                        launch {
                                            offsetX.animateTo(0f, springSpec)
                                        }
                                        launch {
                                            offsetY.animateTo(0f, springSpec)
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
                RomanticCardSurface(
                    client = client,
                    voiceNotes = voiceNotes,
                    onOpenVoiceMemory = { showVoiceMemoryModal = true },
                    isInteractive = true
                )

                // SWIPE STAMP OVERLAYS: Clean & Crisp (Next Contact stamp)
                if (showRightStamp) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(22.dp)
                            .rotate(-10f)
                            .graphicsLayer {
                                alpha = ((offsetX.value - 40f) / 150f).coerceIn(0f, 0.96f)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF3B82F6),
                        shadowElevation = 6.dp,
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "NEXT CONTACT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                } else if (showLeftStamp) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(22.dp)
                            .rotate(10f)
                            .graphicsLayer {
                                alpha = ((abs(offsetX.value) - 40f) / 150f).coerceIn(0f, 0.96f)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF64748B),
                        shadowElevation = 6.dp,
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "NEXT CONTACT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Centered helper hint
        Text(
            text = "⇄ Swipe card or slide below to pass",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
        )

        // UPPER PIPE: Drag to skip slider with animated trail under finger
        LuxurySkipSlider(
            onSkip = {
                coroutineScope.launch {
                    val exitDuration = when (animationStyle) {
                        CardAnimationStyle.FLY_OUT_PHYSICS_SPRING -> 140
                        CardAnimationStyle.SPRING_BOUNCY_SNAP -> 160
                        else -> 190
                    }
                    offsetX.animateTo(-1200f, tween(exitDuration))
                    onSwipeNext()
                    offsetX.snapTo(0f)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(bottom = 6.dp)
        )

        // LOWER PIPE: Bottom Action Dock with Skip, Snooze, WhatsApp, and CALL NOW
        LuxuryBottomDock(
            client = client,
            onCall = { onCallClick(client) },
            onWhatsApp = { onWhatsAppClick(client) },
            onSnooze = { showSnoozeDialog = true },
            onSkip = {
                coroutineScope.launch {
                    offsetX.animateTo(-850f, tween(160))
                    onSwipeNext()
                    offsetX.snapTo(0f)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(bottom = 10.dp)
        )
    }

    // SNOOZE / RESCHEDULE MODAL WITH EXACT REAPPEARANCE DATES
    if (showSnoozeDialog) {
        RescheduleSnoozeDialog(
            client = client,
            onDismiss = { showSnoozeDialog = false },
            onConfirmSnooze = { days ->
                onSnoozeClick(client, days)
                showSnoozeDialog = false
            }
        )
    }

    // VOICE MEMORY STUDIO MODAL (1 TO 5 RECORDINGS WITH PLAY & DELETE CONFIRMATION)
    if (showVoiceMemoryModal) {
        VoiceMemoryModal(
            client = client,
            voiceNotes = voiceNotes,
            playbackState = playbackState,
            recordingState = recordingState,
            onPlayVoiceNote = onPlayVoiceNote,
            onStopPlayback = onStopPlayback,
            onStartRecording = onStartRecording,
            onStopRecording = { summary ->
                onStopRecording(client.id, summary)
            },
            onCancelRecording = onCancelRecording,
            onDeleteVoiceNote = onDeleteVoiceNote,
            onDismiss = { showVoiceMemoryModal = false }
        )
    }
}

@Composable
fun LuxurySkipSlider(
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val thumbOffsetX = remember { Animatable(0f) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(2.dp, RoundedCornerShape(25.dp), ambientColor = Color(0x140F172A))
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(25.dp))
            .testTag("skip_drag_track")
    ) {
        val thumbSizePx = with(density) { 44.dp.toPx() }
        val maxTravelPx = (with(density) { maxWidth.toPx() } - thumbSizePx - with(density) { 6.dp.toPx() }).coerceAtLeast(10f)
        val progress = (thumbOffsetX.value / maxTravelPx).coerceIn(0f, 1f)

        // Trailing glowing gradient under finger as user drags
        if (progress > 0.02f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF0D8267).copy(alpha = 0.25f),
                                Color(0xFF10B981).copy(alpha = 0.80f)
                            )
                        )
                    )
            )
        }

        // Center track text
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (progress > 0.65f) "Release to skip next" else "Drag to skip client",
                fontSize = 13.sp,
                fontWeight = if (progress > 0.65f) FontWeight.Black else FontWeight.Bold,
                color = if (progress > 0.65f) Color(0xFF0D8267) else Color(0xFF64748B),
                letterSpacing = 0.4.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (progress > 0.65f) Color(0xFF0D8267) else Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }

        // Draggable Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetX.value.toInt() + with(density) { 3.dp.roundToPx() }, with(density) { 3.dp.roundToPx() }) }
                .size(44.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.5.dp, if (progress > 0.5f) Color(0xFF10B981) else Color(0xFFCBD5E1), CircleShape)
                .pointerInput(maxTravelPx) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (thumbOffsetX.value > maxTravelPx * 0.65f) {
                                    thumbOffsetX.animateTo(maxTravelPx, tween(80))
                                    onSkip()
                                    thumbOffsetX.snapTo(0f)
                                } else {
                                    thumbOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                thumbOffsetX.animateTo(0f, spring())
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                val nextVal = (thumbOffsetX.value + dragAmount).coerceIn(0f, maxTravelPx)
                                thumbOffsetX.snapTo(nextVal)
                            }
                        }
                    )
                }
                .testTag("skip_drag_thumb"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Drag thumb to skip",
                tint = if (progress > 0.5f) Color(0xFF0D8267) else Color(0xFF0F172A),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun LuxuryBottomDock(
    client: ClientEntity,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onSnooze: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color(0x140F172A),
                spotColor = Color(0x1A0F172A)
            ),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Skip / Close Button
            Surface(
                shape = CircleShape,
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .clickable { onSkip() }
                    .testTag("dock_skip_button")
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Skip to next client",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2. Follow-Up Time Select Button (Reschedule / Snooze)
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFFBEB),
                border = BorderStroke(1.2.dp, Color(0xFFFDE68A)),
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .clickable { onSnooze() }
                    .testTag("dock_snooze_button")
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Reschedule follow-up",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3. WhatsApp Button (Exact WhatsApp chat dots icon)
            Surface(
                shape = CircleShape,
                color = Color(0xFF25D366),
                modifier = Modifier
                    .size(46.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .clickable { onWhatsApp() }
                    .testTag("dock_whatsapp_button")
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chat_dots_whatsapp),
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 4. CALL NOW Button (Expanded Emerald Pill)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onCall() }
                    .testTag("dock_call_now_button"),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0D8267)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CALL NOW",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RomanticCardSurface(
    client: ClientEntity,
    voiceNotes: List<VoiceNoteEntity>,
    onOpenVoiceMemory: () -> Unit,
    isInteractive: Boolean
) {
    val context = LocalContext.current
    val effectiveLastContacted = remember(client.id, client.lastContactedTimestamp) {
        val logTs = CallLogHelper.getLastCallTimestamp(context, client.number)
        if (logTs != null && logTs > client.lastContactedTimestamp) {
            logTs
        } else {
            client.lastContactedTimestamp
        }
    }
    val daysAgo = CallHelper.formatDaysAgo(effectiveLastContacted)
    val initials = remember(client.name) {
        val parts = client.name.trim().split(Regex("\\s+"))
        if (parts.size >= 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()) {
            "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        } else {
            client.name.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        }
    }

    val dynamicPrompt = remember(client.category, client.dealSize) {
        when {
            client.category.contains("VIP", ignoreCase = true) -> "Confirm next steps from previous executive touchpoint."
            else -> "Reconnect to discuss upcoming project priorities."
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0x140F172A),
                spotColor = Color(0x1A0F172A)
            )
            .clip(RoundedCornerShape(28.dp))
            .testTag("luxury_card_${client.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP ROW: SQUIRCLE EMERALD AVATAR + VIP RELATIONSHIP BADGE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0D8267),
                    modifier = Modifier.size(62.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8EE),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFB47818),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "VIP RELATIONSHIP",
                            color = Color(0xFFB47818),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            // CLIENT NAME & DESIGNATION / COMPANY
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = client.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${client.designation} · ${client.company}",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // PHONE NUMBER PILL WITH ONE-TAP COPY
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = isInteractive) {
                        CallHelper.copyToClipboard(context, client.number)
                    }
                    .testTag("card_phone_pill")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color(0xFF0D8267),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+91 ${client.number}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Copy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy phone number",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // MINI METRICS BOX: LAST CONTACT & TOUCHPOINT STATUS (NO FAKE PIPELINE VALUES)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFEDF2F7))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: LAST CONTACT
                    Column {
                        Text(
                            text = "LAST CONTACT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = daysAgo,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (effectiveLastContacted <= 0L) Color(0xFF64748B) else Color(0xFFB45309)
                        )
                    }

                    // Right: TOUCHPOINT STATUS
                    val statusText = when {
                        effectiveLastContacted <= 0L -> "Never Contacted"
                        (System.currentTimeMillis() - effectiveLastContacted) > 7 * 24 * 3600 * 1000L -> "Overdue (>7d)"
                        else -> "Recently Reached"
                    }
                    val statusColor = when {
                        effectiveLastContacted <= 0L -> Color(0xFF64748B)
                        (System.currentTimeMillis() - effectiveLastContacted) > 7 * 24 * 3600 * 1000L -> Color(0xFFE11D48)
                        else -> Color(0xFF0D8267)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "TOUCHPOINT STATUS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = statusText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                }
            }

            // STRATEGIC RECOMMENDATION PROMPT
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dynamicPrompt,
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp
                )
            }

            // VOICE MEMORY TEASER ROW: "Hold to record a 5-sec memory >"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(enabled = isInteractive) { onOpenVoiceMemory() }
                    .testTag("card_voice_memory_teaser"),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFEDF2F7))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color(0xFFB47818),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (voiceNotes.isNotEmpty()) "Voice Memories (${voiceNotes.size}/5 stored)" else "Hold to record a 5-sec memory",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
