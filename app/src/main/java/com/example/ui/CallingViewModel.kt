package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ClientRelationshipRepository
import com.example.data.ContactsRepository
import com.example.data.UserProfileData
import com.example.data.local.ClientEntity
import com.example.data.local.VoiceNoteEntity
import com.example.model.CardAnimationStyle
import com.example.model.ContactItem
import com.example.util.CallHelper
import com.example.util.PlaybackState
import com.example.util.RecordingState
import com.example.util.VoiceAudioManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class PermissionState(
    val hasContacts: Boolean = false,
    val hasRecordAudio: Boolean = false,
    val hasCallLog: Boolean = false,
    val hasCallPhone: Boolean = false
) {
    val allGranted: Boolean
        get() = hasContacts && hasRecordAudio && hasCallLog && hasCallPhone
}

data class SyncProgressState(
    val isSyncing: Boolean = false,
    val progressPercent: Int = 0,
    val statusMessage: String = "",
    val isCompleted: Boolean = false
)

data class DiskRecordingItem(
    val file: java.io.File,
    val fileName: String,
    val formattedSize: String,
    val formattedDate: String,
    val clientName: String?
)

class CallingViewModel(application: Application) : AndroidViewModel(application) {

    private val contactsRepo = ContactsRepository(application)
    private val clientRepo = ClientRelationshipRepository(application)
    private val audioManager = VoiceAudioManager(application)

