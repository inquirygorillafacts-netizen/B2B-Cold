package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileData
import com.example.data.local.ClientEntity
import com.example.ui.ProfileSubSection

@Composable
fun ProfileView(
    userProfile: UserProfileData,
    currentSubSection: ProfileSubSection,
    allClients: List<ClientEntity>,
    onSelectSubSection: (ProfileSubSection) -> Unit,
    onUpdateProfile: (name: String, title: String, company: String, email: String, phone: String, dailyGoal: Int) -> Unit,
    onToggleClientRotation: (clientId: String, isChecked: Boolean) -> Unit,
    onSetAllClientsRotation: (isChecked: Boolean) -> Unit,
    onOpenDeckSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen")
    ) {
        // Top Navigation bar for the 4 Profile sub-sections
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ProfileSubSection.values().forEach { section ->
                    val isSelected = currentSubSection == section
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectSubSection(section) },
                        label = {
                            Text(
                                text = when (section) {
                                    ProfileSubSection.OVERVIEW -> "Profile"
                                    ProfileSubSection.EDIT_PROFILE -> "Edit"
                                    ProfileSubSection.SUBSCRIPTION -> "Plans"
                                    ProfileSubSection.SETTINGS -> "Rotation"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("profile_tab_${section.name.lowercase()}")
                    )
                }
            }
        }

        // Content for current sub-section
        when (currentSubSection) {
            ProfileSubSection.OVERVIEW -> {
                ProfileOverviewScreen(
                    userProfile = userProfile,
                    allClients = allClients,
                    onNavigateTo = onSelectSubSection,
                    onOpenDeckSettings = onOpenDeckSettings
                )
            }
            ProfileSubSection.EDIT_PROFILE -> {
                EditProfileScreen(
                    userProfile = userProfile,
                    onSave = onUpdateProfile,
                    onBack = { onSelectSubSection(ProfileSubSection.OVERVIEW) }
                )
            }
            ProfileSubSection.SUBSCRIPTION -> {
                SubscriptionPlansScreen(
                    currentPlan = userProfile.activePlan,
                    onBack = { onSelectSubSection(ProfileSubSection.OVERVIEW) }
                )
            }
            ProfileSubSection.SETTINGS -> {
                ClientCardSettingsScreen(
                    clients = allClients,
                    onToggleClient = onToggleClientRotation,
                    onSetAll = onSetAllClientsRotation,
                    onBack = { onSelectSubSection(ProfileSubSection.OVERVIEW) }
                )
            }
        }
    }
}

