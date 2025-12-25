package com.example.app.data.model

data class Banner(
    val id: String,
    val title: String,
    val subtitle: String?,
    val discount: String?,
    val originalPrice: String?,
    val imageUrl: String?,
    val productId: String?,
    val isActive: Boolean
)
