package com.example.app.api

import com.example.app.orders.data.Order
import com.example.app.orders.data.OrderItem
import com.example.app.notifications.data.Notification
import com.example.app.product.Product
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // Android emulator localhost
    // For physical device, use your computer's IP: "http://192.168.1.XXX:8080"
    // For production, use your deployed API Gateway URL
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val auth = FirebaseAuth.getInstance()
    
    private suspend fun getAuthToken(): String? {
        return auth.currentUser?.getIdToken(false)?.await()?.token
    }
    
    private suspend fun makeRequest(
        url: String,
        method: String = "GET",
        body: String? = null,
        requiresAuth: Boolean = false
    ): String {
        return withContext(Dispatchers.IO) {
            val requestBuilder = Request.Builder()
                .url(url)
            
            if (requiresAuth) {
                val token = getAuthToken()
                    ?: throw Exception("User not authenticated")
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            when (method.uppercase()) {
                "GET" -> requestBuilder.get()
                "POST" -> {
                    if (body != null) {
                        requestBuilder.post(body.toRequestBody("application/json".toMediaType()))
                    } else {
                        requestBuilder.post("{}".toRequestBody("application/json".toMediaType()))
                    }
                }
                "PUT" -> {
                    if (body != null) {
                        requestBuilder.put(body.toRequestBody("application/json".toMediaType()))
                    } else {
                        requestBuilder.put("{}".toRequestBody("application/json".toMediaType()))
                    }
                }
                "DELETE" -> requestBuilder.delete()
                else -> requestBuilder.get()
            }
            
            val request = requestBuilder
                .addHeader("Content-Type", "application/json")
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            
            if (!response.isSuccessful) {
                val error = try {
                    JSONObject(responseBody).getString("error")
                } catch (e: Exception) {
                    "Request failed: ${response.code}"
                }
                throw Exception(error)
            }
            
            responseBody
        }
    }
    
    /**
     * Public method for making authenticated API requests
     * Used by other API clients (e.g., TransactionApi)
     */
    suspend fun makeAuthenticatedRequest(
        endpoint: String,
        method: String = "GET",
        body: String? = null
    ): String {
        return makeRequest(
            url = "$BASE_URL$endpoint",
            method = method,
            body = body,
            requiresAuth = true
        )
    }
    
    // Product API
    suspend fun getProducts(): List<Product> {
        val response = makeRequest("$BASE_URL/api/products")
        val json = JSONObject(response)
        
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to fetch products"))
        }
        
        val productsArray = json.getJSONArray("data")
        val products = mutableListOf<Product>()
        
        for (i in 0 until productsArray.length()) {
            val productObj = productsArray.getJSONObject(i)
            products.add(parseProduct(productObj))
        }
        
        return products
    }
    
    suspend fun getProduct(id: String): Product {
        val response = makeRequest("$BASE_URL/api/products/$id")
        val json = JSONObject(response)
        
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to fetch product"))
        }
        
        val productObj = json.getJSONObject("data")
        return parseProduct(productObj)
    }
    
    suspend fun getProductsByCategory(category: String): List<Product> {
        val response = makeRequest("$BASE_URL/api/products/category/$category")
        val json = JSONObject(response)
        
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to fetch products"))
        }
        
        val productsArray = json.getJSONArray("data")
        val products = mutableListOf<Product>()
        
        for (i in 0 until productsArray.length()) {
            val productObj = productsArray.getJSONObject(i)
            products.add(parseProduct(productObj))
        }
        
        return products
    }
    
    suspend fun decreaseStock(productId: String, quantity: Int) {
        val body = JSONObject().apply {
            put("productId", productId)
            put("quantity", quantity)
        }.toString()
        
        val response = makeAuthenticatedRequest("/api/stock/decrease", "POST", body)
        val json = JSONObject(response)
        
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to decrease stock"))
        }
    }
    
    private fun parseProduct(productObj: JSONObject): Product {
        val imagesArray = productObj.optJSONArray("images")
        val images = mutableListOf<String>()
        if (imagesArray != null) {
            for (j in 0 until imagesArray.length()) {
                images.add(imagesArray.getString(j))
            }
        }
        
        val sizesArray = productObj.optJSONArray("sizes")
        val sizes = mutableListOf<String>()
        if (sizesArray != null) {
            for (j in 0 until sizesArray.length()) {
                sizes.add(sizesArray.getString(j))
            }
        }
        
        val colorsArray = productObj.optJSONArray("colors")
        val colors = mutableListOf<com.example.app.product.ProductColor>()
        if (colorsArray != null) {
            for (j in 0 until colorsArray.length()) {
                val colorObj = colorsArray.getJSONObject(j)
                val colorValue = colorObj.getString("colorValue")
                try {
                    val colorInt = android.graphics.Color.parseColor(colorValue)
                    colors.add(com.example.app.product.ProductColor(
                        colorObj.getString("name"),
                        androidx.compose.ui.graphics.Color(colorInt)
                    ))
                } catch (e: Exception) {
                    // Skip invalid colors
                }
            }
        }
        
        return Product(
            id = productObj.getString("id"),
            name = productObj.getString("name"),
            price = productObj.getString("price"),
            images = images,
            description = productObj.optString("description", ""),
            sizes = sizes,
            colors = colors,
            category = productObj.optString("category", ""),
            gender = productObj.optString("gender", ""),
            onSale = productObj.optBoolean("onSale", false),
            freeShipping = productObj.optBoolean("freeShipping", false),
            stock = productObj.optInt("stock", 0)
        )
    }
    
    // Order API
    suspend fun getOrders(): List<Order> {
        val response = makeRequest("$BASE_URL/api/orders", requiresAuth = true)
        val json = JSONObject(response)
        
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to fetch orders"))
        }
        
        val ordersArray = json.getJSONArray("data")
        val orders = mutableListOf<Order>()
        
        for (i in 0 until ordersArray.length()) {
            val orderObj = ordersArray.getJSONObject(i)
            orders.add(parseOrder(orderObj))
        }
        
        return orders
    }
    
    suspend fun getOrder(id: String): Order {
        val response = makeRequest("$BASE_URL/api/orders/$id", requiresAuth = true)
        val json = JSONObject(response)
        
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to fetch order"))
        }
        
        val orderObj = json.getJSONObject("data")
        return parseOrder(orderObj)
    }
    
    suspend fun createOrder(
        items: List<OrderItem>,
        customerName: String,
        customerEmail: String,
        shippingAddress: String,
        shippingPhone: String
    ): Order {
        val orderRequest = JSONObject().apply {
            put("items", JSONArray().apply {
                items.forEach { item ->
                    put(JSONObject().apply {
                        put("productId", item.productId)
                        put("productName", item.productName)
                        put("quantity", item.quantity)
                        put("price", item.price)
                        put("size", item.size ?: "")
                        put("color", item.color ?: "")
                    })
                }
            })
            put("customerName", customerName)
            put("customerEmail", customerEmail)
            put("shippingAddress", shippingAddress)
            put("shippingPhone", shippingPhone)
        }
        
        val response = makeRequest(
            "$BASE_URL/api/orders",
            method = "POST",
            body = orderRequest.toString(),
            requiresAuth = true
        )
        
        val json = JSONObject(response)
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to create order"))
        }
        
        val orderObj = json.getJSONObject("data")
        return parseOrder(orderObj)
    }
    
    private fun parseOrder(orderObj: JSONObject): Order {
        val itemsArray = orderObj.optJSONArray("items")
        val items = mutableListOf<OrderItem>()
        if (itemsArray != null) {
            for (j in 0 until itemsArray.length()) {
                val itemObj = itemsArray.getJSONObject(j)
                items.add(OrderItem(
                    productId = itemObj.getString("productId"),
                    productName = itemObj.getString("productName"),
                    quantity = itemObj.getInt("quantity"),
                    price = itemObj.getDouble("price"),
                    size = itemObj.optString("size").takeIf { it.isNotEmpty() },
                    color = itemObj.optString("color").takeIf { it.isNotEmpty() }
                ))
            }
        }
        
        return Order(
            id = orderObj.getString("id"),
            orderNumber = orderObj.getString("orderNumber"),
            itemCount = orderObj.getInt("itemCount"),
            status = orderObj.getString("status"),
            total = orderObj.getDouble("total"),
            createdAt = orderObj.getLong("createdAt"),
            items = items,
            customerName = orderObj.optString("customerName", ""),
            customerEmail = orderObj.optString("customerEmail", ""),
            shippingAddress = orderObj.optString("shippingAddress", ""),
            shippingPhone = orderObj.optString("shippingPhone", "")
        )
    }
    
    // Notification API
    suspend fun getNotifications(): List<Notification> {
        val response = makeRequest("$BASE_URL/api/notifications", requiresAuth = true)
        val json = JSONObject(response)
        
        if (!json.getBoolean("success")) {
            throw Exception(json.optString("error", "Failed to fetch notifications"))
        }
        
        val notificationsArray = json.getJSONArray("data")
        val notifications = mutableListOf<Notification>()
        
        for (i in 0 until notificationsArray.length()) {
            val notifObj = notificationsArray.getJSONObject(i)
            notifications.add(Notification(
                id = notifObj.getString("id"),
                message = notifObj.getString("message"),
                isRead = notifObj.getBoolean("isRead"),
                createdAt = notifObj.getLong("createdAt"),
                type = notifObj.optString("type", "order")
            ))
        }
        
        return notifications
    }
    
    suspend fun markNotificationAsRead(id: String) {
        makeRequest(
            "$BASE_URL/api/notifications/$id/read",
            method = "PUT",
            requiresAuth = true
        )
    }
    
    suspend fun markAllNotificationsAsRead() {
        makeRequest(
            "$BASE_URL/api/notifications/mark-all-read",
            method = "PUT",
            requiresAuth = true
        )
    }
    suspend fun updateOrderStatus(id: String, status: String) {
        val body = JSONObject().apply {
            put("status", status)
        }.toString()
        
        makeRequest(
            "$BASE_URL/api/orders/$id/status",
            method = "PUT",
            body = body,
            requiresAuth = true
        )
    }
    
    suspend fun createNotification(message: String, type: String = "order") {
        val body = JSONObject().apply {
            put("message", message)
            put("type", type)
        }.toString()
        
        makeRequest(
            "$BASE_URL/api/notifications",
            method = "POST",
            body = body,
            requiresAuth = true
        )
    }
}