    private val _permissionState = MutableStateFlow(
        PermissionState(
            hasContacts = contactsRepo.hasContactsPermission(),
            hasRecordAudio = androidx.core.content.ContextCompat.checkSelfPermission(
                application,
                android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED,
            hasCallLog = com.example.util.CallLogHelper.hasCallLogPermission(application),
            hasCallPhone = androidx.core.content.ContextCompat.checkSelfPermission(
                application,
                android.Manifest.permission.CALL_PHONE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    )
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    private val _userProfile = MutableStateFlow(clientRepo.getUserProfileData())
    val userProfile: StateFlow<UserProfileData> = _userProfile.asStateFlow()

    // Real-time animated sync progress state
    private val _syncProgressState = MutableStateFlow(SyncProgressState())
    val syncProgressState: StateFlow<SyncProgressState> = _syncProgressState.asStateFlow()

    // 10-Card Physics swipe dynamics
    private val _cardAnimationStyle = MutableStateFlow(clientRepo.getCardAnimationStyle())
    val cardAnimationStyle: StateFlow<CardAnimationStyle> = _cardAnimationStyle.asStateFlow()

    // WhatsApp preference (WhatsApp Messenger vs WhatsApp Business)
    private val _preferredWhatsAppPackage = MutableStateFlow(clientRepo.getPreferredWhatsAppPackage())
    val preferredWhatsAppPackage: StateFlow<String?> = _preferredWhatsAppPackage.asStateFlow()

    private val _pendingWhatsAppClient = MutableStateFlow<ClientEntity?>(null)
    val pendingWhatsAppClient: StateFlow<ClientEntity?> = _pendingWhatsAppClient.asStateFlow()

    val allClients: StateFlow<List<ClientEntity>> = clientRepo.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rotationClients: StateFlow<List<ClientEntity>> = clientRepo.getRotationClients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentRandomClient = MutableStateFlow<ClientEntity?>(null)
    val currentRandomClient: StateFlow<ClientEntity?> = _currentRandomClient.asStateFlow()

    private val _currentClientVoiceNotes = MutableStateFlow<List<VoiceNoteEntity>>(emptyList())
    val currentClientVoiceNotes: StateFlow<List<VoiceNoteEntity>> = _currentClientVoiceNotes.asStateFlow()

    private var voiceNotesJob: kotlinx.coroutines.Job? = null

    private val _diskRecordingsList = MutableStateFlow<List<DiskRecordingItem>>(emptyList())
    val diskRecordingsList: StateFlow<List<DiskRecordingItem>> = _diskRecordingsList.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = audioManager.playbackState
    val recordingState: StateFlow<RecordingState> = audioManager.recordingState

    private val _contacts = MutableStateFlow<List<ContactItem>>(emptyList())
    val contacts: StateFlow<List<ContactItem>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 5-Slide Onboarding State (Persisted in SharedPreferences so it shows only once)
    private val _hasCompletedOnboarding = MutableStateFlow(clientRepo.hasCompletedOnboarding())
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    // Dedicated Settings Page State (Full Screen Navigation)
    private val _isSettingsScreenOpen = MutableStateFlow(false)
    val isSettingsScreenOpen: StateFlow<Boolean> = _isSettingsScreenOpen.asStateFlow()

    // PayU Subscription State & Modal
    private val _subscriptionState = MutableStateFlow(clientRepo.getSubscriptionState())
    val subscriptionState: StateFlow<com.example.model.SubscriptionState> = _subscriptionState.asStateFlow()

    private val _showPayUSheet = MutableStateFlow(false)
    val showPayUSheet: StateFlow<Boolean> = _showPayUSheet.asStateFlow()

    private val _lastSyncTimeString = MutableStateFlow(clientRepo.getLastSyncTimeString())
    val lastSyncTimeString: StateFlow<String> = _lastSyncTimeString.asStateFlow()

    // Filtered B2B clients list for search - computed on Dispatchers.Default for maximum speed
    val filteredClients: StateFlow<List<ClientEntity>> = combine(
        allClients,
        _searchQuery
    ) { clientList, query ->
        if (query.isBlank()) {
            clientList
        } else {
            val q = query.trim().lowercase()
            clientList.filter {
                it.name.lowercase().contains(q) ||
                        it.company.lowercase().contains(q) ||
                        it.designation.lowercase().contains(q) ||
                        it.number.replace(" ", "").contains(q)
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                // Ensure initial seed if database is empty
                clientRepo.ensureInitialData()

                if (contactsRepo.hasContactsPermission()) {
                    // 10-Minute Cooldown Rule: Only auto-sync on app open if >= 10 minutes have passed
                    if (clientRepo.shouldAutoSyncOnAppOpen()) {
                        clientRepo.syncDeviceContacts(force = false)
                    }
                    // Refresh touchpoints from call history in background
                    clientRepo.syncCallHistoryTouchpoints()
                }
                _lastSyncTimeString.value = clientRepo.getLastSyncTimeString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Observe rotation clients reactive flow
        viewModelScope.launch {
            rotationClients.collect { list ->
                if (list.isNotEmpty() && _currentRandomClient.value == null) {
                    pickRandomClientFromList(list)
                } else if (list.isNotEmpty() && _currentRandomClient.value?.let { curr -> list.none { it.id == curr.id } } == true) {
                    // Only auto-switch if the client was completely removed from the database
                    val stillExists = allClients.value.any { it.id == _currentRandomClient.value?.id }
                    if (!stillExists) {
                        pickRandomClientFromList(list)
                    }
                }
            }
        }
        VoiceAudioManager.migrateLegacyRecordings(getApplication())
        refreshDiskRecordings()
    }

    fun setCardAnimationStyle(style: CardAnimationStyle) {
        _cardAnimationStyle.value = style
        clientRepo.setCardAnimationStyle(style)
    }

    fun setPreferredWhatsAppPackage(pkg: String?) {
        _preferredWhatsAppPackage.value = pkg
        clientRepo.setPreferredWhatsAppPackage(pkg)
    }

    fun syncContactsNow() {
        viewModelScope.launch {
            _syncProgressState.value = SyncProgressState(
                isSyncing = true,
                progressPercent = 8,
                statusMessage = "Connecting to device phonebook...",
                isCompleted = false
            )
            _isLoading.value = true
            try {
                // Real sync with progressive updates
                clientRepo.syncDeviceContacts(force = true) { percent, msg ->
                    _syncProgressState.value = _syncProgressState.value.copy(
                        progressPercent = percent,
                        statusMessage = msg,
                        isCompleted = percent >= 100
                    )
                }
                _lastSyncTimeString.value = clientRepo.getLastSyncTimeString()
                delay(700) // allow user to see 100% completion
            } catch (e: Exception) {
                _syncProgressState.value = _syncProgressState.value.copy(
                    progressPercent = 100,
                    statusMessage = "Sync completed",
                    isCompleted = true
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissSyncDialog() {
        _syncProgressState.value = SyncProgressState(isSyncing = false)
    }

    fun addNewContact(name: String, number: String, company: String, designation: String) {
        viewModelScope.launch {
            clientRepo.addNewContact(name, number, company, designation)
        }
    }

    fun togglePayUSheet(show: Boolean) {
        _showPayUSheet.value = show
    }

    fun activateSubscription(plan: com.example.model.SubscriptionPlan, txId: String) {
        clientRepo.activateSubscription(plan, txId)
        _subscriptionState.value = clientRepo.getSubscriptionState()
    }

    fun pickRandomClient() {
        audioManager.stopPlayback()
        val pool = rotationClients.value
        if (pool.isNotEmpty()) {
            val currentId = _currentRandomClient.value?.id
            val eligible = if (pool.size > 1 && currentId != null) {
                pool.filter { it.id != currentId }
            } else {
                pool
            }
            val next = eligible.randomOrNull() ?: pool.first()
            _currentRandomClient.value = next
            loadVoiceNotesForClient(next)
            checkClientCallLogInstant(next)
        } else {
            _currentRandomClient.value = null
            _currentClientVoiceNotes.value = emptyList()
        }
    }

    private fun pickRandomClientFromList(list: List<ClientEntity>) {
        if (list.isNotEmpty()) {
            val client = list[Random.nextInt(list.size)]
            _currentRandomClient.value = client
            loadVoiceNotesForClient(client)
            checkClientCallLogInstant(client)
        }
    }

    private fun checkClientCallLogInstant(client: ClientEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val logTs = com.example.util.CallLogHelper.getLastCallTimestamp(getApplication(), client.number)
                if (logTs != null && logTs > client.lastContactedTimestamp) {
                    clientRepo.updateLastContacted(client.id, logTs)
                    if (_currentRandomClient.value?.id == client.id) {
                        _currentRandomClient.value = _currentRandomClient.value?.copy(lastContactedTimestamp = logTs)
                    }
                }
            } catch (e: Exception) {
                // non-blocking
            }
        }
    }

    private fun loadVoiceNotesForClient(client: ClientEntity) {
        voiceNotesJob?.cancel()
        voiceNotesJob = viewModelScope.launch(Dispatchers.IO) {
            clientRepo.syncDiskVoiceNotesForClient(client.id)
            clientRepo.getVoiceNotesForClient(client.id).collect { notes ->
                _currentClientVoiceNotes.value = notes.take(5)
            }
        }
    }

    // Call execution & CRM update
    fun callClient(context: Context, client: ClientEntity) {
        CallHelper.makeDirectCall(context, client.number)
        recordClientContacted(client.id)
    }

    // WhatsApp execution: check single-choice preference
    fun openWhatsApp(context: Context, client: ClientEntity) {
        val preferred = _preferredWhatsAppPackage.value
        if (!preferred.isNullOrBlank()) {
            CallHelper.openWhatsApp(context, client.number, preferred)
            recordClientContacted(client.id)
        } else {
            _pendingWhatsAppClient.value = client
        }
    }

    fun selectWhatsAppPreference(context: Context, pkg: String, rememberChoice: Boolean) {
        if (rememberChoice) {
            setPreferredWhatsAppPackage(pkg)
        }
        val client = _pendingWhatsAppClient.value
        if (client != null) {
            CallHelper.openWhatsApp(context, client.number, pkg)
            recordClientContacted(client.id)
            _pendingWhatsAppClient.value = null
        }
    }

    fun dismissWhatsAppPicker() {
        _pendingWhatsAppClient.value = null
    }

    // Snooze / Reschedule for 3 days (or custom days)
    fun snoozeClient(client: ClientEntity, days: Int = 3) {
        viewModelScope.launch {
            clientRepo.snoozeClient(client.id, days)
            pickRandomClient()
        }
    }

    fun recordClientContacted(clientId: String) {
        viewModelScope.launch {
            clientRepo.updateLastContacted(clientId, System.currentTimeMillis())
            val newCount = clientRepo.incrementCallsMadeToday()
            _userProfile.value = _userProfile.value.copy(
                callsMadeToday = newCount
            )
        }
    }

    // Audio Playback
    fun playVoiceNote(voiceNote: VoiceNoteEntity) {
        audioManager.playVoiceNote(
            voiceNoteId = voiceNote.id,
            filePath = voiceNote.audioFilePath,
            fallbackDurationSeconds = voiceNote.durationSeconds
        )
    }

    fun stopAudioPlayback() {
        audioManager.stopPlayback()
    }

    // Audio Recording
    fun startRecordingVoiceNote(clientNumber: String = ""): Boolean {
        val numberToUse = if (clientNumber.isNotBlank()) {
            clientNumber
        } else {
            _currentRandomClient.value?.number ?: ""
        }
        return audioManager.startRecording(numberToUse)
    }

    fun stopRecordingVoiceNote(clientId: String, summary: String = "") {
        val currentList = _currentClientVoiceNotes.value
        if (currentList.size >= 5) {
            audioManager.cancelRecording()
            return
        }
        val (filePath, duration) = audioManager.stopRecording()
        if (filePath != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val newNote = clientRepo.saveVoiceNoteAndReturn(
                    clientId = clientId,
                    audioFilePath = filePath,
                    durationSeconds = duration,
                    summary = summary.ifBlank { "Client voice memory #${currentList.size + 1}" }
                )
                val updated = (currentList + newNote).distinctBy { it.id }.take(5)
                withContext(Dispatchers.Main) {
                    _currentClientVoiceNotes.value = updated
                }
                refreshDiskRecordings()
            }
        }
    }

    fun deleteVoiceNote(voiceNoteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (playbackState.value.currentVoiceNoteId == voiceNoteId) {
                audioManager.stopPlayback()
            }
            clientRepo.deleteVoiceNote(voiceNoteId)
            val updated = _currentClientVoiceNotes.value.filter { it.id != voiceNoteId }
            withContext(Dispatchers.Main) {
                _currentClientVoiceNotes.value = updated
            }
            refreshDiskRecordings()
        }
    }

    fun cancelRecordingVoiceNote() {
        audioManager.cancelRecording()
    }

    // Client card rotation settings
    fun toggleClientRotation(clientId: String, isChecked: Boolean) {
        viewModelScope.launch {
            clientRepo.setClientRotation(clientId, isChecked)
        }
    }

    fun setAllClientsRotation(isChecked: Boolean) {
        viewModelScope.launch {
            clientRepo.setAllClientsRotation(isChecked)
        }
    }

    fun updatePermissions(
        hasContacts: Boolean,
        hasRecordAudio: Boolean,
        hasCallLog: Boolean = false,
        hasCallPhone: Boolean = false
    ) {
        _permissionState.value = PermissionState(
            hasContacts = hasContacts,
            hasRecordAudio = hasRecordAudio,
            hasCallLog = hasCallLog,
            hasCallPhone = hasCallPhone
        )
    }

    /**
     * Called when user grants permissions in the gatekeeper screen for the very first time.
     */
    fun syncContactsOnFirstGrant() {
        syncContactsNow()
    }

    /**
     * Non-blocking call log update when returning to app from a phone call or WhatsApp.
     * Absolutely NO full contacts sync or intrusive loading dialog is shown.
     */
    fun checkRecentCallLogsOnResume() {
        viewModelScope.launch {
            try {
                if (com.example.util.CallLogHelper.hasCallLogPermission(getApplication())) {
                    clientRepo.syncCallHistoryTouchpoints()
                }
            } catch (e: Exception) {
                // non-blocking
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun completeOnboarding() {
        clientRepo.setCompletedOnboarding(true)
        _hasCompletedOnboarding.value = true
    }

    fun resetOnboarding() {
        clientRepo.setCompletedOnboarding(false)
        _hasCompletedOnboarding.value = false
    }

    fun openSettingsScreen() {
        _isSettingsScreenOpen.value = true
    }

    fun closeSettingsScreen() {
        _isSettingsScreenOpen.value = false
    }

    fun setDailyCallGoal(goal: Int) {
        clientRepo.setDailyCallGoal(goal)
        _userProfile.value = _userProfile.value.copy(dailyCallGoal = goal)
    }

    fun refreshDiskRecordings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val files = VoiceAudioManager.getAllDiskRecordings(context)
                val allList = allClients.value
                val items = files.map { file ->
                    val nameNoExt = file.nameWithoutExtension
                    val clientNum = when {
                        nameNoExt.contains("-") -> nameNoExt.substringBeforeLast("-")
                        nameNoExt.contains("_") -> nameNoExt.split("_").getOrNull(1) ?: ""
                        else -> ""
                    }
                    val clientMatch = allList.find { it.number.replace(Regex("[^0-9]"), "").endsWith(clientNum) }
                    val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a", java.util.Locale.US).format(java.util.Date(file.lastModified()))
                    DiskRecordingItem(
                        file = file,
                        fileName = file.name,
                        formattedSize = VoiceAudioManager.formatFileSize(file.length()),
                        formattedDate = dateStr,
                        clientName = clientMatch?.name ?: if (clientNum.isNotBlank()) "Client ($clientNum)" else "Executive Voice Note"
                    )
                }
                _diskRecordingsList.value = items
            } catch (e: Exception) {
                // non-blocking
            }
        }
    }

    fun openRecordingsLocation(context: Context) {
        val folder = VoiceAudioManager.getAppRecordingsDirectory(context)

        // 1. Run sync to public Downloads & MediaStore in background
        viewModelScope.launch(Dispatchers.IO) {
            VoiceAudioManager.syncAllToPublicDownloads(context)
            val files = VoiceAudioManager.getAllDiskRecordings(context)
            if (files.isNotEmpty()) {
                try {
                    android.media.MediaScannerConnection.scanFile(
                        context,
                        files.map { it.absolutePath }.toTypedArray(),
                        null,
                        null
                    )
                } catch (e: Exception) {
                    // non-blocking
                }
            }
        }

        // 2. Copy exact folder path to clipboard
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Recordings Folder Path", folder.absolutePath)
            clipboard?.setPrimaryClip(clip)
        } catch (e: Exception) {
            // ignore
        }

        val pm = context.packageManager
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            folder
        )

        // Known Files applications across Android ecosystem
        val fileAppPackages = listOf(
            "com.google.android.apps.nbu.files",      // Files by Google
            "com.google.android.documentsui",        // Google DocumentsUI
            "com.android.documentsui",               // AOSP DocumentsUI
            "com.sec.android.app.myfiles",           // Samsung My Files
            "com.mi.android.globalFileexplorer",     // Xiaomi / Redmi / Poco File Manager
            "com.coloros.filemanager",               // Oppo / Realme File Manager
            "com.oneplus.filemanager",               // OnePlus File Manager
            "com.vivo.filemanager",                  // Vivo / iQOO File Manager
            "com.transsion.filemanager"              // Tecno / Infinix File Manager
        )

        val installedFilesPackage = fileAppPackages.firstOrNull { pkg ->
            try {
                pm.getPackageInfo(pkg, 0)
                true
            } catch (e: Exception) {
                false
            }
        }

        var openedDirectly = false

        // Try 1: Launch targeted Files app directly (NO chooser dialog)
        if (installedFilesPackage != null) {
            val mimeTypes = listOf("vnd.android.document/directory", "resource/folder", "*/*")
            for (mime in mimeTypes) {
                try {
                    val directIntent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, mime)
                        addFlags(
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            android.content.Intent.FLAG_GRANT_PREFIX_URI_PERMISSION or
                            android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        setPackage(installedFilesPackage)
                    }
                    if (directIntent.resolveActivity(pm) != null) {
                        context.startActivity(directIntent)
                        openedDirectly = true
                        break
                    }
                } catch (e: Exception) {
                    // Try next mime
                }
            }

            // If action view with URI didn't resolve for the package, open the Files app main interface directly
            if (!openedDirectly) {
                try {
                    val launchIntent = pm.getLaunchIntentForPackage(installedFilesPackage)?.apply {
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                        openedDirectly = true
                    }
                } catch (e: Exception) {
                    // fallback below
                }
            }
        }

        // Try 2: Generic direct ACTION_VIEW (NO createChooser to avoid the "open option" dialog)
        if (!openedDirectly) {
            try {
                val directIntent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "vnd.android.document/directory")
                    addFlags(
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        android.content.Intent.FLAG_GRANT_PREFIX_URI_PERMISSION or
                        android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                }
                context.startActivity(directIntent)
                openedDirectly = true
            } catch (e: Exception) {
                try {
                    val fallbackIntent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "*/*")
                        addFlags(
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                    }
                    context.startActivity(fallbackIntent)
                    openedDirectly = true
                } catch (e2: Exception) {
                    try {
                        val browseIntent = android.content.Intent("android.provider.action.BROWSE").apply {
                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(browseIntent)
                        openedDirectly = true
                    } catch (e3: Exception) {
                        // ignore
                    }
                }
            }
        }

        val count = _diskRecordingsList.value.size
        val msg = if (count > 0) {
            "Files app open ho gaya! Downloads me 'B2B_ColdCaller_Recordings' ya upar 'Audio' tab dekhein ($count notes)"
        } else {
            "Files app open ho gaya! Abhi koi note record nahi hai — pehle kisi card par Mic se recording karein."
        }

        android.widget.Toast.makeText(
            context,
            msg,
            android.widget.Toast.LENGTH_LONG
        ).show()
    }

    fun shareRecordingFile(context: Context, file: java.io.File) {
        try {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Voice Recording"))
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Could not share file: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteDiskRecordingFile(file: java.io.File) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = file.absolutePath
            if (file.exists()) {
                file.delete()
            }
            clientRepo.deleteVoiceNoteByFilePath(path)
            refreshDiskRecordings()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.stopPlayback()
    }
}
