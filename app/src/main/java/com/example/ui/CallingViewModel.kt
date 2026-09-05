package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CallLogRepository
import com.example.data.ClientRelationshipRepository
import com.example.data.ContactsRepository
import com.example.data.UserProfileData
import com.example.data.local.ClientEntity
import com.example.data.local.VoiceNoteEntity
import com.example.model.CallLogItem
import com.example.model.CallType
import com.example.model.ContactItem
import com.example.util.CallHelper
import com.example.util.PlaybackState
import com.example.util.RecordingState
import com.example.util.VoiceAudioManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppTab(val title: String) {
    HOME("Home"),
    CALL_LOG("Call Log"),
    CONTACTS("Contacts"),
    PROFILE("Profile")
}

enum class ProfileSubSection(val title: String) {
    OVERVIEW("Profile"),
    EDIT_PROFILE("Edit Profile"),
    SUBSCRIPTION("App Subscription"),
    SETTINGS("Client Card Settings")
}

data class PermissionState(
    val hasContacts: Boolean = false,
    val hasCallLog: Boolean = false,
    val hasCallPhone: Boolean = false,
    val hasRecordAudio: Boolean = false
) {
    val isAnyMissing: Boolean
        get() = !hasContacts || !hasCallLog || !hasCallPhone || !hasRecordAudio

    val isAllGranted: Boolean
        get() = hasContacts && hasCallLog && hasCallPhone && hasRecordAudio
}

class CallingViewModel(application: Application) : AndroidViewModel(application) {

    private val contactsRepo = ContactsRepository(application)
    private val callLogRepo = CallLogRepository(application)
    private val clientRepo = ClientRelationshipRepository(application)
    private val audioManager = VoiceAudioManager(application)

