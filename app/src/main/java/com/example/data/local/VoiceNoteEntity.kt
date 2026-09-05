package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_notes")
data class VoiceNoteEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val audioFilePath: String,
    val durationSeconds: Int,
    val summary: String,
    val recordedAt: Long = System.currentTimeMillis()
)
