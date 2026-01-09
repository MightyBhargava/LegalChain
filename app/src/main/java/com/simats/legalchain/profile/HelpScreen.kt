@file:OptIn(ExperimentalMaterial3Api::class)

package com.simats.legalchain.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- COLORS ---------- */
private val PAGE_BG = Color(0xFFF5F6F7)
private val TEXT_BLACK = Color.Black
private val ICON_GREEN = Color(0xFF0B5D2E)

/* ---------- FAQ DATA ---------- */
private val faqData = listOf(
    "How do I add a new case?" to "Go to Cases → Add Case → Fill details → Save.",
    "Is my data secure?" to "Yes, your data is encrypted and securely stored.",
    "How can I contact support?" to "Use Call or Email support cards above.",
    "Can I export case reports?" to "Export options are available in case details.",
    "How do I upload documents?" to "Documents → Upload → Select file.",
    "Can I edit my profile?" to "Profile → Edit Profile → Save changes.",
    "Is dark mode supported?" to "Enable it from Settings.",
    "Can I delete documents?" to "Open document → Delete option."
)

/* ---------- LONG USER GUIDE TEXT ---------- */
private val userGuideText = """
LegalChain is a complete legal case management platform designed to simplify complex legal workflows.
It allows users to manage cases, documents, hearings, and insights in one place.

Users can securely upload and organize documents, track case progress, and receive AI-powered insights.
Notifications help ensure important events and deadlines are never missed.

The platform supports both clients and lawyers with role-based features and enhanced security.
Data encryption and secure storage ensure confidentiality at all times.

LegalChain reduces paperwork, improves efficiency, and brings clarity to the legal journey.
""".repeat(6)

@Composable
fun HelpScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var expandedFaq by remember { mutableStateOf<Int?>(null) }
    var showGuide by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Help & Support",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = TEXT_BLACK
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            tint = TEXT_BLACK
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* ---------- CALL & EMAIL ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SupportCard(
                    modifier = Modifier
                        .weight(1f),
                        // 🔹 slight right shift
                    icon = Icons.Default.Phone,
                    title = "Call Support",
                    subtitle = "9652062144"
                ) {
                    context.startActivity(
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:9652062144"))
                    )
                }

                SupportCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Email,
                    title = "Email Support",
                    subtitle = "ravulavenkat759@gmail.com"
                ) {
                    context.startActivity(
                        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ravulavenkat759@gmail.com"))
                    )
                }
            }

            /* ---------- FAQ ---------- */
            Text(
                text = "Frequently Asked Questions",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TEXT_BLACK
            )

            faqData.forEachIndexed { index, (question, answer) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedFaq = if (expandedFaq == index) null else index
                        },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                question,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TEXT_BLACK,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (expandedFaq == index)
                                    Icons.Default.ExpandLess
                                else Icons.Default.ExpandMore,
                                null,
                                tint = ICON_GREEN
                            )
                        }

                        if (expandedFaq == index) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                answer,
                                fontSize = 15.sp,
                                color = TEXT_BLACK
                            )
                        }
                    }
                }
            }

            /* ---------- USER GUIDE ---------- */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showGuide = !showGuide },
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (showGuide) Arrangement.End else Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.MenuBook,
                            null,
                            tint = ICON_GREEN,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "User Guide",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = TEXT_BLACK,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    if (showGuide) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            userGuideText,
                            fontSize = 15.sp,
                            color = TEXT_BLACK
                        )
                    }
                }
            }
        }
    }
}

/* ---------- SUPPORT CARD ---------- */
@Composable
private fun SupportCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()          // ⭐ IMPORTANT
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center   // ⭐ IMPORTANT
        ) {

            Icon(
                icon,
                contentDescription = null,
                tint = ICON_GREEN,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TEXT_BLACK
            )

            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = TEXT_BLACK
            )

        }
    }
}
