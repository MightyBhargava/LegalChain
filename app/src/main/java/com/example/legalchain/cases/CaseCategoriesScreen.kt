package com.example.legalchain.cases

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkGreen = Color(0xFF0B5D2E)
private val BackgroundColor = Color(0xFFF5F6F7)
private val CardWhite = Color.White

data class CaseCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val count: Int,
    val color: Color,
    val bgColor: Color
)

private val categories = listOf(
    CaseCategory(
        id = "civil",
        name = "Civil Cases",
        description = "Property disputes, contracts, torts",
        icon = Icons.Filled.Balance,
        count = 8,
        color = Color(0xFF3B82F6),
        bgColor = Color(0xFFEFF6FF)
    ),
    CaseCategory(
        id = "criminal",
        name = "Criminal Cases",
        description = "Criminal defense and prosecution",
        icon = Icons.Filled.Shield,
        count = 5,
        color = Color(0xFFEF4444),
        bgColor = Color(0xFFFEF2F2)
    ),
    CaseCategory(
        id = "corporate",
        name = "Corporate Cases",
        description = "Business law, mergers, compliance",
        icon = Icons.Filled.Business,
        count = 4,
        color = Color(0xFFA855F7),
        bgColor = Color(0xFFFAF5FF)
    ),
    CaseCategory(
        id = "family",
        name = "Family Cases",
        description = "Divorce, custody, inheritance",
        icon = Icons.Filled.Favorite,
        count = 3,
        color = Color(0xFFEC4899),
        bgColor = Color(0xFFFDF2F8)
    ),
    CaseCategory(
        id = "constitutional",
        name = "Constitutional",
        description = "Fundamental rights, PIL",
        icon = Icons.Filled.Gavel,
        count = 2,
        color = Color(0xFFF59E0B),
        bgColor = Color(0xFFFFFBEB)
    ),
    CaseCategory(
        id = "other",
        name = "Other Cases",
        description = "Miscellaneous legal matters",
        icon = Icons.Filled.Description,
        count = 1,
        color = Color(0xFF6B7280),
        bgColor = Color(0xFFF9FAFB)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseCategoriesScreen(
    onBack: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val role = prefs.getString("userRole", "client") ?: "client"
    val isLawyer = role == "lawyer"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Case Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardWhite,
                tonalElevation = 4.dp
            ) {
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
                    selected = true,
                    onClick = { /* Already on cases screen */ },
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
                    label = { Text("Docs", color = Color.Black, fontWeight = FontWeight.Bold) }
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
                    label = { Text("Chat", color = Color.Black, fontWeight = FontWeight.Bold) }
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
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGreen)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Total Cases",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        "23",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("12", "Active")
                        StatItem("6", "Pending")
                        StatItem("5", "Closed")
                    }
                }
            }

            // Categories Grid
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "BROWSE BY CATEGORY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    itemsIndexed(categories) { index, category ->
                        CategoryCard(
                            category = category,
                            index = index,
                            onClick = { onCategoryClick(category.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CategoryCard(
    category: CaseCategory,
    index: Int,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(category.bgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Category Name
            Text(
                category.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF111827),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(2.dp))

            // Description
            Text(
                category.description,
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(category.color, CircleShape)
                )
                Text(
                    "${category.count} cases",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )
            }
        }
    }
}
