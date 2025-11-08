package com.example.app.data

import com.example.app.firebase.FirestoreHelper
import com.example.app.product.Product
import com.example.app.search.SearchProduct
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object ProductRepository {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    suspend fun loadProducts() {
        _isLoading.value = true
        try {
            // We need to get documents with IDs, so we'll use a different approach
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val result = db.collection("products").get().await()
            
            val productList = result.documents.mapNotNull { documentSnapshot ->
                try {
                    val doc = documentSnapshot.data ?: return@mapNotNull null
                    Product(
                        id = documentSnapshot.id, // Use Firestore document ID
                        name = doc["name"] as? String ?: "",
                        price = doc["priceString"] as? String ?: (doc["price"] as? Double)?.let { "$${String.format("%.2f", it)}" } ?: "$0.00",
                        images = (doc["images"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        description = doc["description"] as? String ?: "",
                        sizes = (doc["sizes"] as? List<*>)?.mapNotNull { it as? String } ?: listOf("S", "M", "L", "XL", "2XL"),
                        colors = (doc["colors"] as? List<*>)?.mapNotNull { colorMap ->
                            val color = colorMap as? Map<*, *>
                            if (color != null) {
                                val name = color["name"] as? String ?: ""
                                val colorValue = color["colorValue"] as? String ?: "#000000"
                                try {
                                    val colorInt = android.graphics.Color.parseColor(colorValue)
                                    com.example.app.product.ProductColor(name, androidx.compose.ui.graphics.Color(colorInt))
                                } catch (e: Exception) {
                                    null
                                }
                            } else null
                        } ?: emptyList(),
                        category = doc["category"] as? String ?: "",
                        gender = doc["gender"] as? String ?: "",
                        onSale = doc["onSale"] as? Boolean ?: false,
                        freeShipping = doc["freeShipping"] as? Boolean ?: false,
                        stock = (doc["stock"] as? Number)?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    println("Error parsing product: ${e.message}")
                    null
                }
            }
            _products.value = productList
        } catch (e: Exception) {
            println("Exception loading products: ${e.message}")
            _products.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }
    
    fun getProductsAsSearchProducts(): List<SearchProduct> {
        return _products.value.map { product ->
            SearchProduct(
                id = product.id,
                name = product.name,
                price = product.price,
                category = product.category,
                gender = product.gender,
                onSale = product.onSale,
                freeShipping = product.freeShipping,
                stock = product.stock
            )
        }
    }
    
    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }
    
    fun getProductsByCategory(category: String): List<Product> {
        return _products.value.filter { it.category.equals(category, ignoreCase = true) }
    }
}