@Composable
fun ProfileOverviewScreen(
    userProfile: UserProfileData,
    allClients: List<ClientEntity>,
    onNavigateTo: (ProfileSubSection) -> Unit,
    onOpenDeckSettings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val rotationClientsCount = allClients.count { it.isInRotation }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User Avatar and info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AV",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = userProfile.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = userProfile.company,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = userProfile.activePlan,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // B2B Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            B2BStatCard(
                modifier = Modifier.weight(1f),
                title = "Calls Today",
                value = "${userProfile.callsMadeToday}/${userProfile.dailyCallGoal}",
                subtitle = "Daily goal",
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF16A34A)
            )

            B2BStatCard(
                modifier = Modifier.weight(1f),
                title = "In Rotation",
                value = "$rotationClientsCount/${allClients.size}",
                subtitle = "Active pool",
                icon = Icons.Default.People,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            B2BStatCard(
                modifier = Modifier.weight(1f),
                title = "Pipeline",
                value = userProfile.pipelineValue,
                subtitle = "Tracked deals",
                icon = Icons.Default.Star,
                color = Color(0xFFE5A000)
            )

            B2BStatCard(
                modifier = Modifier.weight(1f),
                title = "Call Streak",
                value = "5 Days",
                subtitle = "Consistency",
                icon = Icons.Default.CheckCircle,
                color = Color(0xFF0284C7)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Navigation Links
        ProfileMenuRow(
            icon = Icons.Default.Settings,
            title = "Deck Studio & Animations",
            subtitle = "Choose from 10 card animation physics & goals",
            onClick = { onOpenDeckSettings() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileMenuRow(
            icon = Icons.Default.Edit,
            title = "Edit Profile",
            subtitle = "Name, title, company & daily target",
            onClick = { onNavigateTo(ProfileSubSection.EDIT_PROFILE) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileMenuRow(
            icon = Icons.Default.FilterList,
            title = "Client Card Rotation",
            subtitle = "Select or uncheck clients for random cards",
            onClick = { onNavigateTo(ProfileSubSection.SETTINGS) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileMenuRow(
            icon = Icons.Default.CardMembership,
            title = "App Subscription",
            subtitle = "Manage corporate B2B networking tiers",
            onClick = { onNavigateTo(ProfileSubSection.SUBSCRIPTION) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun B2BStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EditProfileScreen(
    userProfile: UserProfileData,
    onSave: (name: String, title: String, company: String, email: String, phone: String, dailyGoal: Int) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(userProfile.name) }
    var title by remember { mutableStateOf(userProfile.title) }
    var company by remember { mutableStateOf(userProfile.company) }
    var email by remember { mutableStateOf(userProfile.email) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var dailyGoal by remember { mutableIntStateOf(userProfile.dailyCallGoal) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit Profile Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Corporate Role / Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            label = { Text("Company / Agency") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Work Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Daily Relationship Calls Target",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "How many key client touches do you aim to complete daily instead of browsing feeds?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(5, 8, 10, 15).forEach { goal ->
                val isSelected = dailyGoal == goal
                FilterChip(
                    selected = isSelected,
                    onClick = { dailyGoal = goal },
                    label = { Text("$goal calls") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                onSave(name, title, company, email, phone, dailyGoal)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Save Profile", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SubscriptionPlansScreen(
    currentPlan: String,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Corporate Subscription Plans",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Designed for corporate, real estate, and agency leaders to convert past relationships into recurring business.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Plan 1: Free Starter
        PlanCard(
            title = "Free Starter",
            price = "$0 / month",
            description = "Basic relationship management for individual professionals.",
            features = listOf(
                "Up to 20 client cards in rotation",
                "Basic call log history",
                "Direct calling & SMS",
                "Daily goal tracker"
            ),
            isCurrentPlan = false,
            onSelect = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 2: Pro Networker (Active)
        PlanCard(
            title = "Pro Networker",
            price = "$19 / month",
            description = "High-velocity relationship nurturing with audio intelligence.",
            features = listOf(
                "Unlimited client cards in rotation",
                "Audio voice notes recording & playback",
                "Direct WhatsApp client messaging",
                "Deal pipeline tracking ($18.4M+)",
                "Custom smart rotation frequency"
            ),
            isCurrentPlan = true,
            onSelect = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 3: Enterprise Agency
        PlanCard(
            title = "Enterprise Agency",
            price = "$49 / month",
            description = "Complete corporate networking system for high-performing teams.",
            features = listOf(
                "Multi-seat team sharing & syncing",
                "Advanced client interaction analytics",
                "Priority in-app corporate dialer",
                "Dedicated B2B relationship coach"
            ),
            isCurrentPlan = false,
            onSelect = {}
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    description: String,
    features: List<String>,
    isCurrentPlan: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlan) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isCurrentPlan) 1.5.dp else 1.dp,
            if (isCurrentPlan) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isCurrentPlan) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "CURRENT PLAN",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            features.forEach { feat ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feat,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isCurrentPlan) {
                OutlinedButton(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Select Plan")
                }
            }
        }
    }
}

/**
 * Core Feature C: Contacts Settings (Filtering)
 * By default all contacts are checked (ON) for random cards.
 * User can uncheck contacts they do not want to see in rotation.
 * Only ticked contacts appear on the Home page random cards.
 */
@Composable
fun ClientCardSettingsScreen(
    clients: List<ClientEntity>,
    onToggleClient: (clientId: String, isChecked: Boolean) -> Unit,
    onSetAll: (isChecked: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val activeCount = clients.count { it.isInRotation }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("client_card_settings_screen")
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Client Card Rotation Pool",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "$activeCount of ${clients.size} clients active on Home cards",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "By default, all key clients are enabled. Uncheck any client you do not want to see in the daily random card rotation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Select All / Deselect All Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onSetAll(true) }) {
                        Text("Select All", style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onSetAll(false) }) {
                        Text("Deselect All", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // List of all clients with checkboxes
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(clients, key = { it.id }) { client ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onToggleClient(client.id, !client.isInRotation) }
                        .testTag("rotation_client_item_${client.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (client.isInRotation) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (client.isInRotation) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = client.isInRotation,
                            onCheckedChange = { onToggleClient(client.id, it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("checkbox_${client.id}")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = client.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (client.isInRotation) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${client.designation} • ${client.company}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${client.category} • ${client.dealSize}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (client.isInRotation) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = "Active",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
