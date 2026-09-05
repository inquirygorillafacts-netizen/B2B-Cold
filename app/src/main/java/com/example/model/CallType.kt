package com.example.model

enum class CallType(val displayName: String) {
    INCOMING("Incoming"),
    OUTGOING("Outgoing"),
    MISSED("Missed"),
    REJECTED("Rejected"),
    UNKNOWN("Unknown")
}
