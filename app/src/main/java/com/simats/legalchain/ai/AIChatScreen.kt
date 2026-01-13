@file:OptIn(ExperimentalMaterial3Api::class)

package com.simats.legalchain.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.simats.legalchain.network.ApiClient
import com.simats.legalchain.network.AIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun AIChatScreen(onBack: () -> Unit = {}) {

    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var loading by remember { mutableStateOf(false) }

    fun sendMessage() {
        val question = input.trim()
        if (question.isEmpty()) return

        messages.add(ChatMessage(question, true))
        input = ""
        loading = true

        ApiClient.apiService.askAI(
            mapOf("question" to question)
        ).enqueue(object : Callback<AIResponse> {

            override fun onResponse(
                call: Call<AIResponse>,
                response: Response<AIResponse>
            ) {
                loading = false
                val body = response.body()

                if (response.isSuccessful && body != null && body.success && !body.reply.isNullOrEmpty()) {
                    messages.add(ChatMessage(body.reply, false))
                } else {
                    messages.add(ChatMessage("AI did not respond. Please try again.", false))
                }
            }

            override fun onFailure(call: Call<AIResponse>, t: Throwable) {
                loading = false
                messages.add(ChatMessage("Network error. Check internet.", false))
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
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
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color =
                                if (msg.isUser)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask a legal question…") },
                    singleLine = true
                )

                IconButton(onClick = { sendMessage() }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}
