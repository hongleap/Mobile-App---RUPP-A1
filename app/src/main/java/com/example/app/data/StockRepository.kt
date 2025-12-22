package com.example.app.data



object StockRepository {
    
    /**
     * Decrease stock for a product by the specified quantity
     */
    suspend fun decreaseStock(productId: String, quantity: Int): Result<Unit> {
        return try {
            com.example.app.api.ApiClient.decreaseStock(productId, quantity)
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

