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

    @Query("SELECT * FROM voice_notes WHERE clientId = :clientId ORDER BY recordedAt DESC")
    suspend fun getVoiceNotesSnapshot(clientId: String): List<VoiceNoteEntity>

    @Query("SELECT * FROM voice_notes WHERE audioFilePath = :filePath")
    suspend fun getByAudioFilePath(filePath: String): List<VoiceNoteEntity>

    @Query("DELETE FROM voice_notes WHERE audioFilePath = :filePath")
    suspend fun deleteByAudioFilePath(filePath: String)

    @Query("SELECT COUNT(*) FROM voice_notes WHERE clientId = :clientId")
    suspend fun getCountForClient(clientId: String): Int
}
