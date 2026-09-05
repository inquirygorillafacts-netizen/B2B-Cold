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
        makeDirectCall(context, phoneNumber)
    }

    /**
     * Direct one-tap phone call without opening external dialer application.
     * Uses ACTION_CALL when CALL_PHONE permission is granted.
     */
    fun makeDirectCall(context: Context, phoneNumber: String) {
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
            if (hasCallPermission) {
                val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${Uri.encode(cleanNumber)}")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(callIntent)
            } else {
                // If permission is not granted yet, use ACTION_DIAL fallback
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(cleanNumber)}")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
            }
        } catch (e: Exception) {
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(cleanNumber)}")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
            } catch (fallbackEx: Exception) {
                Toast.makeText(context, "Could not place call: ${fallbackEx.localizedMessage}", Toast.LENGTH_LONG).show()
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

    /**
     * Opens user's preferred WhatsApp application (WhatsApp or WhatsApp Business)
     */
    fun openWhatsApp(
        context: Context,
        phoneNumber: String,
        preferredPackage: String? = null,
        message: String = ""
    ) {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        val normalized = if (cleanNumber.startsWith("+")) cleanNumber.substring(1) else cleanNumber
        try {
            val url = if (message.isNotBlank()) {
                "https://api.whatsapp.com/send?phone=$normalized&text=${Uri.encode(message)}"
            } else {
                "https://api.whatsapp.com/send?phone=$normalized"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                if (!preferredPackage.isNullOrBlank()) {
                    setPackage(preferredPackage)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback without package restriction
                val genericIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$normalized")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(genericIntent)
            } catch (fallbackEx: Exception) {
                Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun formatDaysAgo(timestamp: Long): String {
        if (timestamp <= 0L) return "No prior call"
        val now = System.currentTimeMillis()
        val diffMs = now - timestamp
        if (diffMs < 0L) return "Today"
        val days = (diffMs / (24 * 60 * 60 * 1000L)).toInt()
        val hours = (diffMs / (60 * 60 * 1000L)).toInt()
        return when {
            hours < 1 -> "Just now"
            days == 0 -> "Today"
            days == 1 -> "Yesterday"
            days < 30 -> "$days days ago"
            days < 365 -> "${days / 30} mo ago"
            else -> "${days / 365} yr ago"
        }
    }

    fun formatDisplayNumber(rawNumber: String): String {
        val trimmed = rawNumber.trim()
        val digits = trimmed.replace(Regex("[^0-9]"), "")
        return when {
            digits.length == 10 -> "+91 ${digits.substring(0, 5)} ${digits.substring(5)}"
            digits.length == 12 && digits.startsWith("91") -> "+91 ${digits.substring(2, 7)} ${digits.substring(7)}"
            digits.length == 11 && digits.startsWith("0") -> "+91 ${digits.substring(1, 6)} ${digits.substring(6)}"
            trimmed.startsWith("+") -> trimmed
            digits.isNotBlank() -> "+91 $digits"
            else -> trimmed
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
