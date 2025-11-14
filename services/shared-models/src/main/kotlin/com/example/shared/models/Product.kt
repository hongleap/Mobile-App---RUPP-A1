package com.example.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val images: List<String> = emptyList(),
    val description: String = "",
    val sizes: List<String> = emptyList(),
    val colors: List<ProductColor> = emptyList(),
    val category: String = "",
    val gender: String = "",
    val onSale: Boolean = false,
    val freeShipping: Boolean = false,
    val stock: Int = 0
)

@Serializable
data class ProductColor(
    val name: String = "",
    val colorValue: String = "" // Hex color code as string
)

