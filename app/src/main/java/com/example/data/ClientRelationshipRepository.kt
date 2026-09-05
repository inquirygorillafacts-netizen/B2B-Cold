package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ClientEntity
import com.example.data.local.VoiceNoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

data class UserProfileData(
    val name: String = "Alexander Vance",
    val title: String = "Managing Partner, B2B Growth",
    val company: String = "Apex Commercial Ventures",
    val email: String = "a.vance@apexcommercial.io",
    val phone: String = "+1 (555) 439-8120",
    val dailyCallGoal: Int = 8,
    val callsMadeToday: Int = 3,
    val activePlan: String = "Pro Networker",
    val pipelineValue: String = "$18.4M"
)

class ClientRelationshipRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val clientDao = db.clientDao()
    private val voiceNoteDao = db.voiceNoteDao()

    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()

    fun getRotationClients(): Flow<List<ClientEntity>> {
        return clientDao.getRotationClients(System.currentTimeMillis())
    }

    suspend fun ensureInitialData() = withContext(Dispatchers.IO) {
        val count = clientDao.getCount()
        if (count == 0) {
            if (contactsRepo.hasContactsPermission()) {
                syncDeviceContacts(force = true)
            } else {
                seedCorporateClients()
            }
        }
    }

    private suspend fun seedCorporateClients() {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L

        val defaultClients = listOf(
            ClientEntity(
                id = "client-1",
                name = "Rajesh Singhania",
                company = "Singhania Infra Corp",
                designation = "Managing Director",
                number = "+91 98765 43210",
                email = "rajesh@singhaniainfra.com",
                dealSize = "$7.0M",
                category = "Commercial Real Estate",
                lastContactedTimestamp = now - 18 * oneDay,
                isInRotation = true,
                isStarred = true
            ),
            ClientEntity(
                id = "client-2",
                name = "Priya Patel",
                company = "Lodha Strategic Holdings",
                designation = "VP Acquisitions",
                number = "+91 98111 22334",
                email = "p.patel@lodhash.com",
                dealSize = "$4.2M",
                category = "Key Client",
                lastContactedTimestamp = now - 12 * oneDay,
                isInRotation = true,
                isStarred = true
            ),
            ClientEntity(
                id = "client-3",
                name = "Vikram Oberoi",
                company = "Oberoi Hospitality & Assets",
                designation = "Principal Partner",
                number = "+91 98222 33445",
                email = "vikram@oberoiassets.com",
                dealSize = "$3.5M",
                category = "Corporate Buyer",
                lastContactedTimestamp = now - 25 * oneDay,
                isInRotation = true,
                isStarred = false
            ),
            ClientEntity(
                id = "client-4",
                name = "Ananya Sen",
                company = "Crestview Private Equity",
                designation = "Chief Investment Officer",
                number = "+91 98333 44556",
                email = "ananya.sen@crestviewcap.com",
                dealSize = "$5.8M",
                category = "Investor",
                lastContactedTimestamp = now - 9 * oneDay,
                isInRotation = true,
                isStarred = true
            )
        )

        clientDao.insertAll(defaultClients)
    }

    fun getVoiceNotesForClient(clientId: String): Flow<List<VoiceNoteEntity>> {
        return voiceNoteDao.getVoiceNotesForClient(clientId)
    }

    suspend fun getLatestVoiceNote(clientId: String): VoiceNoteEntity? = withContext(Dispatchers.IO) {
        voiceNoteDao.getLatestVoiceNoteForClient(clientId)
    }

    suspend fun setClientRotation(clientId: String, isInRotation: Boolean) = withContext(Dispatchers.IO) {
        clientDao.setRotationStatus(clientId, isInRotation)
    }

    suspend fun setAllClientsRotation(isInRotation: Boolean) = withContext(Dispatchers.IO) {
        clientDao.setAllRotationStatus(isInRotation)
    }

    suspend fun updateLastContacted(clientId: String, timestamp: Long = System.currentTimeMillis()) = withContext(Dispatchers.IO) {
        clientDao.updateLastContacted(clientId, timestamp)
    }

    suspend fun snoozeClient(clientId: String, days: Int) = withContext(Dispatchers.IO) {
        val snoozeUntil = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
        clientDao.setSnooze(clientId, snoozeUntil)
    }

    suspend fun saveVoiceNote(
        clientId: String,
        audioFilePath: String,
        durationSeconds: Int,
        summary: String
    ) = withContext(Dispatchers.IO) {
        saveVoiceNoteAndReturn(clientId, audioFilePath, durationSeconds, summary)
    }

    suspend fun saveVoiceNoteAndReturn(
        clientId: String,
        audioFilePath: String,
        durationSeconds: Int,
        summary: String
    ): VoiceNoteEntity = withContext(Dispatchers.IO) {
        // Cap at max 5 notes: delete the oldest if we already have >= 5
        val snapshot = voiceNoteDao.getVoiceNotesSnapshot(clientId)
        if (snapshot.size >= 5) {
            val oldest = snapshot.lastOrNull()
            if (oldest != null) {
                deleteVoiceNote(oldest.id)
            }
        }

        val note = VoiceNoteEntity(
            id = UUID.randomUUID().toString(),
            clientId = clientId,
            audioFilePath = audioFilePath,
            durationSeconds = durationSeconds,
            summary = summary.ifBlank { "Client voice memory #${snapshot.size + 1}" },
            recordedAt = System.currentTimeMillis()
        )
        voiceNoteDao.insert(note)
        note
    }

    suspend fun syncDiskVoiceNotesForClient(clientId: String) = withContext(Dispatchers.IO) {
        try {
            val client = clientDao.getClientById(clientId) ?: return@withContext
            val cleanNumber = client.number.replace(Regex("[^0-9]"), "").let {
                if (it.length > 10) it.takeLast(10) else it
            }
            if (cleanNumber.isBlank()) return@withContext

            val recordingsDir = com.example.util.VoiceAudioManager.getAppRecordingsDirectory(context)
            val files = recordingsDir.listFiles { _, name ->
                (name.startsWith("$cleanNumber-") || name.startsWith("rec_${cleanNumber}_") || name.contains(cleanNumber)) && name.endsWith(".m4a")
            } ?: return@withContext

            // 1. Clean up stale DB records whose file on disk no longer exists
            val existingNotes = voiceNoteDao.getVoiceNotesSnapshot(clientId)
            for (note in existingNotes) {
                if (note.audioFilePath.isNotBlank() && !note.audioFilePath.startsWith("seed_")) {
                    val f = java.io.File(note.audioFilePath)
                    if (!f.exists() || f.length() == 0L) {
                        voiceNoteDao.deleteById(note.id)
                    }
                }
            }

            // 2. Fetch updated notes snapshot
            val validNotes = voiceNoteDao.getVoiceNotesSnapshot(clientId).toMutableList()
            val existingPaths = validNotes.map { it.audioFilePath }.toSet()

            // 3. For any file on disk not yet in DB, only register if total notes < 5
            for (file in files.sortedByDescending { it.lastModified() }) {
                if (!existingPaths.contains(file.absolutePath) && file.length() > 0L) {
                    if (validNotes.size < 5) {
                        val note = VoiceNoteEntity(
                            id = "disk-${file.nameWithoutExtension}",
                            clientId = clientId,
                            audioFilePath = file.absolutePath,
                            durationSeconds = 12,
                            summary = "Client follow-up memory",
                            recordedAt = file.lastModified()
                        )
                        voiceNoteDao.insert(note)
                        validNotes.add(note)
                    }
                }
            }
        } catch (e: Exception) {
            // Non-blocking disk sync
        }
    }

    suspend fun deleteVoiceNote(id: String) = withContext(Dispatchers.IO) {
        try {
            val note = voiceNoteDao.getById(id)
            if (note != null && note.audioFilePath.isNotBlank()) {
                val file = java.io.File(note.audioFilePath)
                if (file.exists()) {
                    file.delete()
                }
                // Also delete any duplicate DB records that reference this same audio file
                voiceNoteDao.deleteByAudioFilePath(note.audioFilePath)
            }
        } catch (e: Exception) {
            // Ignore file deletion error
        }
        voiceNoteDao.deleteById(id)
    }

    suspend fun deleteVoiceNoteByFilePath(filePath: String) = withContext(Dispatchers.IO) {
        voiceNoteDao.deleteByAudioFilePath(filePath)
    }

    private val prefs = context.getSharedPreferences("b2b_client_prefs", Context.MODE_PRIVATE)
    private val contactsRepo = ContactsRepository(context)

    companion object {
        private const val KEY_LAST_SYNC_TIME = "last_contacts_sync_timestamp"
        private const val KEY_TRIAL_START_TIME = "free_trial_start_timestamp"
        private const val KEY_ACTIVE_PLAN_NAME = "active_subscription_plan_name"
        private const val KEY_ACTIVE_PLAN_PRICE = "active_subscription_plan_price"
        private const val KEY_LAST_TX_ID = "last_payu_tx_id"
        private const val KEY_CARD_ANIMATION_STYLE = "selected_card_animation_style"
        private const val KEY_PREFERRED_WHATSAPP_PACKAGE = "preferred_whatsapp_package"
        private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding_flow"
        private const val KEY_CALLS_TODAY_DATE = "calls_today_calendar_date"
        private const val KEY_CALLS_TODAY_COUNT = "calls_today_count"
        private const val KEY_DAILY_CALL_GOAL = "daily_call_goal"
        const val TEN_MINUTES_MS = 10 * 60 * 1000L
        const val TRIAL_DURATION_DAYS = 60
    }

    fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)
    }

    fun setCompletedOnboarding(completed: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_COMPLETED_ONBOARDING, completed).apply()
    }

    private fun getTodayDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    fun getCallsMadeToday(): Int {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_CALLS_TODAY_DATE, "")
        return if (savedDate == today) {
            prefs.getInt(KEY_CALLS_TODAY_COUNT, 0)
        } else {
            prefs.edit().putString(KEY_CALLS_TODAY_DATE, today).putInt(KEY_CALLS_TODAY_COUNT, 0).apply()
            0
        }
    }

    fun incrementCallsMadeToday(): Int {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_CALLS_TODAY_DATE, "")
        val current = if (savedDate == today) prefs.getInt(KEY_CALLS_TODAY_COUNT, 0) else 0
        val next = current + 1
        prefs.edit()
            .putString(KEY_CALLS_TODAY_DATE, today)
            .putInt(KEY_CALLS_TODAY_COUNT, next)
            .apply()
        return next
    }

    fun getDailyCallGoal(): Int {
        return prefs.getInt(KEY_DAILY_CALL_GOAL, 8)
    }

    fun setDailyCallGoal(goal: Int) {
        prefs.edit().putInt(KEY_DAILY_CALL_GOAL, goal).apply()
    }

    fun getUserProfileData(): UserProfileData {
        return UserProfileData(
            dailyCallGoal = getDailyCallGoal(),
            callsMadeToday = getCallsMadeToday()
        )
    }

    fun shouldAutoSyncOnAppOpen(): Boolean {
        val lastSync = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
        if (lastSync == 0L) return true
        val elapsed = System.currentTimeMillis() - lastSync
        return elapsed >= TEN_MINUTES_MS
    }

    fun getLastSyncTimestamp(): Long {
        return prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
    }

    fun getCardAnimationStyle(): com.example.model.CardAnimationStyle {
        val name = prefs.getString(KEY_CARD_ANIMATION_STYLE, com.example.model.CardAnimationStyle.LIQUID_GLASS_STACK.name)
        return try {
            com.example.model.CardAnimationStyle.valueOf(name ?: com.example.model.CardAnimationStyle.LIQUID_GLASS_STACK.name)
        } catch (e: Exception) {
            com.example.model.CardAnimationStyle.LIQUID_GLASS_STACK
        }
    }

    fun setCardAnimationStyle(style: com.example.model.CardAnimationStyle) {
        prefs.edit().putString(KEY_CARD_ANIMATION_STYLE, style.name).apply()
    }

    fun getPreferredWhatsAppPackage(): String? {
        return prefs.getString(KEY_PREFERRED_WHATSAPP_PACKAGE, null)
    }

    fun setPreferredWhatsAppPackage(pkg: String?) {
        if (pkg == null) {
            prefs.edit().remove(KEY_PREFERRED_WHATSAPP_PACKAGE).apply()
        } else {
            prefs.edit().putString(KEY_PREFERRED_WHATSAPP_PACKAGE, pkg).apply()
        }
    }

    suspend fun addNewContact(
        name: String,
        number: String,
        company: String,
        designation: String,
        dealSize: String = "$1.0M"
    ) = withContext(Dispatchers.IO) {
        val client = ClientEntity(
            id = "manual-${UUID.randomUUID()}",
            name = name.trim(),
            company = company.ifBlank { "Corporate Partner" },
            designation = designation.ifBlank { "Direct Contact" },
            number = number.trim(),
            email = "",
            dealSize = dealSize.ifBlank { "$1.0M" },
            category = "VIP Network",
            lastContactedTimestamp = 0L,
            isInRotation = true,
            isStarred = true,
            snoozeUntilTimestamp = 0L
        )
        clientDao.insert(client)
    }

    suspend fun syncDeviceContacts(
        force: Boolean = false,
        onProgress: suspend (Int, String) -> Unit = { _, _ -> }
    ): Boolean = withContext(Dispatchers.IO) {
        val lastSync = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
        val now = System.currentTimeMillis()
        val count = clientDao.getCount()

        // 10-Minute Cache Rule: If not forced and already synced in the last 10 minutes, skip to preserve performance
        if (!force && count > 0 && (now - lastSync < TEN_MINUTES_MS)) {
            onProgress(100, "Contacts already up to date")
            return@withContext false
        }

        if (!contactsRepo.hasContactsPermission()) {
            onProgress(0, "Permission required")
            return@withContext false
        }

        onProgress(15, "Connecting to device phonebook...")
        val deviceContacts = contactsRepo.getContacts()
        if (deviceContacts.isEmpty()) {
            onProgress(100, "No contacts found")
            return@withContext false
        }

        onProgress(35, "Reading ${deviceContacts.size} contacts...")

        // Remove placeholder seed clients if real contacts are found
        val hasOnlySeeds = clientDao.getAllNumbers().any { it.startsWith("+91 98765") || it.startsWith("+91 98111") }
        if (hasOnlySeeds && count <= 8) {
            clientDao.removeSeedClients()
        }

        onProgress(60, "Filtering & deduplicating VIP contacts...")
        // Map existing numbers so user tick/untick states are respected
        val existingNumbers = clientDao.getAllNumbers().map { it.replace(Regex("[^0-9+]"), "") }.toSet()

        val newEntities = mutableListOf<ClientEntity>()
        for (contact in deviceContacts) {
            val cleanNumber = contact.number.replace(Regex("[^0-9+]"), "")
            if (cleanNumber.isNotBlank() && !existingNumbers.contains(cleanNumber)) {
                newEntities.add(
                    ClientEntity(
                        id = "device-${contact.id.ifBlank { UUID.randomUUID().toString() }}",
                        name = contact.name,
                        company = "Device Contact",
                        designation = contact.type,
                        number = contact.number,
                        email = "",
                        dealSize = "$1.0M",
                        category = "Device Rolodex",
                        lastContactedTimestamp = 0L,
                        isInRotation = true,
                        isStarred = contact.isStarred,
                        photoUri = contact.photoUri,
                        snoozeUntilTimestamp = 0L
                    )
                )
            }
        }

        onProgress(80, "Indexing high-speed database...")
        // Fast chunked insert for large rolodex (6,000+ contacts)
        if (newEntities.isNotEmpty()) {
            newEntities.chunked(400).forEach { chunk ->
                clientDao.insertAll(chunk)
            }
        }

        prefs.edit().putLong(KEY_LAST_SYNC_TIME, now).apply()
        // Synchronize call history touchpoints with device call log
        syncCallHistoryTouchpointsInternal()

        onProgress(100, "Sync complete! ${deviceContacts.size} contacts active")
        return@withContext true
    }

    /**
     * Fast, non-blocking scan of recent call log to update lastContactedTimestamp
     * for all matching clients in the Room database.
     */
    suspend fun syncCallHistoryTouchpoints(): Boolean = withContext(Dispatchers.IO) {
        syncCallHistoryTouchpointsInternal()
    }

    private suspend fun syncCallHistoryTouchpointsInternal(): Boolean {
        if (!com.example.util.CallLogHelper.hasCallLogPermission(context)) return false
        return try {
            val callMap = com.example.util.CallLogHelper.getAllRecentCallsMap(context)
            if (callMap.isEmpty()) return false

            val clients = clientDao.getAllClientsSnapshot()
            for (client in clients) {
                val key = com.example.util.CallLogHelper.normalizeToKey(client.number)
                if (key.length >= 7) {
                    val lastCall = callMap[key]
                    if (lastCall != null && lastCall > client.lastContactedTimestamp) {
                        clientDao.updateLastContactedIfNewer(client.id, lastCall)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getLastSyncTimeString(): String {
        val lastSync = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
        if (lastSync == 0L) return "Never synced"
        val diffMin = ((System.currentTimeMillis() - lastSync) / 60000L).coerceAtLeast(0)
        return when {
            diffMin < 1 -> "Just now"
            diffMin < 60 -> "$diffMin mins ago"
            else -> "${diffMin / 60} hrs ago"
        }
    }

    fun getSubscriptionState(): com.example.model.SubscriptionState {
        val trialStart = prefs.getLong(KEY_TRIAL_START_TIME, 0L).let {
            if (it == 0L) {
                val now = System.currentTimeMillis()
                prefs.edit().putLong(KEY_TRIAL_START_TIME, now).apply()
                now
            } else {
                it
            }
        }

        val daysPassed = ((System.currentTimeMillis() - trialStart) / (24 * 60 * 60 * 1000L)).toInt()
        val daysRemaining = (TRIAL_DURATION_DAYS - daysPassed).coerceAtLeast(0)
        val isTrialActive = daysRemaining > 0

        val planName = prefs.getString(KEY_ACTIVE_PLAN_NAME, null)
        val planPrice = prefs.getInt(KEY_ACTIVE_PLAN_PRICE, 0)
        val lastTxId = prefs.getString(KEY_LAST_TX_ID, null)

        val isSubscribed = planName != null

        return com.example.model.SubscriptionState(
            isSubscribed = isSubscribed,
            activePlanName = planName ?: if (isTrialActive) "Free Trial (60 Days)" else "Trial Expired",
            activePrice = planPrice,
            isTrialActive = isTrialActive,
            trialDaysRemaining = daysRemaining,
            lastTransactionId = lastTxId
        )
    }

    fun activateSubscription(plan: com.example.model.SubscriptionPlan, txId: String) {
        prefs.edit()
            .putString(KEY_ACTIVE_PLAN_NAME, plan.title)
            .putInt(KEY_ACTIVE_PLAN_PRICE, plan.price)
            .putString(KEY_LAST_TX_ID, txId)
            .apply()
    }
}
