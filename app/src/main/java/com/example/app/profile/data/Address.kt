package com.example.app.profile.data

data class Address(
    val id: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false
)

