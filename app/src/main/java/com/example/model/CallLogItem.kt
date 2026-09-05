package com.example.model

data class CallLogItem(
    val id: String,
    val cachedName: String?,
    val number: String,
    val type: CallType,
    val timestamp: Long,
    val duration: Long // duration in seconds
) {
    val displayName: String
        get() = if (!cachedName.isNullOrBlank()) cachedName else number
}
