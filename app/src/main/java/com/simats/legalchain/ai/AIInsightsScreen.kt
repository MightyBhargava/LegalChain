@file:OptIn(ExperimentalMaterial3Api::class)

package com.simats.legalchain.ai

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

/* ---------- COLORS ---------- */
private val HeaderDark = Color(0xFF0F2F44)
private val HeaderLight = Color(0xFF183D57)
private val Green = Color(0xFF2E7D32)
private val Amber = Color(0xFFF59E0B)
private val Red = Color(0xFFDC2626)
private val PageBg = Color(0xFFF5F6F7)
private val CardWhite = Color.White
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)

/* ---------- DATA ---------- */
data class Insight(
    val type: String,
    val title: String,
    val description: String,
    val urgent: Boolean,
    val action: String
)

data class CaseProgress(
    val name: String,
    val progress: Int,
    val status: String
)

private val insights = listOf(
    Insight(
        "alert",
        "Missing Documents",
        "Singh vs. State case is missing certified copy of bank statements",
        true,
        "Upload Now"
    ),
    Insight(
        "recommendation",
        "Case Progress Update",
        "Sharma Property case has been pending for 45 days. Consider filing for expedited hearing.",
        false,
        "View Case"
    ),
    Insight(
        "reminder",
        "Upcoming Deadline",
        "Response filing deadline for TechCorp case in 3 days",
        true,
        "View Details"
    )
)

private val caseProgress = listOf(
    CaseProgress("Singh vs. State", 65, "On Track"),
    CaseProgress("Sharma Property", 40, "Delayed"),
    CaseProgress("TechCorp Merger", 80, "On Track")
)

/* ---------- SCREEN ---------- */
@Composable
fun AIInsightsScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val role = prefs.getString("userRole", "client") ?: "client"
    val isLawyer = role == "lawyer"

    fun showComingSoon(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Insights",
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
                    label = { Text("Home", color = TextDark, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already here */ },
                    icon = {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = "AI Insights",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("AI Insights", color = TextDark, fontWeight = FontWeight.Bold) }
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
                    label = { Text("Cases", color = TextDark, fontWeight = FontWeight.Bold) }
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
                    label = { Text("Docs", color = TextDark, fontWeight = FontWeight.Bold) }
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
                    label = { Text("Profile", color = TextDark, fontWeight = FontWeight.Bold) }
                )
            }
        },
        containerColor = PageBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Header()

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActions(
                    onAssistant = { onNavigate("ai/assistant") },
                    onDaily = { onNavigate("ai/daily") },
                    onFuture = { showComingSoon("Stay tuned for future updates.") }
                )

                Text(
                    "Action Required",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    color = TextDark
                )

                insights.forEach { insight ->
                    InsightCard(
                        insight = insight,
                        onActionClick = {
                            if (insight.action.equals("Upload Now", ignoreCase = true)) {
                                onNavigate("docs")
                            } else {
                                showComingSoon("Stay tuned for future updates.")
                            }
                        }
                    )
                }
                CaseProgressSection(
                    onViewAll = { showComingSoon("Stay tuned for future updates.") }
                )

                SummaryCard(
                    onClick = { showComingSoon("Stay tuned for AI summaries.") }
                )
            }
        }
    }
}

/* ---------- SECTIONS ---------- */
@Composable
private fun Header() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(HeaderLight, HeaderDark)))
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = "AI", tint = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Smart Insights",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp
                )
                Text(
                    "AI-powered recommendations",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox("3", "Alerts")
            StatBox("12", "Cases")
            StatBox("85%", "Health")
        }
    }
}

@Composable
private fun QuickActions(
    onAssistant: () -> Unit,
    onDaily: () -> Unit,
    onFuture: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAction(
            title = "AI Assistant",
            subtitle = "Ask legal queries",
            icon = Icons.Filled.AutoAwesome,
            tint = Green,
            modifier = Modifier.weight(1f),
            onClick = onAssistant
        )

        QuickAction(
            title = "Daily Insights",
            subtitle = "Legal updates",
            icon = Icons.Outlined.Lightbulb,
            tint = Amber,
            modifier = Modifier.weight(1f),
            onClick = onDaily
        )
    }
}

@Composable
private fun CaseProgressSection(onViewAll: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Case Progress",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
                color = TextDark
            )
            Text(
                "View All",
                color = Green,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                caseProgress.forEach { progress ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                progress.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                progress.status,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (progress.status == "On Track") Green else Amber
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = if (progress.status == "On Track") Green else Amber,
                            trackColor = Color(0xFFE5E7EB)
                        )

                        Text(
                            "${progress.progress}% complete",
                            fontSize = 14.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Green),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Description, contentDescription = "Summary", tint = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    "Generate Case Summary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextDark
                )
                Text(
                    "AI-powered case analysis",
                    fontSize = 14.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Open",
                tint = Color.Gray
            )
        }
    }
}

/* ---------- SMALL COMPOSABLES ---------- */
@Composable
private fun RowScope.StatBox(value: String, label: String) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(vertical = 14.dp, horizontal = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun QuickAction(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.heightIn(min = 120.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = tint)
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
            Text(subtitle, fontSize = 14.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InsightCard(
    insight: Insight,
    onActionClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (insight.type) {
                            "alert" -> Red.copy(alpha = 0.15f)
                            "recommendation" -> Color(0xFFDBEAFE)
                            else -> Amber.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (insight.type) {
                        "alert" -> Icons.Filled.Warning
                        "recommendation" -> Icons.Outlined.Lightbulb
                        else -> Icons.Outlined.Schedule
                    },
                    contentDescription = insight.type,
                    tint = if (insight.urgent) Red else Green
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        insight.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (insight.urgent) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Red.copy(alpha = 0.95f))
                            )
                            Text(
                                "Urgent",
                                color = Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    insight.description,
                    fontSize = 15.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                TextButton(
                    onClick = onActionClick,
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                ) {
                    Text(
                        insight.action,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Green
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = "Open",
                        tint = Green,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
