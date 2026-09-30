package com.example.data.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystemPrompt: Boolean = false,
    val attachedCartPreview: Boolean = false,
    val actionButtonText: String? = null
)
