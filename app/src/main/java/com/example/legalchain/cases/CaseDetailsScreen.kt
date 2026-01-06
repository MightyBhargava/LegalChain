package com.example.legalchain.cases

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.foundation.layout.RowScope

private val DarkGreen = Color(0xFF0B5D2E)
private val SecondaryGreen = Color(0xFF10B981)
private val PageBg = Color(0xFFF5F6F7)
private val CardWhite = Color.White
private val MutedText = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailsScreen(
    caseId: String,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val role = prefs.getString("userRole", "client") ?: "client"
    val isLawyer = role == "lawyer"

    val cases by CaseRepository.cases.collectAsState()
    val case = cases.find { it.id == caseId } ?: run {
        onBack()
        return
    }

    var activeTab by remember { mutableStateOf("overview") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Case Details",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (isLawyer) {
                        IconButton(onClick = { onNavigate("cases/edit/${case.id}") }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White)
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
                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black, modifier = Modifier.size(22.dp))
                    },
                    label = { Text("Home", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("ai/insights") },
                    icon = {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "AI", tint = Color.Black, modifier = Modifier.size(22.dp))
                    },
                    label = { Text("AI", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already here */ },
                    icon = {
                        Icon(Icons.Filled.Folder, contentDescription = "Cases", tint = Color.Black, modifier = Modifier.size(22.dp))
                    },
                    label = { Text("Cases", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("docs") },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.Article, contentDescription = "Docs", tint = Color.Black, modifier = Modifier.size(22.dp))
                    },
                    label = { Text("Docs", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { if (isLawyer) onNavigate("chat/lawyer") else onNavigate("chat/client") },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = Color.Black, modifier = Modifier.size(22.dp))
                    },
                    label = { Text("Chat", color = Color.Black, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("profile") },
                    icon = {
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Black, modifier = Modifier.size(22.dp))
                    },
                    label = { Text("Profile", color = Color.Black, fontWeight = FontWeight.Bold) }
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
            // Header block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isLawyer) DarkGreen else SecondaryGreen)
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Scale, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            CaseStatusBadge(case.status)

                            Spacer(Modifier.height(6.dp))
                            Text(
                                case.title,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Tag, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                case.caseNumber,
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Description, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                case.type,
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardWhite)
            ) {
                CaseTabButton(
                    label = "Overview",
                    selected = activeTab == "overview",
                    isLawyer = isLawyer
                ) { activeTab = "overview" }

                CaseTabButton(
                    label = "Evidence",
                    selected = activeTab == "evidence",
                    isLawyer = isLawyer
                ) { activeTab = "evidence" }

                CaseTabButton(
                    label = "AI Assistant",
                    selected = activeTab == "ai",
                    isLawyer = isLawyer
                ) { activeTab = "ai" }
            }

            // Content + bottom remove button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (activeTab) {
                    "overview" -> CaseOverviewTab(case, isLawyer, onNavigate)
                    "evidence" -> CaseEvidenceTab(isLawyer, onNavigate)
                    "ai" -> CaseAITab(isLawyer, caseId, onNavigate)
                }

                Spacer(Modifier.height(24.dp))

                if (isLawyer) {
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    Spacer(Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            // Remove from in-memory repository
                            CaseRepository.removeCase(case.id)

                            // Persist deletion so the case won't show again after login/restart
                            val deletedSet = prefs.getStringSet("deleted_case_ids", emptySet())?.toMutableSet()
                                ?: mutableSetOf()
                            deletedSet.add(case.id)
                            prefs.edit().putStringSet("deleted_case_ids", deletedSet).apply()

                            onNavigate("cases")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red)
                        Spacer(Modifier.width(6.dp))
                        Text("Remove Case", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CaseStatusBadge(status: String) {
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
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(text)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            color = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RowScope.CaseTabButton(label: String, selected: Boolean, isLawyer: Boolean, onClick: () -> Unit) {
    val color = if (isLawyer) DarkGreen else SecondaryGreen
    val selectedColor = if (selected) color else Color.Transparent
    val textColor = if (selected) color else MutedText

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(70.dp)
                .background(selectedColor)
        )
    }
}

@Composable
private fun CaseOverviewTab(case: CaseModel, isLawyer: Boolean, onNavigate: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                if (isLawyer) onNavigate("chat/client") else onNavigate("chat/lawyer")
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF), contentColor = DarkGreen)
        ) {
            Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "Chat with ${if (isLawyer) "Client" else "Lawyer"}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (isLawyer) {
            OutlinedButton(
                onClick = { onNavigate("hearings/add") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Hearing", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            OutlinedButton(
                onClick = { onNavigate("payments") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Make Payment", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    Spacer(Modifier.height(14.dp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Next Hearing", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                TextButton(onClick = { }) {
                    Text("View Details", fontSize = 14.sp, color = DarkGreen)
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(case.nextHearing.ifBlank { "Not scheduled" }, fontSize = 14.sp, color = Color(0xFF111827))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(case.hearingTime.ifBlank { "--" }, fontSize = 14.sp, color = Color(0xFF111827))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Apartment, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(case.court.ifBlank { "Court not set" }, fontSize = 14.sp, color = Color(0xFF111827))
                }
            }
        }
    }

    Spacer(Modifier.height(14.dp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Case Activity", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                TextButton(onClick = { onNavigate("cases/activity") }) {
                    Text("View Log", fontSize = 15.sp, color = DarkGreen, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text("Track all updates and history for this case.", fontSize = 14.sp, color = MutedText)
        }
    }

    Spacer(Modifier.height(14.dp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Parties Involved", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PartyRow(label = "Petitioner", value = case.petitioner, icon = Icons.Filled.Person)
                PartyRow(label = "Respondent", value = case.respondent, icon = Icons.Filled.PersonOutline)
                PartyRow(label = "Advocate", value = case.advocate, icon = Icons.Filled.Gavel)
            }
        }
    }

    Spacer(Modifier.height(14.dp))

    if (case.description.isNotBlank()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Case Summary", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Spacer(Modifier.height(6.dp))
                Text(case.description, fontSize = 14.sp, color = MutedText)
            }
        }
    }
}

