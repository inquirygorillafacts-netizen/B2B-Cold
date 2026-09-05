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
    val activePlan: String = "Pro Networker ($19/mo)",
    val pipelineValue: String = "$18.4M"
)

class ClientRelationshipRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val clientDao = db.clientDao()
    private val voiceNoteDao = db.voiceNoteDao()

    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    val rotationClients: Flow<List<ClientEntity>> = clientDao.getRotationClients()

    suspend fun ensureInitialData() = withContext(Dispatchers.IO) {
        val count = clientDao.getCount()
        if (count == 0) {
            seedCorporateClients()
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
            ),
            ClientEntity(
                id = "client-5",
                name = "Karan Mehra",
                company = "TechCorp Solutions",
                designation = "Head of Procurement",
                number = "+91 98444 55667",
                email = "k.mehra@techcorpglobal.com",
                dealSize = "$1.8M",
                category = "Enterprise Retainer",
                lastContactedTimestamp = now - 32 * oneDay,
                isInRotation = true,
                isStarred = false
            ),
            ClientEntity(
                id = "client-6",
                name = "Sunil Varma",
                company = "Varma Global Logistics",
                designation = "Chief Executive Officer",
                number = "+91 98555 66778",
                email = "sunil@varmalogistics.com",
                dealSize = "$6.4M",
                category = "Supply Chain Partner",
                lastContactedTimestamp = now - 14 * oneDay,
                isInRotation = true,
                isStarred = false
            ),
            ClientEntity(
                id = "client-7",
                name = "Ritu Deshmukh",
                company = "Horizon Urban Developers",
                designation = "Director of Assets",
                number = "+91 98666 77889",
                email = "ritu.d@horizondev.in",
                dealSize = "$8.2M",
                category = "Key Client",
                lastContactedTimestamp = now - 40 * oneDay,
                isInRotation = true,
                isStarred = true
            ),
            ClientEntity(
                id = "client-8",
                name = "Aditya Goel",
                company = "Goel Steel & Fabrication",
                designation = "Founder & MD",
                number = "+91 98777 88990",
                email = "aditya@goelsteel.com",
                dealSize = "$2.9M",
                category = "Industrial Client",
                lastContactedTimestamp = now - 21 * oneDay,
                isInRotation = true,
                isStarred = false
            )
        )

        clientDao.insertAll(defaultClients)

        // Seed initial Voice Notes for these clients with high-context business intelligence
        val defaultNotes = listOf(
            VoiceNoteEntity(
                id = "vn-seed-1",
                clientId = "client-1",
                audioFilePath = "",
                durationSeconds = 18,
                summary = "Client budget was tight last quarter; planning $7M commercial space acquisition in March. Follow up on downtown lease terms.",
                recordedAt = now - 18 * oneDay
            ),
            VoiceNoteEntity(
                id = "vn-seed-2",
                clientId = "client-2",
                audioFilePath = "",
                durationSeconds = 14,
                summary = "Requested updated proposal for 15,000 sq ft tech park floor. Board approval scheduled for next Thursday.",
                recordedAt = now - 12 * oneDay
            ),
            VoiceNoteEntity(
                id = "vn-seed-3",
                clientId = "client-3",
                audioFilePath = "",
                durationSeconds = 22,
                summary = "Exploring renewal of B2B retainer agreement. Emphasized dedicated account executive and priority SLA.",
                recordedAt = now - 25 * oneDay
            ),
            VoiceNoteEntity(
                id = "vn-seed-4",
                clientId = "client-4",
                audioFilePath = "",
                durationSeconds = 16,
                summary = "Positive on residential REIT portfolio yields. Needs final due diligence report before committing $5.8M.",
                recordedAt = now - 9 * oneDay
            ),
            VoiceNoteEntity(
                id = "vn-seed-5",
                clientId = "client-5",
                audioFilePath = "",
                durationSeconds = 12,
                summary = "Reviewing Q2 software licensing and agency scope. Wants pricing structured quarterly instead of upfront.",
                recordedAt = now - 32 * oneDay
            ),
            VoiceNoteEntity(
                id = "vn-seed-6",
                clientId = "client-6",
                audioFilePath = "",
                durationSeconds = 19,
                summary = "Planning warehouse expansion across 3 regional hubs. Asked for comparative commercial rental rates.",
                recordedAt = now - 14 * oneDay
            ),
            VoiceNoteEntity(
                id = "vn-seed-7",
                clientId = "client-7",
                audioFilePath = "",
                durationSeconds = 24,
                summary = "Awaiting final municipal clearance on sector 62 plot. Ready to sign joint-venture agreement upon approval.",
                recordedAt = now - 40 * oneDay
            ),
            VoiceNoteEntity(
                id = "vn-seed-8",
                clientId = "client-8",
                audioFilePath = "",
                durationSeconds = 15,
                summary = "Met at national steel expo. Expressed interest in new industrial logistics hub land parcel.",
                recordedAt = now - 21 * oneDay
            )
        )

        voiceNoteDao.insertAll(defaultNotes)
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
            summary = summary.ifBlank { "Recorded voice note on ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US).format(java.util.Date())}" },
            recordedAt = System.currentTimeMillis()
        )
        voiceNoteDao.insert(note)
    }

    suspend fun importDeviceContacts(deviceContacts: List<com.example.model.ContactItem>) = withContext(Dispatchers.IO) {
        val entities = deviceContacts.map { contact ->
            ClientEntity(
                id = "device-${contact.id.ifBlank { UUID.randomUUID().toString() }}",
                name = contact.name,
                company = "Corporate Contact",
                designation = contact.type,
                number = contact.number,
                email = "",
                dealSize = "$1.0M",
                category = "Professional Network",
                lastContactedTimestamp = 0L,
                isInRotation = true,
                isStarred = contact.isStarred,
                photoUri = contact.photoUri
            )
        }
        clientDao.insertAll(entities)
    }
}
