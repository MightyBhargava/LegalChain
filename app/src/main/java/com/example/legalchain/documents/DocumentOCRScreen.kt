package com.example.legalchain.documents

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* ---------------- DATA MODEL ---------------- */
data class OCRData(
    val documentName: String,
    val extractedAt: String,
    val confidence: Int,
    val text: String,
    val filePath: String
)

/* ---------------- VIEWMODEL ---------------- */
class DocumentOCRViewModel(filePath: String) : ViewModel() {

    private val _state = MutableStateFlow(
        OCRData(
            documentName = filePath.substringAfterLast("/"),
            extractedAt = "Dec 10, 2024",
            confidence = 98,
            text = SAMPLE_TEXT,
            filePath = filePath
        )
    )

    val state: StateFlow<OCRData> = _state

    init {
        viewModelScope.launch {
            repeat(6) {
                delay(500)
                _state.value = _state.value.copy(confidence = (95..99).random())
            }
        }
    }
}

/* ---------------- COMPOSABLE ---------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentOCRScreen(
    filePath: String,
    onBack: () -> Unit,
    onOpenFile: (String) -> Unit
) {
    val viewModel = remember { DocumentOCRViewModel(filePath) }
    val ocr by viewModel.state.collectAsState()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val darkGreen = Color(0xFF006400)
    val textColor = if (isDark) MaterialTheme.colorScheme.onBackground else Color.Black

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("OCR Extraction", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = darkGreen
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            /* HEADER */
            Card(
                colors = CardDefaults.cardColors(containerColor = darkGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AutoAwesome, null, tint = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(ocr.documentName, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("AI OCR Extraction", color = Color.White.copy(0.9f))
                    }
                }
            }

            /* CONFIDENCE */
            Card {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CheckCircle, null, tint = darkGreen)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Confidence", fontWeight = FontWeight.Bold)
                        Text("Extracted on ${ocr.extractedAt}")
                    }
                    Text("${ocr.confidence}%", fontWeight = FontWeight.Bold, color = darkGreen)
                }
            }

            /* OCR TEXT */
            Card {
                Column(Modifier.padding(16.dp)) {

                    Row {
                        Text(
                            "Extracted Text",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = {
                            clipboard.setText(AnnotatedString(ocr.text))
                        }) {
                            Icon(Icons.Filled.ContentCopy, null)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            ocr.text,
                            color = textColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            /* ACTIONS */
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        clipboard.setText(AnnotatedString(ocr.text))
                    }
                ) {
                    Icon(Icons.Filled.ContentCopy, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Copy", fontWeight = FontWeight.Bold)
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shareText(context, ocr.text, ocr.documentName)
                    }
                ) {
                    Icon(Icons.Filled.FileDownload, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Export", fontWeight = FontWeight.Bold)
                }
            }

            /* OPEN ORIGINAL FILE */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenFile(ocr.filePath) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Article, null, tint = darkGreen)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("View Original Document", fontWeight = FontWeight.Bold)
                        Text(
                            ocr.documentName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }
        }
    }
}

/* ---------------- SHARE ---------------- */
private fun shareText(context: Context, text: String, subject: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    context.startActivity(Intent.createChooser(intent, "Share OCR"))
}

/* ---------------- SAMPLE TEXT ---------------- */
private const val SAMPLE_TEXT = """
FIRST INFORMATION REPORT (FIR)

Police Station: Andheri
FIR No: 234/2024
Sections: IPC 420, 406

Accused: Vikram Singh
Amount: ₹45,00,000
"""
