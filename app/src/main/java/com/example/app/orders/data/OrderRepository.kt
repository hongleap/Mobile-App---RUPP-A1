package com.example.app.orders.data



data class Order(
    val id: String = "",
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

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val size: String? = null,
    val color: String? = null,
    val category: String? = null
)

object OrderRepository {
    
    suspend fun createOrder(
        orderNumber: String,
        items: List<OrderItem>,
        total: Double,
        customerName: String = "",
        customerEmail: String = "",
        shippingAddress: String = "",
        shippingPhone: String = "",
        status: String = "Processing"
    ): Result<String> {
        return try {
            val order = com.example.app.api.ApiClient.createOrder(
                items = items,
                customerName = customerName,
                customerEmail = customerEmail,
                shippingAddress = shippingAddress,
                shippingPhone = shippingPhone
            )
            Result.success(order.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrders(): Result<List<Order>> {
        return try {
            val orders = com.example.app.api.ApiClient.getOrders()
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            com.example.app.api.ApiClient.updateOrderStatus(orderId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun generateOrderNumber(): String {
        return "ORD${System.currentTimeMillis().toString().takeLast(8)}"
    }
}

