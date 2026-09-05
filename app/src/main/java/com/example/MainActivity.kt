package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ClientEntity
import com.example.model.CardAnimationStyle
import com.example.ui.CallingViewModel
import com.example.ui.components.ContactsPermissionGateScreen
import com.example.ui.components.DeckStudioModal
import com.example.ui.components.LuxuryClientCardDeck
import com.example.ui.components.LuxuryExecutiveHeader
import com.example.ui.components.LuxuryOnboardingFlow
import com.example.ui.components.LuxurySettingsScreen
import com.example.ui.components.ModernSyncProgressDialog
import com.example.ui.components.PayUSubscriptionSheet
import com.example.ui.components.WhatsAppPreferenceDialog
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
    val lifecycleOwner = LocalLifecycleOwner.current

    // Reactive State collections
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsStateWithLifecycle()
    val permissionState by viewModel.permissionState.collectAsStateWithLifecycle()
    val randomClient by viewModel.currentRandomClient.collectAsStateWithLifecycle()
    val rotationClients by viewModel.rotationClients.collectAsStateWithLifecycle()
    val allClients by viewModel.allClients.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val subscriptionState by viewModel.subscriptionState.collectAsStateWithLifecycle()
    val showPayUSheet by viewModel.showPayUSheet.collectAsStateWithLifecycle()
    val lastSyncTimeString by viewModel.lastSyncTimeString.collectAsStateWithLifecycle()

    val isSettingsScreenOpen by viewModel.isSettingsScreenOpen.collectAsStateWithLifecycle()

    val clientVoiceNotes by viewModel.currentClientVoiceNotes.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()

    // 10 Card Physics and WhatsApp Preference
    val cardAnimationStyle by viewModel.cardAnimationStyle.collectAsStateWithLifecycle()
    val preferredWhatsAppPackage by viewModel.preferredWhatsAppPackage.collectAsStateWithLifecycle()
    val pendingWhatsAppClient by viewModel.pendingWhatsAppClient.collectAsStateWithLifecycle()
    val syncProgressState by viewModel.syncProgressState.collectAsStateWithLifecycle()

    var hasAttemptedPermissionRequest by remember { mutableStateOf(false) }
    var pendingCallClient by remember { mutableStateOf<ClientEntity?>(null) }
    var showDeckStudioModal by remember { mutableStateOf(false) }

    // Direct Call Phone launcher (so tapping call immediately dials without opening external dialer)
    val callPhoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        pendingCallClient?.let { client ->
            CallHelper.makeDirectCall(context, client.number)
            viewModel.recordClientContacted(client.id)
        }
        pendingCallClient = null
    }

    // Next client in stack for 3D stack & drag reveal effect
    val nextClient = remember(randomClient, rotationClients) {
        val pool = rotationClients.filter { it.id != randomClient?.id }
        if (pool.isNotEmpty()) pool.first() else null
    }

    // Primary permission launcher for contacts, audio, call log & direct calling
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasAttemptedPermissionRequest = true
        val contactsGranted = perms[Manifest.permission.READ_CONTACTS] == true
        val audioGranted = perms[Manifest.permission.RECORD_AUDIO] == true
        val callLogGranted = perms[Manifest.permission.READ_CALL_LOG] == true
        val callPhoneGranted = perms[Manifest.permission.CALL_PHONE] == true
        viewModel.updatePermissions(
            hasContacts = contactsGranted,
            hasRecordAudio = audioGranted,
            hasCallLog = callLogGranted,
            hasCallPhone = callPhoneGranted
        )
        if (contactsGranted) {
            viewModel.syncContactsOnFirstGrant()
        }
    }

    fun requestRequiredPermissions() {
        hasAttemptedPermissionRequest = true
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE
            )
        )
    }

    // Re-check permissions and silently refresh call history touchpoints on resume
    // NEVER re-triggers full contacts sync or displays sync dialog on resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val hasContacts = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
                val hasAudio = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                val hasCallLog = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALL_LOG
                ) == PackageManager.PERMISSION_GRANTED
                val hasCallPhone = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED

                viewModel.updatePermissions(
                    hasContacts = hasContacts,
                    hasRecordAudio = hasAudio,
                    hasCallLog = hasCallLog,
                    hasCallPhone = hasCallPhone
                )

                // Ultra-lightweight background touchpoint refresh without blocking UI
                viewModel.checkRecentCallLogsOnResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Handle back button when on dedicated Settings screen
    if (isSettingsScreenOpen) {
        BackHandler {
            viewModel.closeSettingsScreen()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryLightCanvas)
            .testTag("app_root_container")
    ) {
        // STEP 1: Ultra-Slick 5-Slide Onboarding Flow
        if (!hasCompletedOnboarding) {
            LuxuryOnboardingFlow(
                onComplete = {
                    viewModel.completeOnboarding()
                }
            )
            return@Box
        }

        // STEP 2: Mandatory Permissions Gatekeeper Screen (Zero bypass without ALL 4 permissions)
        if (!permissionState.allGranted) {
            ContactsPermissionGateScreen(
                permissionState = permissionState,
                hasAskedPermission = hasAttemptedPermissionRequest,
                onRequestPermission = { requestRequiredPermissions() }
            )
            return@Box
        }

        // STEP 3: Dedicated Settings Page (10 Card Physics, Contacts, WhatsApp choice & VIP)
        if (isSettingsScreenOpen) {
            LuxurySettingsScreen(
                dailyGoal = userProfile.dailyCallGoal,
                allClients = allClients,
                subscriptionState = subscriptionState,
                lastSyncTimeString = lastSyncTimeString,
                cardAnimationStyle = cardAnimationStyle,
                preferredWhatsAppPackage = preferredWhatsAppPackage,
                onSelectCardAnimationStyle = { style ->
                    viewModel.setCardAnimationStyle(style)
                },
                onSelectPreferredWhatsAppPackage = { pkg ->
                    viewModel.setPreferredWhatsAppPackage(pkg)
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
                onAddNewContact = { name, number, company, designation ->
                    viewModel.addNewContact(name, number, company, designation)
                },
                onSyncContactsNow = {
                    viewModel.syncContactsNow()
                },
                onOpenPayUSheet = {
                    viewModel.togglePayUSheet(true)
                },
                onResetOnboarding = {
                    viewModel.resetOnboarding()
                },
                onNavigateBack = {
                    viewModel.closeSettingsScreen()
                }
            )
        } else {
            // STEP 4: Pure Clean Card Deck Screen (No Navigation Bar, Open & Spacious Canvas)
            Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFFF4F5F7),
            topBar = {
                // Minimal Header: Remaining Calls Badge + Quick Subscription Pill + Settings Studio
                LuxuryExecutiveHeader(
                    callsDoneToday = userProfile.callsMadeToday,
                    dailyGoal = userProfile.dailyCallGoal,
                    subscriptionState = subscriptionState,
                    onOpenSettings = {
                        viewModel.openSettingsScreen()
                    },
                    onOpenSubscription = {
                        viewModel.togglePayUSheet(true)
                    },
                    onOpenDeckStudio = {
                        showDeckStudioModal = true
                    },
                    onSyncClick = {
                        viewModel.syncContactsNow()
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF4F5F7),
                                Color(0xFFEDEFF3),
                                Color(0xFFE5E8ED)
                            )
                        )
                    )
                    .padding(innerPadding)
            ) {
                // Silky smooth, highly optimized Romantic Card Deck with User's Chosen Dynamic Physics
                LuxuryClientCardDeck(
                    client = randomClient,
                    nextClient = nextClient,
                    animationStyle = cardAnimationStyle,
                    voiceNotes = clientVoiceNotes,
                    playbackState = playbackState,
                    recordingState = recordingState,
                    onCallClick = { client ->
                        // Direct call without opening dialer app
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            CallHelper.makeDirectCall(context, client.number)
                            viewModel.recordClientContacted(client.id)
                        } else {
                            pendingCallClient = client
                            callPhoneLauncher.launch(Manifest.permission.CALL_PHONE)
                        }
                    },
                    onWhatsAppClick = { client ->
                        viewModel.openWhatsApp(context, client)
                    },
                    onSnoozeClick = { client, days ->
                        viewModel.snoozeClient(client, days)
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
                    onDeleteVoiceNote = { noteId ->
                        viewModel.deleteVoiceNote(noteId)
                    },
                    onSwipeNext = {
                        viewModel.pickRandomClient()
                    }
                )
            }
        }
    }

        // STEP 5: PayU Subscription Payment Sheet (₹49, ₹199 Popular, ₹499)
        if (showPayUSheet) {
            PayUSubscriptionSheet(
                subscriptionState = subscriptionState,
                onActivatePlan = { plan, txId ->
                    viewModel.activateSubscription(plan, txId)
                },
                onDismiss = {
                    viewModel.togglePayUSheet(false)
                }
            )
        }

        // STEP 6: Modern Sync Progress Dialog (Animated Percentage Ring & Confetti)
        ModernSyncProgressDialog(
            syncState = syncProgressState,
            onDismiss = {
                viewModel.dismissSyncDialog()
            }
        )

        // STEP 7: WhatsApp Application Preference Dialog (Choice stored, ask once)
        if (pendingWhatsAppClient != null) {
            WhatsAppPreferenceDialog(
                onSelectOption = { pkg, rememberChoice ->
                    viewModel.selectWhatsAppPreference(context, pkg, rememberChoice)
                },
                onDismiss = {
                    viewModel.dismissWhatsAppPicker()
                }
            )
        }

        // STEP 8: 10 Card Physics Deck Studio Modal
        if (showDeckStudioModal) {
            DeckStudioModal(
                currentStyle = cardAnimationStyle,
                onSelectStyle = { style ->
                    viewModel.setCardAnimationStyle(style)
                },
                onDismiss = {
                    showDeckStudioModal = false
                }
            )
        }
    }
}
