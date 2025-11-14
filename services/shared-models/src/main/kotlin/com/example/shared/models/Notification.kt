package com.example.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String = "",
    val userId: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val type: String = "order" // order, promotion, system, etc.
)

@Serializable
data class CreateNotificationRequest(
    val userId: String,
    val message: String,
    val type: String = "order"
)

