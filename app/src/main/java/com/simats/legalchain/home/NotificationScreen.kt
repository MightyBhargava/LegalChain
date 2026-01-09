@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.simats.legalchain.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ---------- Design tokens ----------
private val DarkGreen = Color(0xFF004D40)
private val PageBg = Color(0xFFF6F7F6)

// -----------------------------------------------------------
// Notification types (icons + soft bg)
sealed class NotificationType(
    val icon: ImageVector,
    val bg: Color,
    val tint: Color
) {
    object Hearing : NotificationType(Icons.Filled.Event, Color(0xFFDCEAFE), Color(0xFF1D4ED8))
    object Document : NotificationType(Icons.Filled.Description, Color(0xFFEDE9FE), Color(0xFF6D28D9))
    object Alert : NotificationType(Icons.Filled.Warning, Color(0xFFFFE4E6), Color(0xFFDC2626))
    object Message : NotificationType(Icons.AutoMirrored.Filled.Message, Color(0xFFE0F2FE), Color(0xFF0369A1))
    object Update : NotificationType(Icons.Filled.Gavel, Color(0xFFFFF7E6), Color(0xFFB45309))
}

// Notification data model
data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val description: String,
    val time: String,
    val read: Boolean
)

// -----------------------------------------------------------
// NotificationScreen (Option A + A3 title style)
// -----------------------------------------------------------
@Composable
fun NotificationScreen(
    onBack: () -> Unit = {},
    onOpenRoute: (String) -> Unit = {}
) {
    // sample data; swap for your ViewModel/repo later
    val notifications = remember {
        mutableStateListOf(
            NotificationItem("1", NotificationType.Hearing, "Hearing Reminder",
                "Singh vs. State hearing tomorrow at 10:30 AM", "2 hours ago", false),
            NotificationItem("2", NotificationType.Document, "Document Uploaded",
                "New evidence document added to Sharma Property case", "5 hours ago", false),
            NotificationItem("3", NotificationType.Alert, "Deadline Approaching",
                "Response filing deadline in 3 days for TechCorp case", "Yesterday", true),
            NotificationItem("4", NotificationType.Message, "New Message",
                "Client Mr. Sharma sent you a message", "Yesterday", true),
            NotificationItem("5", NotificationType.Update, "Case Status Updated",
                "Kumar Divorce case changed to Pending Judgment", "2 days ago", true),
            NotificationItem("6", NotificationType.Hearing, "Hearing Scheduled",
                "New hearing set for Patel vs. Patel", "3 days ago", true)
        )
    }

    // helpers
    fun toggleRead(id: String) {
        val idx = notifications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val old = notifications[idx]
            notifications[idx] = old.copy(read = !old.read)
        }
    }

    fun markAllRead() {
        for (i in notifications.indices) {
            val old = notifications[i]
            if (!old.read) notifications[i] = old.copy(read = true)
        }
    }

    val unreadCount by derivedStateOf { notifications.count { !it.read } }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val selected = remember { mutableStateOf<NotificationItem?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = PageBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Notifications",
                        fontSize = 22.sp,                   // Option A heading (professional)
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBg)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // header: unread count + mark all read
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp,
                color = colors.surface
            ) {
                Row(
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$unreadCount unread",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )

                    TextButton(
                        onClick = {
                            markAllRead()
                            coroutineScope.launch { snackbarHostState.showSnackbar("All marked read") }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = DarkGreen)
                    ) {
                        Text("Mark all read", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(notifications) { _, item ->
                    NotificationCard(
                        notification = item,
                        onClick = {
                            selected.value = item
                            showDialog.value = true
                        },
                        onToggleRead = {
                            toggleRead(item.id)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    if (notifications.find { it.id == item.id }?.read == true) "Marked read" else "Marked unread"
                                )
                            }
                        },
                        accentColor = DarkGreen
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            val id = (notifications.size + 1).toString()
                            notifications.add(
                                NotificationItem(
                                    id,
                                    NotificationType.Update,
                                    "Older Notification",
                                    "Loaded older notifications...",
                                    "${notifications.size + 1} days ago",
                                    true
                                )
                            )
                            coroutineScope.launch { snackbarHostState.showSnackbar("Loaded more notifications") }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White)
                    ) {
                        Text("Load More Notifications", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // dialog for selected notification
    if (showDialog.value && selected.value != null) {
        val item = selected.value!!
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                selected.value = null
            },
            title = {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.read) colors.onSurface.copy(alpha = 0.75f) else DarkGreen
                )
            },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(item.description, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item.time, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    selected.value = null
                    onOpenRoute("home") // change to case-specific route when needed
                }) {
                    Text("Open", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        toggleRead(item.id)
                        showDialog.value = false
                        selected.value = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                if (notifications.find { it.id == item.id }?.read == true) "Marked read" else "Marked unread"
                            )
                        }
                    }) {
                        val nowRead = notifications.find { it.id == item.id }?.read ?: item.read
                        Text(if (nowRead) "Mark unread" else "Mark read", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = {
                        showDialog.value = false
                        selected.value = null
                    }) {
                        Text("Close", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

// -----------------------------------------------------------
// NotificationCard composable (Option A sizes + A3 title style)
// -----------------------------------------------------------
@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    onToggleRead: () -> Unit,
    accentColor: Color
) {
    val colors = MaterialTheme.colorScheme

    val cardColor = if (notification.read) colors.surfaceVariant else colors.surface
    val titleColor = if (notification.read) colors.onSurface.copy(alpha = 0.75f) else DarkGreen
    val descColor = colors.onSurface.copy(alpha = if (notification.read) 0.7f else 0.9f)
    val titleWeight = FontWeight.Bold // A3: both bold, unread slightly darker via color

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // left accent for unread
            if (!notification.read) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Spacer(modifier = Modifier.width(18.dp))
            }

            // icon box (rounded)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(notification.type.bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    notification.type.icon,
                    contentDescription = null,
                    tint = notification.type.tint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 18.sp,           // Option A title size
                        fontWeight = titleWeight,
                        color = titleColor
                    )

                    if (!notification.read) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.description,
                    fontSize = 15.sp,               // Option A description size
                    fontWeight = FontWeight.Medium,
                    color = descColor,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(notification.time, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = onToggleRead,
                        colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
                    ) {
                        Text(if (notification.read) "Mark unread" else "Mark read", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
