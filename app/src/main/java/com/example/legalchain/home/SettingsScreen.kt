@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.legalchain.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkGreen = Color(0xFF004D40)

@Composable
fun SettingsScreen(
    onEmailNotificationsToggle: (Boolean) -> Unit = {},
    onAppNotificationsToggle: (Boolean) -> Unit = {},
    onLogoutConfirmed: () -> Unit,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var emailNotifications by remember { mutableStateOf(true) }
    var appNotifications by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        }
    ) { padding ->

        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .shadow(6.dp, RoundedCornerShape(18.dp))
                        .background(DarkGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚖️", fontSize = 44.sp, color = Color.White)
                }
                Spacer(Modifier.height(12.dp))
                Text("LegalChain", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = DarkGreen)
                Spacer(Modifier.height(18.dp))
            }

            item {
                SectionTitle(title = "ACCOUNT")
                SettingsCard {
                    SettingsRow(
                        icon = Icons.Filled.Person,
                        title = "Profile Settings",
                        subtitle = "Manage your profile",
                        onClick = { onNavigate("/profile/edit") }
                    )
                    HorizontalDivider(color = colors.surfaceVariant)
                    SettingsRow(
                        icon = Icons.Filled.Security,
                        title = "Security",
                        subtitle = "Password & authentication",
                        onClick = { onNavigate("/settings/security") }
                    )
                    HorizontalDivider(color = colors.surfaceVariant)
                    SettingsRow(
                        icon = Icons.Filled.Info,
                        title = "Account Activity",
                        subtitle = "Recent sign-ins & sessions",
                        onClick = { onNavigate("/settings/activity") }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                SectionTitle(title = "PREFERENCES")
                SettingsCard {
                    SettingsRow(
                        icon = Icons.Filled.Notifications,
                        title = "Notifications",
                        subtitle = "Manage push & in-app alerts",
                        trailing = {
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
                        },
                        onClick = { onNavigate("/settings/notifications") }
                    )
                    HorizontalDivider(color = colors.surfaceVariant)
                    ToggleRow(
                        icon = Icons.Filled.Email,
                        title = "Email Notifications",
                        subtitle = "Receive updates by email",
                        checked = emailNotifications,
                        onCheckedChange = {
                            emailNotifications = it
                            onEmailNotificationsToggle(it)
                        }
                    )
                    HorizontalDivider(color = colors.surfaceVariant)
                    ToggleRow(
                        icon = Icons.Filled.Notifications,
                        title = "App Notifications",
                        subtitle = "Enable in-app alerts",
                        checked = appNotifications,
                        onCheckedChange = {
                            appNotifications = it
                            onAppNotificationsToggle(it)
                        }
                    )
                    HorizontalDivider(color = colors.surfaceVariant)
                    // Language row removed as requested
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                SectionTitle(title = "SUPPORT")
                SettingsCard {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = "Help & Support",
                        subtitle = "FAQ and assistance",
                        trailing = {
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
                        },
                        onClick = { onNavigate("/profile/help") }
                    )
                    HorizontalDivider(color = colors.surfaceVariant)
                    SettingsRow(
                        icon = Icons.Filled.Info,
                        title = "Privacy Policy",
                        subtitle = "Read our policy",
                        trailing = {
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
                        },
                        onClick = { onNavigate("/settings/privacy") }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Log Out", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Red)
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            item {
                Text("LegalChain v1.0.0", fontSize = 14.sp, color = Color.Gray)
                Spacer(Modifier.height(28.dp))
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGreen) },
            text = { Text("Are you sure you want to log out?", fontSize = 15.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogoutConfirmed()
                }) {
                    Text("Confirm", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = DarkGreen)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

/* ---------- Small reusable components ---------- */

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(content = content)
    }
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkGreen,
                checkedTrackColor = DarkGreen.copy(alpha = 0.38f)
            )
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            subtitle?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }

        trailing?.invoke()
    }
}
