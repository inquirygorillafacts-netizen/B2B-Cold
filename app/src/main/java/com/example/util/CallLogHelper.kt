package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat

object CallLogHelper {

    fun hasCallLogPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Lightweight, non-blocking check for the last call timestamp for a given phone number.
     * Uses normalized phone number (last 10 digits) to match across different country code prefixes.
     */
    fun getLastCallTimestamp(context: Context, rawNumber: String): Long? {
        if (!hasCallLogPermission(context)) return null
        val digitsOnly = rawNumber.replace(Regex("[^0-9]"), "")
        if (digitsOnly.length < 7) return null
        val searchSuffix = if (digitsOnly.length > 10) digitsOnly.takeLast(10) else digitsOnly

        return try {
            val projection = arrayOf(CallLog.Calls.DATE, CallLog.Calls.NUMBER)
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                "${CallLog.Calls.DATE} DESC LIMIT 50"
            )

            var matchedTimestamp: Long? = null
            cursor?.use {
                val dateCol = it.getColumnIndex(CallLog.Calls.DATE)
                val numCol = it.getColumnIndex(CallLog.Calls.NUMBER)

                while (it.moveToNext()) {
                    val logNumber = if (numCol != -1) it.getString(numCol)?.replace(Regex("[^0-9]"), "") ?: "" else ""
                    if (logNumber.endsWith(searchSuffix) || searchSuffix.endsWith(logNumber)) {
                        if (dateCol != -1) {
                            matchedTimestamp = it.getLong(dateCol)
                            break
                        }
                    }
                }
            }
            matchedTimestamp
        } catch (e: Exception) {
            null
        }
    }
}
