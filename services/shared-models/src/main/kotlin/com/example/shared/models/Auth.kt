package com.example.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val email: String,
    val displayName: String? = null
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val phoneNumber: String? = null
)

