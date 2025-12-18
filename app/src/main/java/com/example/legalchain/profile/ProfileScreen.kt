package com.example.legalchain.profile

/* ---------- Compose Foundation ---------- */
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

/* ---------- Icons ---------- */
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout

/* ---------- Material 3 ---------- */
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

/* ---------- Runtime ---------- */
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/* ---------- UI ---------- */
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val HeaderPrimary = Color(0xFF0F172A)
private val PageBackground = Color(0xFFF5F6F7)
private val CardBackground = Color.White
private val Muted = Color(0xFF6B7280)
private val AccentAmberStart = Color(0xFFF59E0B)
private val AccentAmberEnd = Color(0xFFF97316)

private data class ProfileMenuItem(
    val icon: @Composable () -> Unit,
    val label: String,
    val route: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isLawyer: Boolean,
    lawyerName: String = "Adv. Rajesh Kumar",
    lawyerTitle: String = "Senior Advocate • High Court",
    clientName: String = "Client User",
    clientTitle: String = "Your Legal Companion",
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onLogoutConfirm: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val menuItems = remember {
        listOf(
            ProfileMenuItem(
                icon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Edit Profile",
                        tint = Color(0xFF374151),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = "Edit Profile",
                route = "profile/edit"
            ),
            ProfileMenuItem(
                icon = {
                    Icon(
                        Icons.Filled.History,
                        contentDescription = "Case History",
                        tint = Color(0xFF374151),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = "Case History",
                route = "profile/history"
            ),
            ProfileMenuItem(
                icon = {
                    Icon(
                        Icons.Filled.FavoriteBorder,
                        contentDescription = "Saved Favorites",
                        tint = Color(0xFF374151),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = "Saved Favorites",
                route = "profile/favorites"
            ),
            ProfileMenuItem(
                icon = {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF374151),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = "Settings",
                route = "settings"
            ),
            ProfileMenuItem(
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Help & Support",
                        tint = Color(0xFF374151),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = "Help & Support",
                route = "profile/help"
            )
        )
    }

    val displayName = remember(isLawyer, lawyerName, clientName) {
        if (isLawyer) lawyerName else clientName
    }
    val displayTitle = remember(isLawyer, lawyerTitle, clientTitle) {
        if (isLawyer) lawyerTitle else clientTitle
    }
    val initials = remember(displayName) {
        displayName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifBlank { "LC" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigate("home")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HeaderPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {

                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("home") },
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("Home", fontWeight = FontWeight.Bold) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("ai/insights") },
                    icon = {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = "AI",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("AI Insights", fontWeight = FontWeight.Bold) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("cases") },
                    icon = {
                        Icon(
                            Icons.Filled.Folder,
                            contentDescription = "Cases",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("Cases", fontWeight = FontWeight.Bold) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("docs") },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Article,
                            contentDescription = "Docs",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("Docs", fontWeight = FontWeight.Bold) }
                )

                // ✅ CHAT (role-aware, matches HomeScreen behaviour)
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        if (isLawyer) {
                            onNavigate("chat/lawyer")
                        } else {
                            onNavigate("chat/client")
                        }
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Chat",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("Chat", fontWeight = FontWeight.Bold) }
                )

                // ✅ PROFILE (CURRENT SCREEN)
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(22.dp),
                            tint = HeaderPrimary
                        )
                    },
                    label = { Text("Profile", fontWeight = FontWeight.ExtraBold) }
                )
            }
        },
                containerColor = PageBackground

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Profile header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderPrimary)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = displayName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = displayTitle,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStat(label = "Cases", value = "124")
                        ProfileStat(label = "Rating", value = "4.9")
                        ProfileStat(label = "Years", value = "15")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscription card
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(AccentAmberStart, AccentAmberEnd)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = "Pro Plan",
                                    tint = Color.White
                                )
                            }

                            Column {
                                Text(
                                    text = "Pro Plan",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Valid until Dec 2025",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        TextButton(
                            onClick = { /* TODO: handle upgrade */ },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Upgrade",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu card
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    menuItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = item.route != null,
                                    onClick = { item.route?.let(onNavigate) }
                                )
                                .then(
                                    if (index != 0) Modifier
                                        .background(Color.Transparent)
                                    else Modifier
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3F4F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                item.icon()
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = item.label,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = Color(0xFF111827)
                            )

                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = Muted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (index != menuItems.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color(0xFFE5E7EB)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showLogoutDialog = true }
                    .background(Color(0xFFFEF2F2))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Log Out",
                        tint = Color(0xFFB91C1C)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Log Out",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB91C1C),
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Legal info
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Privacy Policy",
                        color = Muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "  ·  ",
                        color = Muted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Terms of Service",
                        color = Muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out?") },
            text = { Text("Are you sure you want to log out and return to role selection?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutConfirm()
                    }
                ) {
                    Text("Log out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
        )
    }
}


