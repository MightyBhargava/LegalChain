package com.example.legalchain.cases

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkGreen = Color(0xFF0B5D2E)
private val PageBg = Color(0xFFF5F6F7)
private val CardWhite = Color.White
private val MutedGray = Color(0xFF6B7280)
private val PrimaryBlack = Color(0xFF0F172A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseListScreen(
    isLawyer: Boolean,
    onAddCase: () -> Unit,
    onOpenCase: (String) -> Unit,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onBrowseCategories: () -> Unit = {}
) {
    val allCases by CaseRepository.cases.collectAsState()

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val deletedIds = prefs.getStringSet("deleted_case_ids", emptySet()) ?: emptySet()

    var query by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Pending", "Closed")

    val filteredCases = remember(query, activeFilter, allCases, deletedIds) {
        allCases.filter { c ->
            val notDeleted = !deletedIds.contains(c.id)
            val fMatch = activeFilter == "All" || c.status.equals(activeFilter, ignoreCase = true)
            val qMatch = query.isBlank() ||
                    c.title.contains(query, true) ||
                    c.caseNumber.contains(query, true)
            notDeleted && fMatch && qMatch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isLawyer) "My Cases" else "My Legal Matters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isLawyer) {
                        IconButton(onClick = onAddCase) {
                            Icon(Icons.Filled.Add, contentDescription = "Add case", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CardWhite, tonalElevation = 4.dp) {
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("home") },
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Home", color = PrimaryBlack, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("ai/insights") },
                    icon = {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = "AI Insights",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("AI Insights", color = PrimaryBlack, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already here */ },
                    icon = {
                        Icon(
                            Icons.Filled.Folder,
                            contentDescription = "Cases",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Cases", color = PrimaryBlack, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("docs") },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Article,
                            contentDescription = "Docs",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Docs", color = PrimaryBlack, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        if (isLawyer) onNavigate("chat/lawyer") else onNavigate("chat/client")
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Chat",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Chat", color = PrimaryBlack, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("profile") },
                    icon = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Profile", color = PrimaryBlack, fontWeight = FontWeight.Bold) }
                )
            }
        },
        containerColor = PageBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PageBg)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = DarkGreen) },
                        placeholder = { Text("Search by title or case number", fontSize = 14.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { f ->
                            val selected = activeFilter == f
                            FilterChip(
                                selected = selected,
                                onClick = { activeFilter = f },
                                label = {
                                    Text(
                                        f,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (selected) DarkGreen else Color(0xFFF3F4F6),
                                    labelColor = if (selected) Color.White else MutedGray
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickable { onBrowseCategories() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Work, contentDescription = "Category", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Browse by Category", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = PrimaryBlack)
                        Text("Civil, Criminal, Corporate, Family", color = MutedGray, fontSize = 13.sp)
                    }

                    Icon(Icons.Filled.ChevronRight, contentDescription = "Open categories", tint = DarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${filteredCases.size} cases found",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MutedGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                itemsIndexed(filteredCases) { _, item ->
                    CaseCard(item = item, isLawyer = isLawyer) {
                        onOpenCase(item.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun CaseCard(item: CaseModel, isLawyer: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Gavel, contentDescription = null, tint = DarkGreen)
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(2.dp))
                    Text(item.caseNumber, fontSize = 12.sp, color = MutedGray)
                }

                StatusPill(item.status)
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Court", fontSize = 11.sp, color = MutedGray)
                    Text(item.court.ifBlank { "-" }, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Next Hearing", fontSize = 11.sp, color = MutedGray)
                    Text(item.nextHearing.ifBlank { "-" }, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val (bg, text) = when (status.lowercase()) {
        "active" -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
        "pending" -> Color(0xFFFFF7ED) to Color(0xFFEA580C)
        "closed" -> Color(0xFFF3F4F6) to Color(0xFF6B7280)
        else -> Color(0xFFE5E7EB) to Color(0xFF4B5563)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(text)
        )
        Spacer(Modifier.width(4.dp))
        Text(status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, color = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
