package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PermissionState
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary
import com.example.ui.theme.RgbGlassBorder

@Composable
fun ContactsPermissionGateScreen(
    permissionState: PermissionState,
    hasAskedPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun openDeviceSettings(ctx: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", ctx.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        ctx.startActivity(intent)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFF1F5F9),
                        Color(0xFFE2E8F0)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("contacts_permission_gate_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                // ICON BADGE WITH GLASS BORDER
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RgbGlassBorder)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = LuxuryBlue,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mandatory Permissions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = LuxuryTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "B2B Cold requires 4 core Android permissions to operate. If any permission is denied, access to the card deck remains locked.",
                    fontSize = 13.sp,
                    color = LuxuryTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // REASON 1: Contacts Permission
                PermissionCardItem(
                    title = "1. Phonebook Contacts Access",
                    description = "Imports your executive rolodex and creates your randomized 10-card calling deck. Without this, no call cards can be generated.",
                    icon = Icons.Default.Contacts,
                    isGranted = permissionState.hasContacts,
                    accentColor = LuxuryBlue,
                    bgColor = LuxuryBlueContainer
                )

                Spacer(modifier = Modifier.height(10.dp))

                // REASON 2: Call Log Permission
                PermissionCardItem(
                    title = "2. Call Logs & Call History",
                    description = "Reads device call history to detect exactly when you last spoke with each contact (e.g., 'Connected Today', 'Yesterday', '5 days ago') without manual data entry.",
                    icon = Icons.Default.PhoneInTalk,
                    isGranted = permissionState.hasCallLog,
                    accentColor = Color(0xFF0D8267),
                    bgColor = Color(0xFFE6F5F0)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // REASON 3: Voice Note Recording
                PermissionCardItem(
                    title = "3. Voice Memo Audio Recording",
                    description = "Allows recording 5-second voice memos right on the client's card for instant deal recall before and after conversations.",
                    icon = Icons.Default.Mic,
                    isGranted = permissionState.hasRecordAudio,
                    accentColor = Color(0xFF7C3AED),
                    bgColor = Color(0xFFEDE9FE)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // REASON 4: Direct Calling
                PermissionCardItem(
                    title = "4. Direct 1-Tap Calling",
                    description = "Enables instant 1-tap phone dialing directly from the client card without opening external dialers.",
                    icon = Icons.Default.Phone,
                    isGranted = permissionState.hasCallPhone,
                    accentColor = Color(0xFFD97706),
                    bgColor = Color(0xFFFEF3C7)
                )

                // STATUS / REASON WARNING IF PERMISSION DENIED
                if (hasAskedPermission && !permissionState.allGranted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.5.dp, Color(0xFFF87171)),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Access Blocked: Permissions Incomplete",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "All 4 permissions must be enabled to enter the app. Tap the button below to open Device Settings and allow the remaining permissions.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // BOTTOM ACTION SECTION (EXACTLY 1 BUTTON ONLY)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!hasAskedPermission) {
                    // FIRST ATTEMPT: SINGLE BUTTON TO TRIGGER SYSTEM DIALOG
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("grant_contacts_permission_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuxuryBlue),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Grant All Permissions",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                } else {
                    // IF ANY PERMISSION WAS DENIED: EXACTLY 1 ONLY BUTTON TO OPEN SETTINGS
                    Button(
                        onClick = { openDeviceSettings(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("open_device_settings_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Device Settings To Allow All",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (hasAskedPermission) "App unlocks automatically once permissions are granted in Settings" else "100% offline security: your contacts and call logs never leave your phone",
                    fontSize = 11.sp,
                    color = LuxuryTextMuted,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PermissionCardItem(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    accentColor: Color,
    bgColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(
            1.dp,
            if (isGranted) LuxuryEmerald.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) Color(0xFFECFDF5) else bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) LuxuryEmerald else accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = LuxuryTextPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isGranted) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                    ) {
                        Text(
                            text = if (isGranted) "GRANTED" else "REQUIRED",
                            color = if (isGranted) LuxuryEmerald else Color(0xFFDC2626),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = LuxuryTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
