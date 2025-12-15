package com.example.legalchain.documents

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/* ---------------- COLORS ---------------- */

private val DarkGreen = Color(0xFF004D40)
private val BackgroundColor = Color(0xFFF5F6F7)
private val CardWhite = Color.White
private val BorderGray = Color(0xFFE5E7EB)
private val Amber = Color(0xFFF59E0B)

/* ---------------- DATA ---------------- */

private val categories = listOf("FIR", "Petition", "Evidence", "Notice", "Orders", "Other")
private val cases = listOf(
    "Singh vs. State",
    "Sharma Property",
    "TechCorp Merger",
    "Kumar Divorce"
)

private enum class FileType(val label: String, val mime: Array<String>) {
    IMAGE("Image", arrayOf("image/*")),
    VIDEO("Video", arrayOf("video/*")),
    AUDIO("Audio", arrayOf("audio/*")),
    DOCUMENT(
        "Document",
        arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {}
) {

    var selectedFileType by remember { mutableStateOf<FileType?>(null) }
    var showTypeDialog by remember { mutableStateOf(true) }

    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedCase by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf<String>()) }

    var showCaseDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    /* -------- File Picker -------- */

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { selectedFiles = selectedFiles + it }
    }

    fun pickFile() {
        selectedFileType?.let { picker.launch(it.mime) }
    }

    fun addTag() {
        val tag = tagInput.trim()
        if (tag.isNotEmpty() && !tags.contains(tag)) {
            tags = tags + tag
        }
        tagInput = ""
    }

    fun removeFile(index: Int) {
        selectedFiles = selectedFiles.toMutableList().apply { removeAt(index) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Upload Document",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            /* -------- Upload Section -------- */
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, BorderGray, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.CloudUpload, null, tint = DarkGreen, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))

                        Text(
                            selectedFileType?.let { "Upload ${it.label}" } ?: "Select File Type",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { if (selectedFileType != null) pickFile() },
                            enabled = selectedFileType != null,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Icon(Icons.Filled.Upload, null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                selectedFileType?.let { "Upload ${it.label}" } ?: "Select File Type",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            /* -------- Selected Files -------- */
            if (selectedFiles.isNotEmpty()) {
                itemsIndexed(selectedFiles) { index, uri ->
                    Card {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(uri.lastPathSegment ?: "File", modifier = Modifier.weight(1f))
                            IconButton(onClick = { removeFile(index) }) {
                                Icon(Icons.Filled.Close, null, tint = DarkGreen)
                            }
                        }
                    }
                }
            }

            /* -------- File Details -------- */
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CardWhite)) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("File Details", fontWeight = FontWeight.Bold)

                        FieldButton("Link to Case *", selectedCase.ifEmpty { "Select case" }, selectedCase.isEmpty()) {
                            showCaseDialog = true
                        }

                        FieldButton("Category", category.ifEmpty { "Select category" }) {
                            showCategoryDialog = true
                        }

                        /* -------- TAG INPUT (ICON BASED) -------- */
                        Column {
                            Text("Tags", fontWeight = FontWeight.Medium)

                            OutlinedTextField(
                                value = tagInput,
                                onValueChange = { tagInput = it },
                                placeholder = { Text("Enter tag") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Add Tag",
                                        tint = DarkGreen,
                                        modifier = Modifier
                                            .clickable { addTag() }
                                    )
                                }
                            )

                            if (tags.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(tags) { tag ->
                                        TagChip(tag) {
                                            tags = tags - tag
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /* -------- Actions -------- */
            item {
                Button(
                    onClick = onNavigateToDocuments,
                    enabled = selectedFiles.isNotEmpty() && selectedCase.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("Upload All", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    /* -------- Dialogs -------- */

    if (showTypeDialog) {
        SelectionDialog("Select File Type", FileType.values().map { it.label }) {
            selectedFileType = FileType.values().first { f -> f.label == it }
            showTypeDialog = false
        }
    }

    if (showCaseDialog) {
        SelectionDialog("Select Case", cases) {
            selectedCase = it
            showCaseDialog = false
        }
    }

    if (showCategoryDialog) {
        SelectionDialog("Select Category", categories) {
            category = it
            showCategoryDialog = false
        }
    }
}

/* ---------------- REUSABLE ---------------- */

@Composable
private fun FieldButton(
    label: String,
    value: String,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Column {
        Text(label, fontWeight = FontWeight.Medium)
        OutlinedButton(
            onClick = onClick,
            border = BorderStroke(1.dp, if (highlight) Amber else BorderGray),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(value)
                Icon(Icons.Filled.ExpandMore, null)
            }
        }
    }
}

@Composable
private fun TagChip(text: String, onRemove: () -> Unit) {
    AssistChip(
        onClick = {},
        label = { Text(text, color = Color.White, fontWeight = FontWeight.Medium) },
        trailingIcon = {
            Icon(
                Icons.Filled.Close,
                null,
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() }
            )
        },
        colors = AssistChipDefaults.assistChipColors(containerColor = DarkGreen)
    )
}

@Composable
private fun SelectionDialog(title: String, items: List<String>, onSelect: (String) -> Unit) {
    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Text(title, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                HorizontalDivider()
                items.forEach {
                    TextButton(onClick = { onSelect(it) }, modifier = Modifier.fillMaxWidth()) {
                        Text(it)
                    }
                }
            }
        }
    }
}
