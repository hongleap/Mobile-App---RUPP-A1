package com.example.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val userId: String = "",
    val orderNumber: String = "",
    val itemCount: Int = 0,
    val status: String = "Processing",
    val total: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val items: List<OrderItem> = emptyList(),
    val customerName: String = "",
    val customerEmail: String = "",
    val shippingAddress: String = "",
    val shippingPhone: String = ""
)

@Serializable
data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val size: String? = null,
    val color: String? = null
)

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItem>,
    val customerName: String,
    val customerEmail: String,
    val shippingAddress: String,
    val shippingPhone: String
)

