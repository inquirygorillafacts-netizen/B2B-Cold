package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ClientEntity
import com.example.model.CardAnimationStyle
import com.example.model.SubscriptionState
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary

enum class ContactFilterOption(val label: String) {
    ALL("All Contacts"),
    SELECTED("Selected"),
    UNSELECTED("Unselected")
}

@Composable
fun LuxurySettingsScreen(
    dailyGoal: Int,
    allClients: List<ClientEntity>,
    subscriptionState: SubscriptionState,
    lastSyncTimeString: String,
    cardAnimationStyle: CardAnimationStyle,
    preferredWhatsAppPackage: String?,
    onSelectCardAnimationStyle: (CardAnimationStyle) -> Unit,
    onSelectPreferredWhatsAppPackage: (String?) -> Unit,
    onSelectDailyGoal: (Int) -> Unit,
    onToggleClient: (String, Boolean) -> Unit,
    onSelectAllClients: (Boolean) -> Unit,
    onAddNewContact: (String, String, String, String) -> Unit,
    onSyncContactsNow: () -> Unit,
    onOpenPayUSheet: () -> Unit,
    onResetOnboarding: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddContactDialog by remember { mutableStateOf(false) }

    var filterOption by remember { mutableStateOf(ContactFilterOption.ALL) }
    var isFilterMenuExpanded by remember { mutableStateOf(false) }

    val activeCount = remember(allClients) {
        allClients.count { it.isInRotation }
    }
    val unselectedCount = remember(allClients, activeCount) {
        allClients.size - activeCount
    }

    val filteredList = remember(allClients, searchQuery, filterOption) {
        val baseList = when (filterOption) {
            ContactFilterOption.ALL -> allClients
            ContactFilterOption.SELECTED -> allClients.filter { it.isInRotation }
            ContactFilterOption.UNSELECTED -> allClients.filter { !it.isInRotation }
        }
        if (searchQuery.isBlank()) {
            baseList
        } else {
            val q = searchQuery.trim().lowercase()
            baseList.filter {
                it.name.lowercase().contains(q) ||
                        it.number.contains(q) ||
                        it.company.lowercase().contains(q) ||
                        it.designation.lowercase().contains(q)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .testTag("luxury_settings_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP BAR WITH BACK BUTTON
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Deck",
                                tint = LuxuryTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Settings & Rotation",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = LuxuryTextPrimary
                            )
                            Text(
                                text = "${allClients.size} Contacts ($activeCount in Deck)",
                                fontSize = 12.sp,
                                color = LuxuryTextSecondary
                            )
                        }
                    }

                    // Quick Sync Contacts Button
                    OutlinedButton(
                        onClick = onSyncContactsNow,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LuxuryBlue),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = LuxuryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Sync", color = LuxuryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // TABS (2 SECTIONS: Deck Contacts, Goals & Preferences)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = LuxuryBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = LuxuryBlue,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Deck Contacts (${allClients.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Black else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Goals & Preferences",
                            fontWeight = if (selectedTab == 1) FontWeight.Black else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            // TAB CONTENT
            when (selectedTab) {
                0 -> {
                    // TAB 0: CONTACTS SELECTION & SEARCH
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Search & Add Bar (Sleek Modern Pipe Design)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = "name, number, company",
                                        fontSize = 13.sp,
                                        color = LuxuryTextMuted
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = LuxuryTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = LuxuryTextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(26.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedBorderColor = LuxuryBlue,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("settings_search_field")
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Add Contact Button matching the sleek pipe design
                            Button(
                                onClick = { showAddContactDialog = true },
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LuxuryBlue),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("settings_add_contact_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Contact",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // CONTACT FILTER DROPDOWN & QUICK CHIPS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Filter Dropdown Anchor
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, LuxuryBlue),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { isFilterMenuExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FilterList,
                                            contentDescription = "Filter Contacts",
                                            tint = LuxuryBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = when (filterOption) {
                                                ContactFilterOption.ALL -> "Filter: All"
                                                ContactFilterOption.SELECTED -> "Filter: Selected"
                                                ContactFilterOption.UNSELECTED -> "Filter: Unselected"
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LuxuryBlue
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = LuxuryBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = isFilterMenuExpanded,
                                    onDismissRequest = { isFilterMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "All Contacts",
                                                    fontWeight = if (filterOption == ContactFilterOption.ALL) FontWeight.Black else FontWeight.Normal,
                                                    fontSize = 13.sp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("(${allClients.size})", color = LuxuryTextMuted, fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            filterOption = ContactFilterOption.ALL
                                            isFilterMenuExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Selected",
                                                    fontWeight = if (filterOption == ContactFilterOption.SELECTED) FontWeight.Black else FontWeight.Normal,
                                                    color = Color(0xFF0D8267),
                                                    fontSize = 13.sp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("($activeCount)", color = Color(0xFF0D8267), fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            filterOption = ContactFilterOption.SELECTED
                                            isFilterMenuExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Unselected",
                                                    fontWeight = if (filterOption == ContactFilterOption.UNSELECTED) FontWeight.Black else FontWeight.Normal,
                                                    color = Color(0xFF64748B),
                                                    fontSize = 13.sp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("($unselectedCount)", color = Color(0xFF64748B), fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            filterOption = ContactFilterOption.UNSELECTED
                                            isFilterMenuExpanded = false
                                        }
                                    )
                                }
                            }

                            // Quick Toggle Chips
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (filterOption == ContactFilterOption.SELECTED) Color(0xFFD1FAE5) else Color(0xFFF1F5F9),
                                    border = BorderStroke(
                                        1.dp,
                                        if (filterOption == ContactFilterOption.SELECTED) Color(0xFF10B981) else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { filterOption = ContactFilterOption.SELECTED }
                                ) {
                                    Text(
                                        text = "Selected ($activeCount)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (filterOption == ContactFilterOption.SELECTED) Color(0xFF047857) else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (filterOption == ContactFilterOption.UNSELECTED) Color(0xFFFEE2E2) else Color(0xFFF1F5F9),
                                    border = BorderStroke(
                                        1.dp,
                                        if (filterOption == ContactFilterOption.UNSELECTED) Color(0xFFF87171) else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { filterOption = ContactFilterOption.UNSELECTED }
                                ) {
                                    Text(
                                        text = "Unselected ($unselectedCount)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (filterOption == ContactFilterOption.UNSELECTED) Color(0xFFB91C1C) else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selection Tools: Select All, Deselect All, Sync Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onSelectAllClients(true) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Done, contentDescription = null, tint = LuxuryBlue, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Select All", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LuxuryBlue)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onSelectAllClients(false) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Deselect All", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF64748B))
                                    }
                                }
                            }

                            Text(
                                text = "Last synced: $lastSyncTimeString",
                                fontSize = 10.sp,
                                color = LuxuryTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fast Lazy Column for contacts
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = filteredList,
                                key = { it.id }
                            ) { client ->
                                ContactRowCard(
                                    client = client,
                                    onToggle = { isChecked ->
                                        onToggleClient(client.id, isChecked)
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: GOALS & PREFERENCES (Daily Goals, WhatsApp Default, VIP & Reset)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // DAILY CALLING GOALS
                        item {
                            Text(
                                text = "Daily Calling Commitment",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = LuxuryTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Choose how many executive client touches you aim to complete daily.",
                                fontSize = 12.sp,
                                color = LuxuryTextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val goalOptions = listOf(5, 8, 12, 15, 20)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                goalOptions.forEach { goal ->
                                    val isSelected = dailyGoal == goal
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) LuxuryBlue else Color.White,
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (isSelected) LuxuryBlue else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { onSelectDailyGoal(goal) }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "$goal",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp,
                                                color = if (isSelected) Color.White else LuxuryTextPrimary
                                            )
                                            Text(
                                                text = "calls",
                                                fontSize = 10.sp,
                                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else LuxuryTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // WHATSAPP APP PREFERENCE SECTION
                        item {
                            Text(
                                text = "WHATSAPP DEFAULT APPLICATION",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = LuxuryTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Choose which WhatsApp opens directly when tapping the chat button.",
                                fontSize = 12.sp,
                                color = LuxuryTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Option 1: WhatsApp Messenger
                                    val isMessenger = preferredWhatsAppPackage == "com.whatsapp"
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onSelectPreferredWhatsAppPackage("com.whatsapp") }
                                            .padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isMessenger,
                                            onClick = { onSelectPreferredWhatsAppPackage("com.whatsapp") },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF25D366))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_whatsapp),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LuxuryTextPrimary)
                                            Text("Standard WhatsApp Messenger", fontSize = 11.sp, color = LuxuryTextSecondary)
                                        }
                                    }

                                    // Option 2: WhatsApp Business
                                    val isBusiness = preferredWhatsAppPackage == "com.whatsapp.w4b"
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onSelectPreferredWhatsAppPackage("com.whatsapp.w4b") }
                                            .padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isBusiness,
                                            onClick = { onSelectPreferredWhatsAppPackage("com.whatsapp.w4b") },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF075E54))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = null,
                                            tint = Color(0xFF075E54),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("WhatsApp Business", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LuxuryTextPrimary)
                                            Text("Official Business account", fontSize = 11.sp, color = LuxuryTextSecondary)
                                        }
                                    }

                                    // Option 3: Always Ask
                                    val isAlwaysAsk = preferredWhatsAppPackage.isNullOrBlank()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onSelectPreferredWhatsAppPackage(null) }
                                            .padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isAlwaysAsk,
                                            onClick = { onSelectPreferredWhatsAppPackage(null) },
                                            colors = RadioButtonDefaults.colors(selectedColor = LuxuryBlue)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Spacer(modifier = Modifier.width(34.dp))
                                        Column {
                                            Text("Always Ask First", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LuxuryTextPrimary)
                                            Text("Prompt between WhatsApp & WhatsApp Business", fontSize = 11.sp, color = LuxuryTextSecondary)
                                        }
                                    }
                                }
                            }
                        }

                        // VIP SUBSCRIPTION CARD
                        item {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFFFFBEB),
                                border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                                shadowElevation = 4.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = subscriptionState.activePlanName,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp,
                                                color = Color(0xFF92400E)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (subscriptionState.isSubscribed) LuxuryEmerald else Color(0xFFD97706)
                                        ) {
                                            Text(
                                                text = if (subscriptionState.isSubscribed) "ACTIVE" else "${subscriptionState.trialDaysRemaining}d REMAINING",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Unlock unlimited client deck cards, PayU recurring payment safety, voice intelligence, and custom touchpoints.",
                                        fontSize = 12.sp,
                                        color = LuxuryTextSecondary,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = onOpenPayUSheet,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Upgrade Subscription (from ₹49)",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // RESET ONBOARDING
                        item {
                            OutlinedButton(
                                onClick = onResetOnboarding,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "View App Tour Again", color = LuxuryTextSecondary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // ADD CONTACT DIALOG WITH +91 FIXED PREFIX & STRICT 10-DIGIT RESTRICTION
    if (showAddContactDialog) {
        var nameInput by remember { mutableStateOf("") }
        var numberInput by remember { mutableStateOf("") }
        var companyInput by remember { mutableStateOf("") }
        var designationInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = {
                Text(text = "Add New Client Card", fontWeight = FontWeight.Black, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Client Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = numberInput,
                        onValueChange = { input ->
                            // Strictly allow only 10 digits
                            val digits = input.filter { it.isDigit() }.take(10)
                            numberInput = digits
                        },
                        label = { Text("Mobile Number *") },
                        prefix = {
                            Text(
                                text = "+91 ",
                                fontWeight = FontWeight.Bold,
                                color = LuxuryTextPrimary,
                                fontSize = 15.sp
                            )
                        },
                        placeholder = { Text("9876543210") },
                        supportingText = {
                            Text(
                                text = "Enter 10-digit number (${numberInput.length}/10)",
                                fontSize = 11.sp,
                                color = if (numberInput.length == 10) LuxuryEmerald else LuxuryTextMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = companyInput,
                        onValueChange = { companyInput = it },
                        label = { Text("Company / Organization") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = designationInput,
                        onValueChange = { designationInput = it },
                        label = { Text("Designation / Role") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank() && numberInput.length == 10) {
                            onAddNewContact(nameInput, "+91 $numberInput", companyInput, designationInput)
                            showAddContactDialog = false
                        }
                    },
                    enabled = nameInput.isNotBlank() && numberInput.length == 10,
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add to Deck", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun ContactRowCard(
    client: ClientEntity,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (client.isInRotation) Color.White else Color(0xFFF1F5F9),
        border = BorderStroke(
            1.dp,
            if (client.isInRotation) Color(0xFFE2E8F0) else Color(0xFFCBD5E1)
        ),
        shadowElevation = if (client.isInRotation) 1.5.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onToggle(!client.isInRotation) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = client.isInRotation,
                onCheckedChange = { onToggle(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = LuxuryBlue,
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (client.isInRotation) LuxuryTextPrimary else LuxuryTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${client.number} • ${client.company}",
                    fontSize = 11.sp,
                    color = LuxuryTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (client.snoozeUntilTimestamp > System.currentTimeMillis()) {
                val remainingHours = ((client.snoozeUntilTimestamp - System.currentTimeMillis()) / (1000 * 60 * 60)).coerceAtLeast(1)
                val remainingDays = (remainingHours / 24).coerceAtLeast(1)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "Snoozed (${remainingDays}d)",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color(0xFFD97706),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
