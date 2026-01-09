package com.simats.legalchain.network

data class AIResponse(
    val success: Boolean,
    val reply: String?   // ✅ MUST be nullable
)
