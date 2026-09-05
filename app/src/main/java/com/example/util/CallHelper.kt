package com.example.util

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.format.DateUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CallHelper {

    fun makeCall(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.trim()
        if (cleanNumber.isBlank()) {
            Toast.makeText(context, "Number cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        try {
            val intent = if (hasCallPermission) {
                Intent(Intent.ACTION_CALL, Uri.parse("tel:${Uri.encode(cleanNumber)}"))
            } else {
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(cleanNumber)}"))
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback to dial intent
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(cleanNumber)}"))
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(dialIntent)
            } catch (fallbackEx: Exception) {
                Toast.makeText(context, "Could not open dialer: ${fallbackEx.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun sendSms(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${Uri.encode(phoneNumber.trim())}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open messaging: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String, message: String = "") {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        val normalized = if (cleanNumber.startsWith("+")) cleanNumber.substring(1) else cleanNumber
        try {
            val url = if (message.isNotBlank()) {
                "https://api.whatsapp.com/send?phone=$normalized&text=${Uri.encode(message)}"
            } else {
                "https://api.whatsapp.com/send?phone=$normalized"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback to generic action send
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    setPackage("com.whatsapp")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(sendIntent)
            } catch (fallbackEx: Exception) {
                Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun formatDaysAgo(timestamp: Long): String {
        if (timestamp <= 0L) return "Never contacted yet"
        val now = System.currentTimeMillis()
        val diffMs = now - timestamp
        if (diffMs < 0L) return "Today"
        val days = (diffMs / (24 * 60 * 60 * 1000L)).toInt()
        return when {
            days == 0 -> "Contacted today"
            days == 1 -> "1 day ago"
            days < 30 -> "$days days ago"
            days < 365 -> "${days / 30} months ago"
            else -> "${days / 365} years ago"
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Phone Number") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to copy", Toast.LENGTH_SHORT).show()
        }
    }

    fun formatCallDate(timestamp: Long): String {
        if (timestamp <= 0L) return ""
        val now = System.currentTimeMillis()
        val isToday = DateUtils.isToday(timestamp)
        val isYesterday = DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date(timestamp))

        return when {
            isToday -> "Today, $timeStr"
            isYesterday -> "Yesterday, $timeStr"
            now - timestamp < DateUtils.DAY_IN_MILLIS * 7 -> {
                val dayFormat = SimpleDateFormat("EEE, hh:mm a", Locale.getDefault())
                dayFormat.format(Date(timestamp))
            }
            else -> {
                val fullFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                fullFormat.format(Date(timestamp))
            }
        }
    }

    fun formatDuration(seconds: Long): String {
        if (seconds <= 0L) return "0s"
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m ${secs}s"
            minutes > 0 -> "${minutes}m ${secs}s"
            else -> "${secs}s"
        }
    }
}
