package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val company: String,
    val designation: String,
    val number: String,
    val email: String,
    val dealSize: String,
    val category: String,
    val lastContactedTimestamp: Long,
    val isInRotation: Boolean = true,
    val isStarred: Boolean = false,
    val photoUri: String? = null,
    val snoozeUntilTimestamp: Long = 0L
)
