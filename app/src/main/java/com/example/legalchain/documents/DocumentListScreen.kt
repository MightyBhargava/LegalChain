@file:Suppress("DEPRECATION")

package com.example.legalchain.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.input.nestedscroll.nestedScroll

private val DARK_GREEN = Color(0xFF0B5D2E)
private val PAGE_BG = Color(0xFFF5F6F7)
private val CARD_WHITE = Color.White
private val MUTED = Color(0xFF6B7280)

private data class DocItem(
    val id: String,
    val name: String,
    val type: String,
    val size: String,
    val caseName: String,
    val uploadedAt: String,
    val category: String
)

private fun sampleDocs() = listOf(
    DocItem("1", "FIR_Copy.pdf", "pdf", "245 KB", "Singh vs. State", "Dec 10, 2024", "FIR"),
    DocItem("2", "Evidence_Photos.jpg", "image", "1.8 MB", "Singh vs. State", "Dec 8, 2024", "Evidence"),
    DocItem("3", "Bank_Statements.pdf", "pdf", "890 KB", "Singh vs. State", "Dec 5, 2024", "Evidence"),
    DocItem("4", "Property_Deed.pdf", "pdf", "2.1 MB", "Sharma Property", "Dec 3, 2024", "Petition"),
    DocItem("5", "Court_Order_Nov.pdf", "pdf", "156 KB", "Singh vs. State", "Nov 28, 2024", "Orders"),
    DocItem("6", "Witness_Statement.docx", "doc", "78 KB", "Singh vs. State", "Nov 25, 2024", "Evidence")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
    onBack: () -> Unit = {},
    onUpload: () -> Unit = {},
    onOpenCategory: (String) -> Unit = {},
    onOpenDocument: (String) -> Unit = {}
) {
    val docs = remember { sampleDocs() }
    var query by remember { mutableStateOf("") }

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
        containerColor = PAGE_BG
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
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
private fun DocumentRow(item: DocItem, onOpen: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CARD_WHITE),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            val bg = when (item.type) {
                "pdf" -> Color(0xFFFFF1F0)
                "image" -> Color(0xFFE8F3FF)
                "doc" -> Color(0xFFEEF2FF)
                else -> Color(0xFFF8F9FA)
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                Text(item.name, fontWeight = FontWeight.Medium, fontSize = 15.sp, maxLines = 1)
                Text(item.caseName, fontSize = 13.sp, color = MUTED)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.size, fontSize = 12.sp, color = MUTED)
                    Text("•", fontSize = 12.sp, color = MUTED)
                    Text(item.uploadedAt, fontSize = 12.sp, color = MUTED)
                }
            }

            Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = DARK_GREEN)
        }
    }
}