@Composable
private fun PartyRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFE5F0FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 12.sp, color = MutedText)
            Text(value.ifBlank { "Not set" }, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun CaseEvidenceTab(isLawyer: Boolean, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Case Evidence", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            if (isLawyer) {
                OutlinedButton(
                    onClick = { onNavigate("documents/upload") },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add", fontSize = 13.sp)
                }
            }
        }

        EvidenceItem(
            title = "CCTV_Footage_01.mp4",
            subtitle = "Video • 245 MB • Added yesterday",
            icon = Icons.Filled.Videocam,
            iconBg = Color(0xFFFEE2E2),
            iconTint = Color(0xFFDC2626)
        )
        EvidenceItem(
            title = "Call_Recording_Nov.mp3",
            subtitle = "Audio • 12 MB • Added 2 days ago",
            icon = Icons.Filled.Mic,
            iconBg = Color(0xFFEDE9FE),
            iconTint = Color(0xFF7C3AED)
        )
        EvidenceItem(
            title = "Bank_Statement_Oct.pdf",
            subtitle = "Document • 2.4 MB • Added 1 week ago",
            icon = Icons.Filled.Description,
            iconBg = Color(0xFFE0F2FE),
            iconTint = Color(0xFF2563EB)
        )
    }
}

@Composable
private fun EvidenceItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(iconBg, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = MutedText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun CaseAITab(isLawyer: Boolean, caseId: String, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isLawyer) DarkGreen else SecondaryGreen
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "AI Case Analysis",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Based on recent filings, the case is 65% complete. Next critical step is the cross-examination.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate("ai/insights") },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Message,
                        contentDescription = null,
                        tint = DarkGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "AI Assistant",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF111827)
                    )
                    Text(
                        "Ask questions about this case",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Win Probability",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText
                    )
                    Text(
                        "72%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Suggested Actions", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }

                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SuggestionBullet(
                        if (isLawyer) "Upload original FIR copy for verification" else "Review the FIR copy uploaded by your lawyer"
                    )
                    SuggestionBullet(
                        if (isLawyer) "Prepare witness questions for next hearing" else "Confirm your availability for the next hearing"
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionBullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFF59E0B))
        )
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = MutedText)
    }
}
