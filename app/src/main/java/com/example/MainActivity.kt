package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppTab
import com.example.ui.CallingViewModel
import com.example.ui.components.CallLogView
import com.example.ui.components.ContactsView
import com.example.ui.components.DialpadSheet
import com.example.ui.components.LuxuryBottomNavigationBar
import com.example.ui.components.LuxuryClientCardDeck
import com.example.ui.components.LuxuryExecutiveHeader
import com.example.ui.components.LuxuryOnboardingFlow
import com.example.ui.components.LuxuryPermissionPromptFlow
import com.example.ui.components.LuxurySettingsModalSheet
import com.example.ui.components.ProfileView
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryLightCanvas
import com.example.ui.theme.MyApplicationTheme
import com.example.util.CallHelper

class MainActivity : ComponentActivity() {

    private val viewModel: CallingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = false) {
                TenMillionClientDeckApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TenMillionClientDeckApp(
    viewModel: CallingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // State collections
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsStateWithLifecycle()
    val permissionState by viewModel.permissionState.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val randomClient by viewModel.currentRandomClient.collectAsStateWithLifecycle()
    val rotationClients by viewModel.rotationClients.collectAsStateWithLifecycle()
    val allClients by viewModel.allClients.collectAsStateWithLifecycle()
    val filteredClients by viewModel.filteredClients.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val profileSubSection by viewModel.profileSubSection.collectAsStateWithLifecycle()
    val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()
    val callLogFilter by viewModel.callLogFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val cardAnimationStyle by viewModel.cardAnimationStyle.collectAsStateWithLifecycle()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsStateWithLifecycle()
    val showDialpad by viewModel.showInAppDialer.collectAsStateWithLifecycle()
    val dialpadInput by viewModel.dialpadInput.collectAsStateWithLifecycle()
    val allContacts by viewModel.contacts.collectAsStateWithLifecycle()

    val clientVoiceNotes by viewModel.currentClientVoiceNotes.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()

    var permissionBypassed by remember { mutableStateOf(false) }

    // Next client in stack for 3D stack & drag reveal effect
    val nextClient = remember(randomClient, rotationClients) {
        val pool = rotationClients.filter { it.id != randomClient?.id }
        if (pool.isNotEmpty()) pool.first() else null
    }

    // Permission launcher for high-value contacts & audio recording
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val contactsGranted = perms[Manifest.permission.READ_CONTACTS] == true
        val audioGranted = perms[Manifest.permission.RECORD_AUDIO] == true
        viewModel.updatePermissions(
            hasContacts = contactsGranted,
            hasCallLog = true,
            hasCallPhone = true,
            hasRecordAudio = audioGranted
        )
    }

    fun requestAllPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    // Check permissions on start
    LaunchedEffect(Unit) {
        val hasContacts = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        val hasRecordAudio = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.updatePermissions(
            hasContacts = hasContacts,
            hasCallLog = true,
            hasCallPhone = true,
            hasRecordAudio = hasRecordAudio
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryLightCanvas)
            .testTag("app_root_container")
    ) {
        // STEP 1: Ultra-Slick Colorful 5-Slide Onboarding Flow on White Canvas
        if (!hasCompletedOnboarding) {
            LuxuryOnboardingFlow(
                onComplete = {
                    viewModel.completeOnboarding()
                }
            )
            return@Box
        }

        // STEP 2: Frictionless 2-Card Permission Prompt Flow
        if (permissionState.isAnyMissing && !permissionBypassed) {
            LuxuryPermissionPromptFlow(
                permissionState = permissionState,
                onRequestPermissions = { requestAllPermissions() },
                onContinueAnyway = {
                    permissionBypassed = true
                }
            )
            return@Box
        }

        // STEP 3: Complete App Structure with Top Safe Header + Bottom Navigation Bar
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                // Header with safe statusBarsPadding (never overlaps SIM, battery, status icons)
                LuxuryExecutiveHeader(
                    callsDoneToday = userProfile.callsMadeToday,
                    dailyGoal = userProfile.dailyCallGoal,
                    onOpenSettings = {
                        viewModel.toggleSettingsSheet(true)
                    },
                    onOpenDialpad = {
                        viewModel.toggleInAppDialer(true)
                    },
                    onRotateNext = {
                        viewModel.pickRandomClient()
                    }
                )
            },
            bottomBar = {
                // Professional bottom navigation bar (Deck, Call Log, Clients, Profile)
                LuxuryBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        viewModel.setTab(tab)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppTab.HOME -> {
                        // Subtle ambient light glows
                        Box(
                            modifier = Modifier
                                .size(360.dp)
                                .align(Alignment.TopCenter)
                                .blur(110.dp)
                                .background(LuxuryBlue.copy(alpha = 0.04f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(340.dp)
                                .align(Alignment.BottomCenter)
                                .blur(110.dp)
                                .background(LuxuryEmerald.copy(alpha = 0.04f), CircleShape)
                        )

                        // 3D Swipeable Hero Client Card Deck
                        LuxuryClientCardDeck(
                            client = randomClient,
                            nextClient = nextClient,
                            animationStyle = cardAnimationStyle,
                            voiceNotes = clientVoiceNotes,
                            playbackState = playbackState,
                            recordingState = recordingState,
                            onCallClick = { client ->
                                viewModel.callClient(context, client)
                            },
                            onWhatsAppClick = { client ->
                                viewModel.openWhatsApp(context, client)
                            },
                            onPlayVoiceNote = { note ->
                                viewModel.playVoiceNote(note)
                            },
                            onStopPlayback = {
                                viewModel.stopAudioPlayback()
                            },
                            onStartRecording = {
                                viewModel.startRecordingVoiceNote()
                            },
                            onStopRecording = { clientId, summary ->
                                viewModel.stopRecordingVoiceNote(clientId, summary)
                            },
                            onCancelRecording = {
                                viewModel.cancelRecordingVoiceNote()
                            },
                            onSwipeNext = {
                                viewModel.pickRandomClient()
                            }
                        )
                    }

                    AppTab.CALL_LOG -> {
                        CallLogView(
                            logs = callLogs,
                            selectedFilter = callLogFilter,
                            onFilterSelect = { filter ->
                                viewModel.setCallLogFilter(filter)
                            },
                            onCallClick = { number ->
                                CallHelper.makeCall(context, number)
                            },
                            onSmsClick = { number ->
                                CallHelper.sendSms(context, number)
                            },
                            onCopyClick = { number ->
                                CallHelper.copyToClipboard(context, number)
                            },
                            onOpenDialpadClick = { number ->
                                viewModel.setDialpadInput(number)
                                viewModel.toggleInAppDialer(true)
                            }
                        )
                    }

                    AppTab.CONTACTS -> {
                        ContactsView(
                            clients = filteredClients,
                            searchQuery = searchQuery,
                            onSearchChange = { q ->
                                viewModel.setSearchQuery(q)
                            },
                            onSelectClient = { client ->
                                viewModel.selectClientForCard(client)
                            },
                            onCallClick = { client ->
                                viewModel.callClient(context, client)
                            },
                            onWhatsAppClick = { client ->
                                viewModel.openWhatsApp(context, client)
                            },
                            onSmsClick = { client ->
                                CallHelper.sendSms(context, client.number)
                            },
                            onOpenDialpadClick = { number ->
                                viewModel.setDialpadInput(number)
                                viewModel.toggleInAppDialer(true)
                            }
                        )
                    }

                    AppTab.PROFILE -> {
                        ProfileView(
                            userProfile = userProfile,
                            currentSubSection = profileSubSection,
                            allClients = allClients,
                            onSelectSubSection = { section ->
                                viewModel.setProfileSubSection(section)
                            },
                            onUpdateProfile = { name, title, company, email, phone, dailyGoal ->
                                viewModel.updateUserProfile(name, title, company, email, phone, dailyGoal)
                            },
                            onToggleClientRotation = { clientId, isChecked ->
                                viewModel.toggleClientRotation(clientId, isChecked)
                            },
                            onSetAllClientsRotation = { isChecked ->
                                viewModel.setAllClientsRotation(isChecked)
                            }
                        )
                    }
                }
            }
        }

        // STEP 4: Settings & Card Animation Studio (Header Only Modal Sheet)
        if (showSettingsSheet) {
            LuxurySettingsModalSheet(
                selectedStyle = cardAnimationStyle,
                dailyGoal = userProfile.dailyCallGoal,
                allClients = allClients,
                onSelectStyle = { style ->
                    viewModel.setCardAnimationStyle(style)
                },
                onSelectDailyGoal = { goal ->
                    viewModel.setDailyCallGoal(goal)
                },
                onToggleClient = { clientId, isChecked ->
                    viewModel.toggleClientRotation(clientId, isChecked)
                },
                onSelectAllClients = { isChecked ->
                    viewModel.setAllClientsRotation(isChecked)
                },
                onResetOnboarding = {
                    viewModel.resetOnboarding()
                },
                onDismiss = {
                    viewModel.toggleSettingsSheet(false)
                }
            )
        }

        // In-App Dialpad Bottom Sheet (Accessible from Header)
        if (showDialpad) {
            DialpadSheet(
                inputNumber = dialpadInput,
                suggestions = allContacts.filter {
                    val clean = dialpadInput.replace(Regex("[^0-9+]"), "")
                    clean.isNotEmpty() && (it.number.contains(clean) || it.name.lowercase().contains(dialpadInput.lowercase()))
                }.take(3),
                onDigitClick = { digit ->
                    viewModel.onDialDigit(digit)
                },
                onBackspaceClick = {
                    viewModel.onDialBackspace()
                },
                onClearAll = {
                    viewModel.onDialClear()
                },
                onCallClick = { number ->
                    viewModel.toggleInAppDialer(false)
                    CallHelper.makeCall(context, number)
                },
                onSelectSuggestion = { contact ->
                    viewModel.setDialpadInput(contact.number)
                },
                onDismiss = {
                    viewModel.toggleInAppDialer(false)
                }
            )
        }
    }
}
