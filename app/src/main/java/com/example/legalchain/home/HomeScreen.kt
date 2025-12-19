package com.example.legalchain.home

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val HeaderNavy = Color(0xFF062F3C)
private val DarkGreen = Color(0xFF004D40)
private val PageBg = Color(0xFFF5F6F7)
private val CardWhite = Color.White
private val Amber = Color(0xFFF59E0B)
private val PrimaryText = Color(0xFF0F172A)
private val MutedText = Color(0xFF6B7280)

private data class ActivityItem(
    val title: String,
    val subtitle: String,
    val icon: @Composable () -> Unit,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateRaw: (String) -> Unit = {},
    unreadCountProvider: (() -> Int)? = null
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val role = prefs.getString("userRole", "client") ?: "client"
    val isLawyer = role == "lawyer"

    fun navigate(routeRaw: String) {
        val route = if (routeRaw.startsWith("/")) routeRaw.removePrefix("/") else routeRaw
        onNavigateRaw(route)
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showScrollButtons by remember { mutableStateOf(false) }

    val activities = remember {
        listOf(
            ActivityItem(
                "New document uploaded",
                "Sharma Property Dispute • 2h ago",
                {
                    Icon(
                        Icons.AutoMirrored.Filled.Article,
                        contentDescription = "document",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                },
                "cases/1/details"
            ),
            ActivityItem(
                "Case status updated",
                "TechCorp Merger • 5h ago",
                {
                    Icon(
                        Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "chart",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                },
                "cases/2/details"
            )
        )
    }

    val unreadCount = unreadCountProvider?.invoke() ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isLawyer) "Advocate Dashboard" else "My Legal Dashboard",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HeaderNavy,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {

                    /* 🔔 Notifications */
                    IconButton(onClick = { navigate("notifications") }) {
                        Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-6).dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDD3E3E))
                                        .semantics { contentDescription = "unread notifications dot" }
                                )
                            }
                        }
                    }

                    /* 🔍 SEARCH ICON - Only visible for non-lawyers (clients) */
                    if (!isLawyer) {
                        IconButton(onClick = { navigate("search") }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    /* ⚙️ Settings */
                    IconButton(onClick = { navigate("settings") }) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CardWhite, tonalElevation = 4.dp) {
                NavigationBarItem(
                    selected = true,
                    onClick = { navigate("home") },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black, modifier = Modifier.size(22.dp)) },
                    label = { Text("Home", color = PrimaryText, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navigate("ai/insights") },
                    icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = "AI Insights", tint = Color.Black, modifier = Modifier.size(22.dp)) },
                    label = { Text("AI Insights", color = PrimaryText, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navigate("cases") },
                    icon = { Icon(Icons.Filled.Folder, contentDescription = "Cases", tint = Color.Black, modifier = Modifier.size(22.dp)) },
                    label = { Text("Cases", color = PrimaryText, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navigate("docs") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Article, contentDescription = "Docs", tint = Color.Black, modifier = Modifier.size(22.dp)) },
                    label = { Text("Docs", color = PrimaryText, fontWeight = FontWeight.Bold) }
                )

                // Chat item — use AutoMirrored to avoid deprecation
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        // route depends on logged-in role:
                        // - lawyer sees list of clients (chat/lawyer)
                        // - client opens chat with assigned lawyer (chat/client)
                        if (isLawyer) navigate("chat/lawyer") else navigate("chat/client")
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Chat",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Chat", color = PrimaryText, fontWeight = FontWeight.Bold) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navigate("profile") },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Black, modifier = Modifier.size(22.dp)) },
                    label = { Text("Profile", color = PrimaryText, fontWeight = FontWeight.Bold) }
                )
            }
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = showScrollButtons, enter = fadeIn(), exit = fadeOut()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallFloatingActionButton(
                            onClick = { scope.launch { listState.animateScrollToItem(0) } },
                            containerColor = DarkGreen,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Scroll up", modifier = Modifier.size(20.dp))
                        }

                        SmallFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    val last = listState.layoutInfo.totalItemsCount - 1
                                    if (last >= 0) listState.animateScrollToItem(last)
                                }
                            },
                            containerColor = DarkGreen,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Scroll down", modifier = Modifier.size(20.dp))
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showScrollButtons = !showScrollButtons },
                    containerColor = DarkGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", modifier = Modifier.size(20.dp))
                }
            }
        },
        containerColor = PageBg
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = HeaderNavy),
                    shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("Welcome back,", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (isLawyer) "Adv. Rajesh Kumar" else "Mr. Vikram Singh",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (isLawyer) "RK" else "VS", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            StatChip("Active Cases", "12")
                            StatChip("Hearings", "5")
                            StatChip("Pending", "3")
                        }
                    }
                }
            }

            item {
                Box(modifier = Modifier.offset(y = (-20).dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = "AI", tint = Color.White, modifier = Modifier.size(22.dp))
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text("AI Daily Insight", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryText)

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = if (isLawyer)
                                        "3 hearings scheduled for tomorrow. High Court case #452 requires document submission today."
                                    else
                                        "Your case #1234 has a new update. The hearing date has been confirmed for Dec 15th.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )

                                Spacer(Modifier.height(10.dp))

                                TextButton(onClick = { navigate("ai/daily") }) {
                                    Text("View full analysis", color = DarkGreen, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            if (isLawyer) {
                item {
                    Spacer(Modifier.height(4.dp))
                    Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCardExpanded(
                            icon = { Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(22.dp)) },
                            label = "New Case",
                            onClick = { navigate("cases/add") }
                        )

                        QuickActionCardExpanded(
                            icon = { Icon(Icons.Filled.Event, contentDescription = "Event", tint = Color.Black, modifier = Modifier.size(22.dp)) },
                            label = "Add Hearing",
                            onClick = { navigate("hearings/add") } // booking route exists in MainActivity
                        )
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Upcoming Hearings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
                    Text("View All",
                        color = DarkGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navigate("cases") })
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(Modifier.padding(14.dp)) {

                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(72.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Amber)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Singh vs. State of Maharashtra", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
                            Spacer(Modifier.height(4.dp))
                            Text("High Court Mumbai • Room 5", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                            Spacer(Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.AccessTime, contentDescription = "Time", tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("10:30 AM", color = MutedText, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Amber, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Prepare arguments", color = MutedText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        StatusBadge("Tomorrow")
                    }
                }
            }

            item {
                Text("Recent Activity", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
                Spacer(Modifier.height(6.dp))
            }

            itemsIndexed(activities) { _, act ->
                ActivityCard(title = act.title, subtitle = act.subtitle, icon = act.icon) { navigate(act.route) }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFEFF7EF)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
            Spacer(Modifier.width(6.dp))
            Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
        }
    }
}

@Composable
private fun RowScope.StatChip(title: String, value: String) {
    Surface(color = HeaderNavy.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(10.dp)) {
            Text(title, fontSize = 12.sp, color = Color(0xFFBFD6D6), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun RowScope.QuickActionCardExpanded(icon: @Composable () -> Unit, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(92.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFF2F4F6)), contentAlignment = Alignment.Center) {
                icon()
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(label, color = PrimaryText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ActivityCard(title: String, subtitle: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFF0F7F5)), contentAlignment = Alignment.Center) {
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MutedText)
            }

            Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = Color.Black, modifier = Modifier.size(20.dp))
        }
    }
}

