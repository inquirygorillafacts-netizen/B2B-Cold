package com.example.model

data class ContactItem(
    val id: String,
    val name: String,
    val number: String,
    val type: String = "Mobile",
    val photoUri: String? = null,
    val isStarred: Boolean = false
) {
    val initialLetter: Char
        get() = name.trim().firstOrNull()?.uppercaseChar() ?: '#'
}
