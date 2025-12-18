@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.legalchain.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- COLORS ---------- */
private val PAGE_BG = Color(0xFFF5F6F7)
private val PRIMARY = Color(0xFF2563EB)
private val SECONDARY = Color(0xFF059669)
private val AMBER = Color(0xFFF59E0B)
private val MUTED = Color(0xFF6B7280)

/* ---------- DATA MODEL ---------- */
data class FavoriteItem(
    val type: String,
    val title: String,
    val subtitle: String
)

/* ---------- SAMPLE DATA ---------- */
private val favorites = listOf(
    FavoriteItem("case", "Singh vs. State", "Criminal Case"),
    FavoriteItem("doc", "Evidence_List.pdf", "Document"),
    FavoriteItem("lawyer", "Adv. Priya Sharma", "Family Lawyer")
)

@Composable
fun FavoritesScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saved Favorites",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        containerColor = PAGE_BG
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            favorites.forEach { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        val (icon, bgColor, iconColor) = when (item.type) {
                            "case" -> Triple(Icons.Default.Work, PRIMARY.copy(alpha = 0.1f), PRIMARY)
                            "doc" -> Triple(Icons.Default.Description, SECONDARY.copy(alpha = 0.1f), SECONDARY)
                            else -> Triple(Icons.Default.Star, AMBER.copy(alpha = 0.15f), AMBER)
                        }

                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(icon, contentDescription = null, tint = iconColor)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                            Text(
                                text = item.subtitle,
                                fontSize = 12.sp,
                                color = MUTED
                            )
                        }
                    }
                }
            }
        }
    }
}
