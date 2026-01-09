@file:Suppress("DEPRECATION")

package com.simats.legalchain.documents

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.nestedscroll.nestedScroll

private val DARK_GREEN = Color(0xFF0B5D2E)
private val PAGE_BG = Color(0xFFF5F6F7)
private val CARD_WHITE = Color.White
private val MUTED = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
    onBack: () -> Unit = {},
    onUpload: () -> Unit = {},
    onOpenCategory: (String) -> Unit = {},
    onOpenDocument: (String) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val docs by DocumentRepository.documents.collectAsState()
    var query by remember { mutableStateOf("") }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val role = prefs.getString("userRole", "client") ?: "client"
    val isLawyer = role == "lawyer"

    val filtered = remember(query, docs) {
        if (query.isBlank()) docs
        else docs.filter {
            it.name.contains(query, true) ||
                    it.caseName.contains(query, true) ||
                    it.category.contains(query, true)
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Documents", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onUpload) {
                        Icon(Icons.Filled.Add, contentDescription = "Upload", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = DARK_GREEN,
                    scrolledContainerColor = DARK_GREEN
                ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CARD_WHITE, tonalElevation = 4.dp) {
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
                    label = { Text("Home", color = Color.Black, fontWeight = FontWeight.Bold) }
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
                    label = { Text("AI Insights", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("cases") },
                    icon = {
                        Icon(
                            Icons.Filled.Folder,
                            contentDescription = "Cases",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Cases", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already here */ },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Article,
                            contentDescription = "Docs",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Docs", color = Color.Black, fontWeight = FontWeight.Bold) }
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
                    label = { Text("Profile", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
            }
        },
        containerColor = PAGE_BG
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 110.dp, start = 12.dp, end = 12.dp, top = 6.dp)
        ) {
            item {
                Surface(color = CARD_WHITE, tonalElevation = 2.dp) {
                    Column(Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Search documents...", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = DARK_GREEN) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenCategory("all") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CARD_WHITE),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DARK_GREEN),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.InsertDriveFile, contentDescription = "Categories", tint = Color.White)
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(Modifier.weight(1f)) {
                                    Text("Browse by Category", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.Black)
                                    Text("FIR, Petition, Evidence, Orders", fontSize = 13.sp, color = MUTED)
                                }

                                Icon(Icons.Filled.ChevronRight, contentDescription = "Open categories", tint = DARK_GREEN)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "${filtered.size} documents",
                    Modifier.padding(horizontal = 16.dp),
                    color = MUTED,
                    fontSize = 13.sp
                )
            }

            itemsIndexed(filtered) { _, doc ->
                DocumentRow(item = doc) { onOpenDocument(doc.id) }
            }
        }
    }
}

@Composable
private fun DocumentRow(item: Document, onOpen: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CARD_WHITE),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val bg = when (item.type) {
                "pdf" -> Color(0xFFFFF1F0)
                "image" -> Color(0xFFE8F3FF)
                "doc" -> Color(0xFFEEF2FF)
                else -> Color(0xFFF8F9FA)
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                when (item.type) {
                    "pdf" -> Icon(Icons.Filled.InsertDriveFile, contentDescription = "PDF", tint = Color(0xFFD14343), modifier = Modifier.size(26.dp))
                    "image" -> Icon(Icons.Filled.Image, contentDescription = "Image", tint = Color(0xFF2563EB), modifier = Modifier.size(26.dp))
                    "doc" -> Icon(Icons.Filled.InsertDriveFile, contentDescription = "Doc", tint = Color(0xFF1E3A8A), modifier = Modifier.size(26.dp))
                    else -> Icon(Icons.Filled.InsertDriveFile, contentDescription = "File", tint = MUTED, modifier = Modifier.size(26.dp))
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text(item.caseName, fontSize = 13.sp, color = MUTED, maxLines = 1)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.size, fontSize = 12.sp, color = MUTED)
                    Text("•", fontSize = 12.sp, color = MUTED)
                    Text(item.uploadedAt, fontSize = 12.sp, color = MUTED)
                }
            }

            Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = DARK_GREEN)
        }
    }
}
