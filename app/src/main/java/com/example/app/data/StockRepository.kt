package com.example.app.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object StockRepository {
    private val db = FirebaseFirestore.getInstance()
    
    /**
     * Decrease stock for a product by the specified quantity
     */
    suspend fun decreaseStock(productId: String, quantity: Int): Result<Unit> {
        return try {
            val productRef = db.collection("products").document(productId)
            
            // Use Firestore transaction to safely decrease stock
            db.runTransaction { transaction ->
                val snapshot = transaction.get(productRef)
                val currentStock = (snapshot.get("stock") as? Number)?.toInt() ?: 0
                val newStock = (currentStock - quantity).coerceAtLeast(0) // Don't go below 0
                
                transaction.update(productRef, "stock", newStock)
                newStock
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Decrease stock for multiple products (used when placing an order)
     */
    suspend fun decreaseStockForOrder(items: List<com.example.app.orders.data.OrderItem>): Result<Unit> {
        return try {
            // Use individual transactions for each product to safely decrease stock
            items.forEach { item ->
                val result = decreaseStock(item.productId, item.quantity)
                result.onFailure {
                    println("Error decreasing stock for product ${item.productId}: ${it.message}")
                    // Continue with other products even if one fails
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