    private val _permissionState = MutableStateFlow(
        PermissionState(
            hasContacts = contactsRepo.hasContactsPermission(),
            hasCallLog = callLogRepo.hasCallLogPermission(),
            hasCallPhone = false,
            hasRecordAudio = false
        )
    )
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    // Default opened tab is HOME as specified in PRD
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Sub-section within Profile tab
    private val _profileSubSection = MutableStateFlow(ProfileSubSection.OVERVIEW)
    val profileSubSection: StateFlow<ProfileSubSection> = _profileSubSection.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfileData())
    val userProfile: StateFlow<UserProfileData> = _userProfile.asStateFlow()

    val allClients: StateFlow<List<ClientEntity>> = clientRepo.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rotationClients: StateFlow<List<ClientEntity>> = clientRepo.rotationClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentRandomClient = MutableStateFlow<ClientEntity?>(null)
    val currentRandomClient: StateFlow<ClientEntity?> = _currentRandomClient.asStateFlow()

    private val _currentClientVoiceNotes = MutableStateFlow<List<VoiceNoteEntity>>(emptyList())
    val currentClientVoiceNotes: StateFlow<List<VoiceNoteEntity>> = _currentClientVoiceNotes.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = audioManager.playbackState
    val recordingState: StateFlow<RecordingState> = audioManager.recordingState

    private val _contacts = MutableStateFlow<List<ContactItem>>(emptyList())
    val contacts: StateFlow<List<ContactItem>> = _contacts.asStateFlow()

    private val _callLogs = MutableStateFlow<List<CallLogItem>>(emptyList())
    val callLogs: StateFlow<List<CallLogItem>> = _callLogs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _callLogFilter = MutableStateFlow<CallType?>(null)
    val callLogFilter: StateFlow<CallType?> = _callLogFilter.asStateFlow()

    private val _dialpadInput = MutableStateFlow("")
    val dialpadInput: StateFlow<String> = _dialpadInput.asStateFlow()

    private val _showInAppDialer = MutableStateFlow(false)
    val showInAppDialer: StateFlow<Boolean> = _showInAppDialer.asStateFlow()

    // 5-Slide Ultra-Slick Onboarding State
    private val _hasCompletedOnboarding = MutableStateFlow(false)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    // 10 Card Physics & Animation Styles
    private val _cardAnimationStyle = MutableStateFlow(com.example.model.CardAnimationStyle.LIQUID_GLASS_STACK)
    val cardAnimationStyle: StateFlow<com.example.model.CardAnimationStyle> = _cardAnimationStyle.asStateFlow()

    // Luxury Settings Overlay (Header Only Triggered)
    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    // Filtered B2B clients list for Contacts screen
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCallLogs: StateFlow<List<CallLogItem>> = combine(
        _callLogs,
        _callLogFilter,
        _searchQuery
    ) { logs, filter, query ->
        var list = logs
        if (filter != null) {
            list = list.filter { it.type == filter }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                (it.cachedName?.lowercase()?.contains(q) == true) || it.number.contains(q)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                clientRepo.ensureInitialData()
                val fetchedContacts = contactsRepo.getContacts()
                val fetchedLogs = callLogRepo.getCallLogs()
                _contacts.value = fetchedContacts
                _callLogs.value = fetchedLogs

                // Observe rotation clients and pick first random client if not set
                rotationClients.collect { list ->
                    if (list.isNotEmpty() && _currentRandomClient.value == null) {
                        pickRandomClientFromList(list)
                    } else if (list.isNotEmpty() && _currentRandomClient.value?.let { curr -> list.none { it.id == curr.id } } == true) {
                        // Current client was removed from rotation
                        pickRandomClientFromList(list)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
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
            val randomClient = eligible[Random.nextInt(eligible.size)]
            _currentRandomClient.value = randomClient
            loadVoiceNotesForClient(randomClient.id)
        }
    }

    private fun pickRandomClientFromList(list: List<ClientEntity>) {
        if (list.isEmpty()) return
        val randomClient = list[Random.nextInt(list.size)]
        _currentRandomClient.value = randomClient
        loadVoiceNotesForClient(randomClient.id)
    }

    fun selectClientForCard(client: ClientEntity) {
        audioManager.stopPlayback()
        _currentRandomClient.value = client
        loadVoiceNotesForClient(client.id)
        _currentTab.value = AppTab.HOME
    }

    private fun loadVoiceNotesForClient(clientId: String) {
        viewModelScope.launch {
            clientRepo.getVoiceNotesForClient(clientId).collect { notes ->
                _currentClientVoiceNotes.value = notes
            }
        }
    }

    // Call Action
    fun callClient(context: Context, client: ClientEntity) {
        audioManager.stopPlayback()
        CallHelper.makeCall(context, client.number)
        onCallMade(client.id)
    }

    // WhatsApp Action
    fun openWhatsApp(context: Context, client: ClientEntity) {
        val greeting = "Hi ${client.name}, hope you are doing well! Alexander here from Apex Commercial. Reaching out regarding our ongoing relationship and follow-up."
        CallHelper.openWhatsApp(context, client.number, greeting)
    }

    // SMS Action
    fun sendSms(context: Context, client: ClientEntity) {
        CallHelper.sendSms(context, client.number)
    }

    fun onCallMade(clientId: String) {
        viewModelScope.launch {
            clientRepo.updateLastContacted(clientId, System.currentTimeMillis())
            _userProfile.value = _userProfile.value.copy(
                callsMadeToday = _userProfile.value.callsMadeToday + 1
            )
            // Refresh current client with updated timestamp
            val updated = clientRepo.allClients
            // Let collection reflect automatically
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
    fun startRecordingVoiceNote(): Boolean {
        return audioManager.startRecording()
    }

    fun stopRecordingVoiceNote(clientId: String, summary: String = "") {
        val (filePath, duration) = audioManager.stopRecording()
        if (filePath != null) {
            viewModelScope.launch {
                clientRepo.saveVoiceNote(
                    clientId = clientId,
                    audioFilePath = filePath,
                    durationSeconds = duration,
                    summary = summary.ifBlank { "Client follow-up note recorded" }
                )
            }
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

    fun updatePermissions(hasContacts: Boolean, hasCallLog: Boolean, hasCallPhone: Boolean, hasRecordAudio: Boolean) {
        _permissionState.value = PermissionState(
            hasContacts = hasContacts,
            hasCallLog = hasCallLog,
            hasCallPhone = hasCallPhone,
            hasRecordAudio = hasRecordAudio
        )
    }

    fun setTab(tab: AppTab) {
        if (tab != AppTab.HOME) {
            audioManager.stopPlayback()
        }
        _currentTab.value = tab
    }

    fun setProfileSubSection(section: ProfileSubSection) {
        _profileSubSection.value = section
    }

    fun updateUserProfile(
        name: String,
        title: String,
        company: String,
        email: String,
        phone: String,
        dailyGoal: Int
    ) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            title = title,
            company = company,
            email = email,
            phone = phone,
            dailyCallGoal = dailyGoal
        )
        _profileSubSection.value = ProfileSubSection.OVERVIEW
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCallLogFilter(filter: CallType?) {
        _callLogFilter.value = filter
    }

    fun toggleInAppDialer(show: Boolean) {
        _showInAppDialer.value = show
    }

    fun onDialDigit(char: Char) {
        if (_dialpadInput.value.length < 20) {
            _dialpadInput.value += char
        }
    }

    fun onDialBackspace() {
        if (_dialpadInput.value.isNotEmpty()) {
            _dialpadInput.value = _dialpadInput.value.dropLast(1)
        }
    }

    fun onDialClear() {
        _dialpadInput.value = ""
    }

    fun setDialpadInput(number: String) {
        _dialpadInput.value = number
    }

    fun completeOnboarding() {
        _hasCompletedOnboarding.value = true
    }

    fun resetOnboarding() {
        _hasCompletedOnboarding.value = false
    }

    fun toggleSettingsSheet(show: Boolean) {
        _showSettingsSheet.value = show
    }

    fun setCardAnimationStyle(style: com.example.model.CardAnimationStyle) {
        _cardAnimationStyle.value = style
    }

    fun setDailyCallGoal(goal: Int) {
        _userProfile.value = _userProfile.value.copy(dailyCallGoal = goal)
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.stopPlayback()
    }
}
