package com.simats.legalchain.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

// SINGLE DARK GREEN COLOR
private val DARK_GREEN = Color(0xFF0B5D2E)
private val PRIMARY_BG = Color(0xFFF5F6F7)
private val CARD_WHITE = Color.White
private val MUTED_TEXT = Color(0xFF6B7280)

/** Activity model */
data class CaseActivity(
    val id: String,
    val type: String,
    val action: String,
    val description: String,
    val user: String,
    val timestamp: String
)

private fun sampleActivities() = listOf(
    CaseActivity("1", "document", "Document uploaded", "Evidence_List.pdf was added to case documents", "Adv. Rajesh Kumar", "2 hours ago"),
    CaseActivity("2", "hearing", "Hearing scheduled", "New hearing date set for December 15, 2024", "System", "5 hours ago"),
    CaseActivity("3", "note", "Note added", "Added notes about Section 65B arguments", "Adv. Rajesh Kumar", "Yesterday"),
    CaseActivity("4", "task", "Task completed", "Filed reply to prosecution arguments", "Adv. Rajesh Kumar", "Yesterday"),
    CaseActivity("5", "update", "Status updated", "Case status changed to \"Under Trial\"", "System", "2 days ago"),
    CaseActivity("6", "message", "Client message", "Received message from Mr. Vikram Singh", "Mr. Vikram Singh", "3 days ago")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseActivityScreen(onBack: (() -> Unit)? = null) {

    val activities = remember { sampleActivities() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Activity Log",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            // FIXED: uses AutoMirrored (NOT deprecated)
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DARK_GREEN)
            )
        },
        containerColor = PRIMARY_BG
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // TOP green box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DARK_GREEN)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Singh vs. State of Maharashtra",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Recent activity and updates",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {

                itemsIndexed(activities) { index, activity ->

                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(56.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(DARK_GREEN.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getTypeIcon(activity.type),
                                    contentDescription = null,
                                    tint = DARK_GREEN,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (index != activities.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(72.dp)
                                        .background(Color(0xFFE6E9EB))
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CARD_WHITE),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {

                                Text(
                                    text = activity.action,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = activity.description,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MUTED_TEXT
                                )

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Person, contentDescription = null, tint = MUTED_TEXT, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(activity.user, fontSize = 12.sp, color = MUTED_TEXT)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = MUTED_TEXT, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(activity.timestamp, fontSize = 12.sp, color = MUTED_TEXT)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { /* Load More */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DARK_GREEN)
                    ) {
                        Text(
                            text = "Load More Activity",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/** FIXED → all icons green & non-deprecated */
private fun getTypeIcon(type: String) = when (type) {
    "document" -> Icons.Filled.FilePresent
    "hearing" -> Icons.Filled.CalendarToday
    "update" -> Icons.Filled.Edit
    "note" -> Icons.Filled.Edit
    "message" -> Icons.AutoMirrored.Filled.Message   // FIXED
    "task" -> Icons.Filled.CheckCircle
    else -> Icons.Filled.Edit
}
