@file:OptIn(ExperimentalMaterial3Api::class)

package com.simats.legalchain.profile

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- COLORS ---------- */
private val PAGE_BG = Color(0xFFF5F6F7)
private val DARK_GREEN = Color(0xFF0B5D2E)
private val MUTED = Color(0xFF6B7280)

/* ---------- DATA MODEL ---------- */
data class HistoryItem(
    val id: String,
    val type: String,
    val title: String,
    val date: String,
    val amount: String,
    val status: String,
    val downloadUrl: String
)

/* ---------- SAMPLE DATA ---------- */
private val historyList = listOf(
    HistoryItem(
        id = "1",
        type = "Consultation",
        title = "Adv. Priya Sharma",
        date = "Dec 10, 2024",
        amount = "₹2,000",
        status = "completed",
        downloadUrl = "https://yourdomain.com/files/download.php?file=receipt_1.pdf"
    ),
    HistoryItem(
        id = "2",
        type = "Subscription",
        title = "Pro Plan (Monthly)",
        date = "Dec 1, 2024",
        amount = "₹999",
        status = "active",
        downloadUrl = "https://yourdomain.com/files/download.php?file=invoice_3.pdf"
    ),
    HistoryItem(
        id = "3",
        type = "Consultation",
        title = "Adv. Amit Patel",
        date = "Nov 15, 2024",
        amount = "₹1,500",
        status = "completed",
        downloadUrl = "https://yourdomain.com/files/download.php?file=receipt_2.pdf"
    )
)

@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    fun downloadFile(url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading file...")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order History",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black
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

            historyList.forEach { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        /* ---------- TOP ROW ---------- */
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = item.type,
                                    fontSize = 12.sp,
                                    color = MUTED
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }

                            StatusChip(status = item.status)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        /* ✅ FIXED HERE */
                        HorizontalDivider(color = Color(0xFFE5E7EB))

                        Spacer(modifier = Modifier.height(12.dp))

                        /* ---------- BOTTOM ROW ---------- */
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = DARK_GREEN,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.date,
                                    fontSize = 13.sp,
                                    color = MUTED
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.amount,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = {
                                        downloadFile(
                                            item.downloadUrl,
                                            "receipt_${item.id}.pdf"
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Download,
                                        contentDescription = "Download",
                                        tint = DARK_GREEN
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- STATUS CHIP ---------- */
@Composable
private fun StatusChip(status: String) {
    val bgColor = if (status == "active") {
        Color(0xFFDCFCE7)
    } else {
        Color(0xFFE5E7EB)
    }

    val textColor = if (status == "active") {
        Color(0xFF166534)
    } else {
        Color(0xFF374151)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
