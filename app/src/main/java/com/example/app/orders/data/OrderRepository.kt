package com.example.app.orders.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
    val color: String? = null
)

object OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    private fun getOrdersCollection() = db.collection("orders")
    
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
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val orderData = hashMapOf(
                "userId" to userId,
                "orderNumber" to orderNumber,
                "itemCount" to items.size,
                "status" to status,
                "total" to total,
                "customerName" to customerName,
                "customerEmail" to customerEmail,
                "shippingAddress" to shippingAddress,
                "shippingPhone" to shippingPhone,
                "createdAt" to System.currentTimeMillis(),
                "items" to items.map { item ->
                    hashMapOf(
                        "productId" to item.productId,
                        "productName" to item.productName,
                        "quantity" to item.quantity,
                        "price" to item.price,
                        "size" to (item.size ?: ""),
                        "color" to (item.color ?: "")
                    )
                }
            )
            
            val docRef = getOrdersCollection().add(orderData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrders(): Result<List<Order>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            
            // Try with orderBy first, if it fails (needs index), try without orderBy
            val snapshot = try {
                getOrdersCollection()
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
            } catch (e: Exception) {
                // If orderBy fails (needs composite index), get without orderBy and sort in memory
                val unsortedSnapshot = getOrdersCollection()
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                unsortedSnapshot
            }
            
            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    val items = (data["items"] as? List<*>)?.mapNotNull { itemMap ->
                        val item = itemMap as? Map<*, *> ?: return@mapNotNull null
                        OrderItem(
                            productId = item["productId"] as? String ?: "",
                            productName = item["productName"] as? String ?: "",
                            quantity = (item["quantity"] as? Number)?.toInt() ?: 0,
                            price = (item["price"] as? Number)?.toDouble() ?: 0.0,
                            size = item["size"] as? String,
                            color = item["color"] as? String
                        )
                    } ?: emptyList()
                    
                    Order(
                        id = doc.id,
                        orderNumber = data["orderNumber"] as? String ?: "",
                        itemCount = (data["itemCount"] as? Number)?.toInt() ?: 0,
                        status = data["status"] as? String ?: "Processing",
                        total = (data["total"] as? Number)?.toDouble() ?: 0.0,
                        createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        items = items,
                        customerName = data["customerName"] as? String ?: "",
                        customerEmail = data["customerEmail"] as? String ?: "",
                        shippingAddress = data["shippingAddress"] as? String ?: "",
                        shippingPhone = data["shippingPhone"] as? String ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            // Sort by createdAt descending if we didn't use orderBy in query
            val sortedOrders = orders.sortedByDescending { it.createdAt }
            
            Result.success(sortedOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            getOrdersCollection()
                .document(orderId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun generateOrderNumber(): String {
        return "ORD${System.currentTimeMillis().toString().takeLast(8)}"
    }
}

