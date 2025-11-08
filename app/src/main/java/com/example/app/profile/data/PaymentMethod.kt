package com.example.app.profile.data

data class PaymentMethod(
    val id: String = "",
    val cardNumber: String = "",
    val cardholderName: String = "",
    val expiryDate: String = "", // MM/YY format
    val cvv: String = "",
    val isDefault: Boolean = false
)

