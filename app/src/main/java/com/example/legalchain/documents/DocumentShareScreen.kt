package com.example.legalchain.documents

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Kotlin version of the React DocumentShareScreen.
 * Focuses on UI + basic Android share / copy interactions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentShareScreen(
    docId: String,
    documentName: String,
    size: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var copied by remember { mutableStateOf(false) }
    var requirePassword by remember { mutableStateOf(false) }
    var setExpiry by remember { mutableStateOf(false) }

    // In a real backend this would be generated; for now we simulate a deep link
    val shareLink = "https://legalchain.app/doc/$docId"

    fun handleCopy() {
        clipboard.setText(AnnotatedString(shareLink))
        copied = true
        // simple local timer using LaunchedEffect
    }

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(2000)
            copied = false
        }
    }

    fun sharePlainText(via: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Shared document: $documentName")
            putExtra(Intent.EXTRA_TEXT, shareLink)
        }
        val chooser = Intent.createChooser(sendIntent, "Share via $via")
        context.startActivity(chooser)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Share Document",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Document info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Sharing", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text(documentName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("$size • PDF Document", fontSize = 12.sp, color = Color.Gray)
                }
            }

            // Share options
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Share via", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ShareOption(
                            icon = Icons.Filled.Email,
                            label = "Email"
                        ) { sharePlainText("Email") }

                        ShareOption(
                            icon = Icons.AutoMirrored.Filled.Message,
                            label = "WhatsApp"
                        ) { sharePlainText("WhatsApp") }

                        ShareOption(
                            icon = Icons.Filled.MoreHoriz,
                            label = "More"
                        ) { sharePlainText("More") }

                        ShareOption(
                            icon = Icons.Filled.Download,
                            label = "Download"
                        ) {
                            // download handled from preview screen; here we just reuse share link
                            sharePlainText("Download")
                        }
                    }
                }
            }

            // Share link
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Share Link", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Link, contentDescription = null, tint = Color.Gray)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                shareLink,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        FilledTonalButton(onClick = { handleCopy() }) {
                            if (copied) {
                                Icon(Icons.Filled.Check, contentDescription = "Copied")
                            } else {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy")
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Anyone with this link can view the document",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Access settings
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Access Settings", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

                    SettingRow(
                        title = "Require password",
                        subtitle = "Add extra security",
                        checked = requirePassword,
                        onCheckedChange = { requirePassword = it }
                    )
                    SettingRow(
                        title = "Set expiry date",
                        subtitle = "Link expires after date",
                        checked = setExpiry,
                        onCheckedChange = { setExpiry = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFF3F4F6), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color(0xFF2563EB))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = Color(0xFF4B5563))
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}


