package com.simats.legalchain.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DARK_GREEN = Color(0xFF0B5D2E)
private val PAGE_BG = Color(0xFFF5F6F7)
private val CARD_WHITE = Color.White
private val MUTED = Color(0xFF6B7280)

data class DocumentCategory(
    val id: String,
    val name: String,
    val description: String,
    val count: Int,
    val iconProvider: @Composable () -> Unit
)

private fun defaultCategories() = listOf(
    DocumentCategory("fir", "FIR Documents", "First Information Reports", 5) {
        Icon(Icons.Filled.Warning, contentDescription = "FIR", tint = DARK_GREEN, modifier = Modifier.size(26.dp))
    },
    DocumentCategory("petition", "Petitions", "Filed petitions and applications", 12) {
        // use AutoMirrored Article to avoid deprecation warnings
        Icon(Icons.AutoMirrored.Filled.Article, contentDescription = "Petition", tint = DARK_GREEN, modifier = Modifier.size(26.dp))
    },
    DocumentCategory("evidence", "Evidence", "Supporting evidence documents", 18) {
        // use AutoMirrored InsertDriveFile to avoid deprecation warnings
        Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = "Evidence", tint = DARK_GREEN, modifier = Modifier.size(26.dp))
    },
    DocumentCategory("notice", "Notices", "Legal notices and summons", 8) {
        Icon(Icons.Filled.Warning, contentDescription = "Notice", tint = DARK_GREEN, modifier = Modifier.size(26.dp))
    },
    DocumentCategory("orders", "Court Orders", "Judgments and orders", 6) {
        Icon(Icons.Filled.Gavel, contentDescription = "Orders", tint = DARK_GREEN, modifier = Modifier.size(26.dp))
    },
    DocumentCategory("other", "Other Documents", "Miscellaneous files", 3) {
        Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = "Other", tint = DARK_GREEN, modifier = Modifier.size(26.dp))
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentCategoriesScreen(
    onBack: () -> Unit = {},
    onOpenCategory: (categoryId: String) -> Unit = {}
) {
    val categories = remember { defaultCategories() }
    val totalDocs = categories.sumOf { it.count }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Document Categories",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // AutoMirrored ArrowBack used to avoid deprecation warning
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DARK_GREEN)
            )
        },
        containerColor = PAGE_BG
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DARK_GREEN)
                    .padding(vertical = 18.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Total Documents", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = totalDocs.toString(), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("6", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Categories", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("12", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Cases", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("45 MB", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Storage", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(categories) { _, category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCategory(category.id) },
                        colors = CardDefaults.cardColors(containerColor = CARD_WHITE),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DARK_GREEN.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                category.iconProvider()
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = category.description,
                                    fontSize = 13.sp,
                                    color = MUTED
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = category.count.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(text = "files", fontSize = 12.sp, color = MUTED)
                            }
                        }
                    }
                }
            }
        }
    }
}
