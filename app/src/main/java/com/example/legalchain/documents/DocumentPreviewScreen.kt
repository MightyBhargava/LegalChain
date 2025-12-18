package com.example.legalchain.documents

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.compose.foundation.Image
import java.io.File
import android.graphics.BitmapFactory
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentPreviewScreen(
    docId: String,
    documentName: String,
    docType: String,
    pages: Int,
    size: String,
    category: String,
    relatedCase: String,
    uploadedAt: String,
    tags: List<String>,
    fileName: String,
    onBack: () -> Unit,
    onViewOCR: () -> Unit,
    onDelete: () -> Unit,
    onOpenShare: () -> Unit,
    onAddTag: (List<String>) -> Unit = {}
) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    
    // Tag management state
    var currentTags by remember { mutableStateOf(tags) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    
    // Update currentTags when tags prop changes
    LaunchedEffect(tags) {
        currentTags = tags
    }
    
    fun addTag() {
        val tag = tagInput.trim()
        if (tag.isNotEmpty() && !currentTags.contains(tag)) {
            val updatedTags = currentTags + tag
            currentTags = updatedTags
            onAddTag(updatedTags)
            tagInput = ""
            showAddTagDialog = false
        }
    }
    
    fun removeTag(tag: String) {
        val updatedTags = currentTags - tag
        currentTags = updatedTags
        onAddTag(updatedTags)
    }

    val DARK_GREEN = Color(0xFF0B5D2E)
    val LIGHT_GREEN = Color(0xFFE6F4EA)
    val TEXT_BLACK = Color(0xFF111111)

    val file = File(context.filesDir, fileName)
    LaunchedEffect(fileName) {
        if (docType.equals("image", true) && file.exists()) {
            runCatching { BitmapFactory.decodeFile(file.absolutePath) }.onSuccess {
                previewBitmap = it
            }
        } else {
            previewBitmap = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Document Preview", fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenShare) {
                        Icon(Icons.Filled.Share, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DARK_GREEN
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFFF6F7F9))
        ) {

            /* ================= DOCUMENT PREVIEW ================= */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F2937))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val bmp = previewBitmap
                            if (docType.equals("image", true) && bmp != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f, fill = false),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = "Document image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer(
                                                scaleX = scale,
                                                scaleY = scale
                                            )
                                    )

                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IconButton(
                                            onClick = { scale = (scale - 0.2f).coerceIn(0.5f, 5f) },
                                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
                                        ) {
                                            Icon(Icons.Filled.ZoomOut, null, tint = Color.Gray)
                                        }
                                        IconButton(
                                            onClick = { scale = (scale + 0.2f).coerceIn(0.5f, 5f) },
                                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
                                        ) {
                                            Icon(Icons.Filled.ZoomIn, null, tint = Color.Gray)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            } else {
                                Icon(
                                    Icons.Filled.Description,
                                    null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                            }

                            Text(
                                documentName,
                                fontWeight = FontWeight.Bold,
                                color = TEXT_BLACK,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "$pages pages • $size",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

    Spacer(Modifier.height(16.dp))

            /* ================= DOWNLOAD / SHARE ================= */
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { downloadPdfToDownloads(context, file, documentName) }
                ) {
                    Icon(Icons.Filled.Download, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Download", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenShare
                ) {
                    Icon(Icons.Filled.Share, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Share", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ================= OCR ================= */
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .clickable { onViewOCR() },
                colors = CardDefaults.cardColors(containerColor = LIGHT_GREEN),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(DARK_GREEN, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.AutoAwesome, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("View OCR Extraction", fontWeight = FontWeight.Bold)
                        Text(
                            "AI-extracted text from this document",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ================= DOCUMENT DETAILS ================= */
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Document Details", fontWeight = FontWeight.Bold, color = TEXT_BLACK)
                    DetailRow(Icons.Filled.Folder, "Category", category)
                    DetailRow(Icons.Filled.Work, "Related Case", relatedCase)
                    DetailRow(Icons.Filled.CalendarMonth, "Uploaded", uploadedAt)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ================= TAGS ================= */
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tags", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showAddTagDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Tag")
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (currentTags.isEmpty()) {
                        Text(
                            "No tags added",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(currentTags) { tag ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(tag) },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Remove tag",
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { removeTag(tag) }
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = DARK_GREEN
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            /* ================= DELETE ================= */
            TextButton(
                onClick = {
                    // Delegate navigation to the caller so it can decide where to go after delete
                    onDelete()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Filled.Delete, null, tint = Color.Red)
                Spacer(Modifier.width(6.dp))
                Text("Delete Document", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
    
    // Add Tag Dialog
    if (showAddTagDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddTagDialog = false
                tagInput = ""
            },
            title = { Text("Add Tag", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tagInput,
                        onValueChange = { tagInput = it },
                        placeholder = { Text("Enter tag name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { addTag() }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { addTag() },
                    enabled = tagInput.trim().isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddTagDialog = false
                    tagInput = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* ---------------- DOWNLOAD TO DOWNLOADS ---------------- */

private fun downloadPdfToDownloads(
    context: Context,
    sourceFile: File,
    fileName: String
) {
    if (!sourceFile.exists()) return

    // Get file extension and mime type
    val extension = sourceFile.extension.lowercase()
    val mimeType = when (extension) {
        "pdf" -> "application/pdf"
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        else -> "application/octet-stream"
    }
    
    val displayName = if (fileName.contains(".")) fileName else "$fileName.$extension"

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Download")
    }

    val uri = resolver.insert(
        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
        contentValues
    ) ?: return

    resolver.openOutputStream(uri)?.use { output ->
        sourceFile.inputStream().use { input ->
            input.copyTo(output)
        }
    }
}

/* ---------------- SHARE FILE ---------------- */

private fun shareFile(context: Context, file: File) {
    if (!file.exists()) return

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share Document"))
}

/* ---------------- DETAIL ROW ---------------- */

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
