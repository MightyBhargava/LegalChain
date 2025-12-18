package com.example.legalchain.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkGreen = Color(0xFF0B5D2E)
private val PageBg = Color(0xFFF5F6F7)
private val CardWhite = Color.White
private val MutedGray = Color(0xFF6B7280)
private val PrimaryBlack = Color(0xFF0F172A)

private data class CaseItem(
    val id: String,
    val title: String,
    val caseNumber: String,
    val type: String,
    val status: String,
    val court: String,
    val nextHearing: String,
    val client: String
)

private fun sampleCases() = listOf(
    CaseItem("1", "Singh vs. State of Maharashtra", "CR/2024/1234", "Criminal", "active", "High Court Mumbai", "Dec 15, 2024", "Mr. Vikram Singh"),
    CaseItem("2", "Sharma Property Dispute", "CV/2024/5678", "Civil", "pending", "District Court Delhi", "Dec 18, 2024", "Mrs. Priya Sharma"),
    CaseItem("3", "TechCorp Merger Agreement", "CO/2024/9012", "Corporate", "active", "NCLT Mumbai", "Dec 20, 2024", "TechCorp Pvt Ltd"),
    CaseItem("4", "Kumar Divorce Settlement", "FM/2024/3456", "Family", "pending", "Family Court", "Dec 22, 2024", "Mr. Anil Kumar"),
    CaseItem("5", "Patel vs. Patel", "CV/2024/7890", "Civil", "closed", "District Court", "-", "Mr. Rajesh Patel"),
    CaseItem("6", "State vs. Mehta", "CR/2024/2345", "Criminal", "active", "Sessions Court", "Dec 25, 2024", "Mr. Suresh Mehta")
)

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
    val allCases = remember { sampleCases() }

    var query by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Pending", "Closed")

    val filteredCases = remember(query, activeFilter, allCases) {
        allCases.filter { c ->
            val fMatch = activeFilter == "All" || c.status.equals(activeFilter, ignoreCase = true)
            val qMatch = query.isBlank() || c.title.contains(query, true) || c.caseNumber.contains(query, true)
            fMatch && qMatch
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
                        // use AutoMirrored ArrowBack to avoid deprecation warning
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
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Surface(
                color = CardWhite,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                            placeholder = { Text("Search cases...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { /* open filter screen */ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F1F1))
                        ) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        filters.forEach { f ->
                            val selected = activeFilter == f
                            AssistChip(
                                onClick = { activeFilter = f },
                                label = { Text(f, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, fontSize = 13.sp) },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (selected) DarkGreen else Color(0xFFF2F2F2),
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
                Row(modifier = Modifier
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
private fun CaseCard(item: CaseItem, isLawyer: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF2F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Work, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(26.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = PrimaryBlack)
                    StatusBadge(status = item.status)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text("${item.caseNumber} • ${item.type}", color = MutedGray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Apartment, contentDescription = "Court", tint = MutedGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(item.court, color = MutedGray, fontSize = 12.sp)
                }

                if (item.nextHearing != "-") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Next hearing", tint = DarkGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Next: ${item.nextHearing}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (isLawyer) DarkGreen else Color(0xFF0D9488))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bg, txt) = when (status.lowercase()) {
        "active" -> Pair(Color(0xFFEFF7EF), Color(0xFF065F46))
        "pending" -> Pair(Color(0xFFFEF7E0), Color(0xFF92400E))
        "closed" -> Pair(Color(0xFFF3F4F6), Color(0xFF374151))
        else -> Pair(Color(0xFFF3F4F6), Color(0xFF374151))
    }

    Surface(shape = RoundedCornerShape(14.dp), color = bg) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
            Text(text = status.replaceFirstChar { it.uppercase() }, color = txt, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}
