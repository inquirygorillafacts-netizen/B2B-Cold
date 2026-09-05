package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.concurrent.ConcurrentHashMap

object CallLogHelper {

    private val recentCallsCache = ConcurrentHashMap<String, Long>()
    @Volatile
    private var lastCacheTimestamp = 0L

    fun hasCallLogPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Normalizes a phone number to standard last-10 digits for matching.
     * E.g. "+91 70730 77195" -> "7073077195"
     */
    fun normalizeToKey(rawNumber: String): String {
        val digits = rawNumber.replace(Regex("[^0-9]"), "")
        return if (digits.length > 10) digits.takeLast(10) else digits
    }

    /**
     * Refreshes the recent call logs cache.
     * Safe query with NO "LIMIT" in sortOrder string to prevent Android ContentProvider exceptions.
     */
    fun refreshRecentCalls(context: Context) {
        if (!hasCallLogPermission(context)) return
        val now = System.currentTimeMillis()
        if (now - lastCacheTimestamp < 3000L && recentCallsCache.isNotEmpty()) {
            return
        }

        try {
            val projection = arrayOf(
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE
            )
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )

            cursor?.use {
                val dateCol = it.getColumnIndex(CallLog.Calls.DATE)
                val numCol = it.getColumnIndex(CallLog.Calls.NUMBER)
                var count = 0

                while (it.moveToNext() && count < 2000) {
                    count++
                    val rawNumber = if (numCol != -1) it.getString(numCol) ?: "" else ""
                    val key = normalizeToKey(rawNumber)
                    if (key.length >= 6) {
                        val date = if (dateCol != -1) it.getLong(dateCol) else 0L
                        if (date > 0L) {
                            val current = recentCallsCache[key]
                            if (current == null || date > current) {
                                recentCallsCache[key] = date
                            }
                        }
                    }
                }
                lastCacheTimestamp = now
            }
        } catch (e: Exception) {
            Log.e("CallLogHelper", "Failed to refresh call logs: ${e.message}")
        }
    }

    /**
     * Fast batch lookup of the latest call timestamps for all recent callers.
     */
    fun getAllRecentCallsMap(context: Context): Map<String, Long> {
        refreshRecentCalls(context)
        return HashMap(recentCallsCache)
    }

    /**
     * Deep, precise check for a specific number's last call timestamp.
     * Checks in-memory cache first for instant 0ms latency, then scans CallLog.
     */
    fun getLastCallTimestamp(context: Context, rawNumber: String): Long? {
        if (!hasCallLogPermission(context)) return null
        val targetKey = normalizeToKey(rawNumber)
        if (targetKey.length < 6) return null

        refreshRecentCalls(context)

        // Check cache with exact key
        val cached = recentCallsCache[targetKey]
        if (cached != null && cached > 0L) {
            return cached
        }

        // Check suffix matching in cache
        for ((key, date) in recentCallsCache) {
            if (key == targetKey ||
                (key.length >= 7 && targetKey.length >= 7 && (key.endsWith(targetKey) || targetKey.endsWith(key)))
            ) {
                recentCallsCache[targetKey] = date
                return date
            }
        }

        // Deep cursor scan if not found in top cache
        return try {
            val projection = arrayOf(CallLog.Calls.DATE, CallLog.Calls.NUMBER)
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )

            var matched: Long? = null
            cursor?.use {
                val dateCol = it.getColumnIndex(CallLog.Calls.DATE)
                val numCol = it.getColumnIndex(CallLog.Calls.NUMBER)
                var count = 0

                while (it.moveToNext() && count < 3500) {
                    count++
                    val rowNum = if (numCol != -1) it.getString(numCol) ?: "" else ""
                    val rowKey = normalizeToKey(rowNum)
                    if (rowKey.length >= 6) {
                        val isMatch = rowKey == targetKey ||
                                (rowKey.length >= 7 && targetKey.length >= 7 &&
                                        (rowKey.endsWith(targetKey) || targetKey.endsWith(rowKey)))
                        if (isMatch) {
                            if (dateCol != -1) {
                                val date = it.getLong(dateCol)
                                if (date > 0L) {
                                    recentCallsCache[targetKey] = date
                                    matched = date
                                    break
                                }
                            }
                        }
                    }
                }
            }
            matched
        } catch (e: Exception) {
            Log.e("CallLogHelper", "Error searching call log for $rawNumber: ${e.message}")
            null
        }
    }
}
