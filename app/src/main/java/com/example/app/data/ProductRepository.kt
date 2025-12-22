package com.example.app.data

import com.example.app.product.Product
import com.example.app.search.SearchProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductRepository {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    suspend fun loadProducts() {
        _isLoading.value = true
        try {
            val productList = com.example.app.api.ApiClient.getProducts()
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

