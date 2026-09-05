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
        val note = VoiceNoteEntity(
            id = UUID.randomUUID().toString(),
            clientId = clientId,
            audioFilePath = audioFilePath,
            durationSeconds = durationSeconds,
            summary = summary.ifBlank { "Recorded executive note on ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US).format(java.util.Date())}" },
            recordedAt = System.currentTimeMillis()
        )
        voiceNoteDao.insert(note)
    }

    suspend fun deleteVoiceNote(id: String) = withContext(Dispatchers.IO) {
        voiceNoteDao.deleteById(id)
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
        const val ONE_HOUR_MS = 60 * 60 * 1000L
        const val TRIAL_DURATION_DAYS = 60
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

        // 1-Hour Cache Rule: If not forced and already synced in the last hour, skip to preserve performance
        if (!force && count > 0 && (now - lastSync < ONE_HOUR_MS)) {
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
        onProgress(100, "Sync complete! ${deviceContacts.size} contacts active")
        return@withContext true
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
