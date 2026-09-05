package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceNoteDao {

    @Query("SELECT * FROM voice_notes WHERE clientId = :clientId ORDER BY recordedAt DESC")
    fun getVoiceNotesForClient(clientId: String): Flow<List<VoiceNoteEntity>>

    @Query("SELECT * FROM voice_notes ORDER BY recordedAt DESC")
    fun getAllVoiceNotes(): Flow<List<VoiceNoteEntity>>

    @Query("SELECT * FROM voice_notes WHERE clientId = :clientId ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestVoiceNoteForClient(clientId: String): VoiceNoteEntity?

    @Query("SELECT * FROM voice_notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): VoiceNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(voiceNote: VoiceNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(voiceNotes: List<VoiceNoteEntity>)

    @Query("DELETE FROM voice_notes WHERE id = :id")
    suspend fun deleteById(id: String)
}
