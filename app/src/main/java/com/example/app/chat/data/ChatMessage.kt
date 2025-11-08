package com.example.app.chat.data

data class ChatMessage(
    val text: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageRole {
    USER,
    ASSISTANT
}

