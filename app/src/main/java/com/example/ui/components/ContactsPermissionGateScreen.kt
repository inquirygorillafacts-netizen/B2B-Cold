package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LuxuryBlue
import com.example.ui.theme.LuxuryBlueContainer
import com.example.ui.theme.LuxuryEmerald
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.LuxuryTextPrimary
import com.example.ui.theme.LuxuryTextSecondary
import com.example.ui.theme.RgbGlassBorder

@Composable
fun ContactsPermissionGateScreen(
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
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                // ICON BADGE WITH RGB GLASS BORDER
                Box(
                    modifier = Modifier
                        .size(90.dp)
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
                            imageVector = Icons.Default.Contacts,
                            contentDescription = null,
                            tint = LuxuryBlue,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Contacts Access Required",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = LuxuryTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please review why this permission is mandatory before proceeding.",
                    fontSize = 14.sp,
                    color = LuxuryTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // REASON 1: Rolodex Deck Core Architecture
                PermissionReasonCard(
                    icon = Icons.Default.Contacts,
                    iconTint = LuxuryBlue,
                    iconBg = LuxuryBlueContainer,
                    title = "Why is Contacts Access Mandatory?",
                    description = "This application is designed specifically as an executive calling rotation deck. Without permission to read your contacts, the app cannot generate your call cards, build your swipe queue, or rotate contacts."
                )

                Spacer(modifier = Modifier.height(14.dp))

                // REASON 2: 100% Local Privacy Guarantee
                PermissionReasonCard(
                    icon = Icons.Default.Security,
                    iconTint = LuxuryEmerald,
                    iconBg = Color(0xFFECFDF5),
                    title = "100% Offline & Private Security",
                    description = "Your contacts never leave your device. We do not upload, share, or sync your phonebook with any external server. All intelligence is stored entirely in your local SQLite Room database."
                )

                Spacer(modifier = Modifier.height(14.dp))

                // REASON 3: Snooze & 3-Day Reschedule Feature
                PermissionReasonCard(
                    icon = Icons.Default.Timer,
                    iconTint = Color(0xFFD97706),
                    iconBg = Color(0xFFFEF3C7),
                    title = "Smart Follow-Up Scheduling",
                    description = "Enables you to reschedule any client for 3 days later, 1 week later, or custom intervals with automatic recurrence and touchpoint tracking."
                )

                // WARNING CARD IF PREVIOUSLY DENIED
                if (hasAskedPermission) {
                    Spacer(modifier = Modifier.height(18.dp))
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
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Permission Denied by System",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "You cannot enter the application without granting Contacts permission. If Android disabled the popup, tap 'Open Device Settings' below to enable Contacts permission manually.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFB91C1C),
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            // BOTTOM ACTION BUTTONS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PRIMARY: Grant Permission Button
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
                    Icon(imageVector = Icons.Default.Contacts, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Grant Contacts Access",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // SHORTCUT: Open Device Settings Button
                if (hasAskedPermission) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { openDeviceSettings(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("open_device_settings_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, LuxuryBlue),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = LuxuryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Device Settings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "App entry remains locked until Contacts permission is enabled.",
                    fontSize = 11.sp,
                    color = LuxuryTextMuted,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PermissionReasonCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = LuxuryTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = LuxuryTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
