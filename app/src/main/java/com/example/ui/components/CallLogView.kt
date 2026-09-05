package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneMissed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.CallLogItem
import com.example.model.CallType
import com.example.ui.theme.CallBlue
import com.example.ui.theme.CallGreen
import com.example.ui.theme.CallRed
import com.example.util.CallHelper

@Composable
fun CallLogView(
    logs: List<CallLogItem>,
    selectedFilter: CallType?,
    onFilterSelect: (CallType?) -> Unit,
    onCallClick: (String) -> Unit,
    onSmsClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onOpenDialpadClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItemForAction by remember { mutableStateOf<CallLogItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("call_log_view_container")
    ) {
        // Filter chips row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { onFilterSelect(null) },
                    label = { Text("All") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("call_log_filter_all"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == null,
                        borderColor = if (selectedFilter == null) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == CallType.MISSED,
                    onClick = { onFilterSelect(CallType.MISSED) },
                    label = { Text("Missed") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("call_log_filter_missed"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CallRed.copy(alpha = 0.12f),
                        selectedLabelColor = CallRed,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == CallType.MISSED,
                        borderColor = if (selectedFilter == CallType.MISSED) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == CallType.INCOMING,
                    onClick = { onFilterSelect(CallType.INCOMING) },
                    label = { Text("Incoming") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("call_log_filter_incoming"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == CallType.INCOMING,
                        borderColor = if (selectedFilter == CallType.INCOMING) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == CallType.OUTGOING,
                    onClick = { onFilterSelect(CallType.OUTGOING) },
                    label = { Text("Outgoing") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("call_log_filter_outgoing"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == CallType.OUTGOING,
                        borderColor = if (selectedFilter == CallType.OUTGOING) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                )
            }
        }

        // Section tracking header matching design text-[11px] font-medium uppercase tracking-wider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT CALLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneMissed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Call Logs Found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No call records yet. Your recent incoming and outgoing calls will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("call_log_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs, key = { it.id }) { logItem ->
                    CallLogCard(
                        log = logItem,
                        onItemClick = { selectedItemForAction = logItem },
                        onCallClick = { onCallClick(logItem.number) }
                    )
                }
            }
        }
    }

    selectedItemForAction?.let { item ->
        ContactActionSheet(
            name = item.displayName,
            number = item.number,
            subtitle = "${item.type.displayName} • ${CallHelper.formatCallDate(item.timestamp)}",
            onCallClick = onCallClick,
            onSmsClick = onSmsClick,
            onCopyClick = onCopyClick,
            onOpenDialpadClick = onOpenDialpadClick,
            onDismiss = { selectedItemForAction = null }
        )
    }
}

@Composable
fun CallLogCard(
    log: CallLogItem,
    onItemClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val (typeIcon, iconTint) = when (log.type) {
        CallType.INCOMING -> Pair(Icons.AutoMirrored.Filled.CallReceived, CallGreen)
        CallType.OUTGOING -> Pair(Icons.AutoMirrored.Filled.CallMade, MaterialTheme.colorScheme.primary)
        CallType.MISSED -> Pair(Icons.AutoMirrored.Filled.CallMissed, CallRed)
        CallType.REJECTED -> Pair(Icons.Default.Block, CallRed)
        CallType.UNKNOWN -> Pair(Icons.Default.Phone, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    // Coordinated container and text colors matching design HTML
    val (avatarBg, avatarText, initials) = remember(log.displayName) {
        val pairs = listOf(
            Pair(Color(0xFFFFD8E4), Color(0xFF31111D)), // Pink / Wine (RS)
            Pair(Color(0xFFD3E3FD), Color(0xFF041E49)), // Blue / Navy (PP)
            Pair(Color(0xFFE8DEF8), Color(0xFF1D192B)), // Lavender / Purple (VS)
            Pair(Color(0xFFEADDFF), Color(0xFF21005D)), // Primary M3 Container
            Pair(Color(0xFFC4EED0), Color(0xFF072711)), // Soft Green / Dark Green
            Pair(Color(0xFFFFDCC2), Color(0xFF2E1500))  // Soft Peach / Warm Brown
        )
        val index = Math.abs(log.displayName.hashCode()) % pairs.size
        val words = log.displayName.trim().split(Regex("\\s+"))
        val inits = if (words.size >= 2 && words[0].isNotEmpty() && words[1].isNotEmpty()) {
            "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
        } else {
            log.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        }
        Triple(pairs[index].first, pairs[index].second, inits)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onItemClick)
            .testTag("call_log_item_${log.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initial Avatar with M3 color pair matching design
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = avatarText
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (log.type == CallType.MISSED) FontWeight.SemiBold else FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = log.type.displayName,
                        tint = iconTint,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${log.type.displayName} • ${CallHelper.formatCallDate(log.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (log.type == CallType.MISSED) CallRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (log.duration > 0) {
                        Text(
                            text = " • ${CallHelper.formatDuration(log.duration)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Call Button matching text-[#6750A4] in design
            IconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("call_log_call_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call ${log.displayName}",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
