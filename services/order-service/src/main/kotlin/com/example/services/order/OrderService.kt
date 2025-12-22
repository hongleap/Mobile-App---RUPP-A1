package com.example.services.order

import com.example.shared.models.*
import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level
import java.io.File

fun main() {
    // Initialize Firebase Admin SDK
    val serviceAccount = File("firebase-service-account.json")
    if (serviceAccount.exists()) {
        try {
            val options = com.google.firebase.FirebaseOptions.Builder()
                .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount.inputStream()))
                .build()
            FirebaseApp.initializeApp(options)
        } catch (e: Exception) {
            println("WARNING: Failed to initialize Firebase: ${e.message}")
        }
    } else {
        println("WARNING: firebase-service-account.json not found. Firebase operations will fail.")
    }
    
    embeddedServer(Netty, port = 8082, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Nothing>(success = false, error = cause.message ?: "Internal server error")
            )
        }
    }
    
    install(CallLogging) {
        level = Level.INFO
    }
    
    val firestore = try {
        FirestoreClient.getFirestore()
    } catch (e: Exception) {
        println("WARNING: Could not initialize Firestore: ${e.message}")
        null
    }
    
    // HTTP client for communicating with other services
    val httpClient = HttpClient(OkHttp) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }
    
    val productServiceUrl = System.getenv("PRODUCT_SERVICE_URL") ?: "http://product-service:8081"
    val notificationServiceUrl = System.getenv("NOTIFICATION_SERVICE_URL") ?: "http://notification-service:8083"
    
    routing {
        route("/api/orders") {
            get {
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                try {
                    val snapshot = firestore?.collection("orders")
                        ?.whereEqualTo("userId", userId)
                        ?.get()?.get()
                    
                    val orders = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val data = doc.data ?: emptyMap<String, Any>()
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
                                userId = userId,
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
                    } ?: emptyList()
                    
                    val sortedOrders = orders.sortedByDescending { it.createdAt }
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = sortedOrders))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch orders")
                    )
                }
            }
            
            get("/{id}") {
                val orderId = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(success = false, error = "Order ID is required")
                )
                
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                try {
                    val doc = firestore?.collection("orders")?.document(orderId)?.get()?.get()
                    if (doc?.exists() == true) {
                        val data = doc.data ?: emptyMap<String, Any>()
                        if (data["userId"] != userId) {
                            return@get call.respond(
                                HttpStatusCode.Forbidden,
                                ApiResponse<Nothing>(success = false, error = "Access denied")
                            )
                        }
                        
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
                        
                        val order = Order(
                            id = doc.id,
                            userId = userId,
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
                        call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = order))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ApiResponse<Nothing>(success = false, error = "Order not found")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch order")
                    )
                }
            }
            
            post {
                val userId = call.request.header("X-User-Id")
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                val request = call.receive<CreateOrderRequest>()
                
                try {
                    val orderNumber = "ORD${System.currentTimeMillis().toString().takeLast(8)}"
                    val total = request.items.sumOf { it.price * it.quantity }
                    
                    // Create order in Firestore
                    val orderData = mapOf<String, Any>(
                        "userId" to userId,
                        "orderNumber" to orderNumber,
                        "itemCount" to request.items.size,
                        "status" to "Processing",
                        "total" to total,
                        "customerName" to request.customerName,
                        "customerEmail" to request.customerEmail,
                        "shippingAddress" to request.shippingAddress,
                        "shippingPhone" to request.shippingPhone,
                        "createdAt" to System.currentTimeMillis(),
                        "items" to request.items.map { item ->
                            mapOf<String, Any>(
                                "productId" to item.productId,
                                "productName" to item.productName,
                                "quantity" to item.quantity,
                                "price" to item.price,
                                "size" to (item.size ?: ""),
                                "color" to (item.color ?: "")
                            )
                        }
                    )
                    
                    val docRef = firestore?.collection("orders")?.add(orderData)?.get()
                    if (docRef == null) {
                        return@post call.respond(
                            HttpStatusCode.InternalServerError,
                            ApiResponse<Nothing>(success = false, error = "Failed to create order")
                        )
                    }
                    
                    // Decrease stock for each product (call Product Service)
                    for (item in request.items) {
                        try {
                            httpClient.post("$productServiceUrl/api/stock/decrease") {
                                contentType(ContentType.Application.Json)
                                setBody(mapOf("productId" to item.productId, "quantity" to item.quantity))
                            }
                        } catch (e: Exception) {
                            println("Warning: Failed to decrease stock for product ${item.productId}: ${e.message}")
                        }
                    }
                    
                    // Create notification (call Notification Service)
                    try {
                        httpClient.post("$notificationServiceUrl/api/notifications") {
                            contentType(ContentType.Application.Json)
                            setBody(CreateNotificationRequest(
                                userId = userId,
                                message = "${request.customerName}, you placed an order. Check your order history for full details.",
                                type = "order"
                            ))
                        }
                    } catch (e: Exception) {
                        println("Warning: Failed to create notification: ${e.message}")
                    }
                    
                    val order = Order(
                        id = docRef.id,
                        userId = userId,
                        orderNumber = orderNumber,
                        itemCount = request.items.size,
                        status = "Processing",
                        total = total,
                        createdAt = System.currentTimeMillis(),
                        items = request.items,
                        customerName = request.customerName,
                        customerEmail = request.customerEmail,
                        shippingAddress = request.shippingAddress,
                        shippingPhone = request.shippingPhone
                    )
                    
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = order))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to create order")
                    )
                }
            }
            
            put("/{id}/status") {
                val orderId = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(success = false, error = "Order ID is required")
                )
                
                val request = call.receive<UpdateOrderStatusRequest>()
                
                try {
                    firestore?.collection("orders")?.document(orderId)?.update("status", request.status)?.get()
                    call.respond(HttpStatusCode.OK, ApiResponse<Nothing>(success = true, message = "Order status updated"))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to update order status")
                    )
                }
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class UpdateOrderStatusRequest(
    val status: String
)

