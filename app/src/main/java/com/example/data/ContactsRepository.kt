package com.example.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.example.model.ContactItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository(private val context: Context) {

    fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getContacts(): List<ContactItem> = withContext(Dispatchers.IO) {
        if (!hasContactsPermission()) {
            return@withContext emptyList()
        }

        val contactList = mutableListOf<ContactItem>()
        val seenNumbers = mutableSetOf<String>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.STARRED
        )

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                val starredIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED)

                while (it.moveToNext()) {
                    val id = if (idIndex != -1) it.getString(idIndex) ?: "" else ""
                    val name = if (nameIndex != -1) it.getString(nameIndex) ?: "Unknown" else "Unknown"
                    val number = if (numberIndex != -1) it.getString(numberIndex) ?: "" else ""
                    val cleanNumber = number.replace(Regex("[^0-9+]"), "")

                    if (cleanNumber.isNotBlank() && !seenNumbers.contains(cleanNumber)) {
                        seenNumbers.add(cleanNumber)
                        val typeCode = if (typeIndex != -1) it.getInt(typeIndex) else ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                        val label = when (typeCode) {
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "Mobile"
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "Home"
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "Work"
                            else -> "Other"
                        }
                        val photoUri = if (photoIndex != -1) it.getString(photoIndex) else null
                        val isStarred = if (starredIndex != -1) it.getInt(starredIndex) == 1 else false

                        contactList.add(
                            ContactItem(
                                id = id,
                                name = name,
                                number = number,
                                type = label,
                                photoUri = photoUri,
                                isStarred = isStarred
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If the device has no saved contacts (common in new emulator), provide sample contacts
        if (contactList.isEmpty()) {
            return@withContext getDemoContacts()
        }

        return@withContext contactList
    }

    fun getDemoContacts(): List<ContactItem> {
        return listOf(
            ContactItem("demo-1", "Aarav Sharma", "+91 98765 43210", "Mobile", isStarred = true),
            ContactItem("demo-2", "Amit Verma", "+91 98111 22334", "Work"),
            ContactItem("demo-3", "Ananya Gupta", "+91 98222 33445", "Mobile", isStarred = true),
            ContactItem("demo-4", "Bhavna Patel", "+91 98333 44556", "Home"),
            ContactItem("demo-5", "Customer Support", "1800-111-222", "Toll Free"),
            ContactItem("demo-6", "Deepak Kumar", "+91 98444 55667", "Mobile"),
            ContactItem("demo-7", "Emergency Helpline", "112", "Emergency", isStarred = true),
            ContactItem("demo-8", "Ishaan Roy", "+91 98555 66778", "Work"),
            ContactItem("demo-9", "Kavita Singh", "+91 98666 77889", "Mobile"),
            ContactItem("demo-10", "Manish Joshi", "+91 98777 88990", "Mobile"),
            ContactItem("demo-11", "Pooja Mehta", "+91 98888 99001", "Home"),
            ContactItem("demo-12", "Rahul Dravid", "+91 98999 00112", "Mobile"),
            ContactItem("demo-13", "Rohit Malhotra", "+91 98000 11223", "Work"),
            ContactItem("demo-14", "Sneha Rao", "+91 97111 22334", "Mobile"),
            ContactItem("demo-15", "Vikas Dubey", "+91 97222 33445", "Mobile")
        )
    }
}
