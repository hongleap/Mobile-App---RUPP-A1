package com.example.services.product

import com.example.shared.models.*
import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient
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
    
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
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
    
    routing {
        get("/") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "service" to "Product Service",
                    "version" to "1.0.0",
                    "status" to "running",
                    "endpoints" to mapOf(
                        "GET /api/products" to "Get all products",
                        "GET /api/products/{id}" to "Get product by ID",
                        "GET /api/products/category/{category}" to "Get products by category",
                        "POST /api/stock/decrease" to "Decrease product stock"
                    )
                )
            )
        }
        
        route("/api/products") {
            get {
                try {
                    val products = firestore?.collection("products")?.get()?.get()
                        ?.documents
                        ?.mapNotNull { doc ->
                            try {
                                // Get data from DocumentSnapshot using reflection for compatibility
                                @Suppress("UNCHECKED_CAST")
                                val data = try {
                                    doc.javaClass.getMethod("getData").invoke(doc) as? Map<String, Any>
                                } catch (e: Exception) {
                                    // Fallback: try accessing data property directly
                                    try {
                                        doc.javaClass.getField("data").get(doc) as? Map<String, Any>
                                    } catch (e2: Exception) {
                                        null
                                    }
                                } ?: emptyMap<String, Any>()
                                Product(
                                    id = doc.id,
                                    name = data["name"] as? String ?: "",
                                    price = data["priceString"] as? String 
                                        ?: (data["price"] as? Double)?.let { "$${String.format("%.2f", it)}" } 
                                        ?: "$0.00",
                                    images = (data["images"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                    description = data["description"] as? String ?: "",
                                    sizes = (data["sizes"] as? List<*>)?.mapNotNull { it as? String } 
                                        ?: listOf("S", "M", "L", "XL", "2XL"),
                                    colors = (data["colors"] as? List<*>)?.mapNotNull { colorMap ->
                                        val color = colorMap as? Map<*, *>
                                        if (color != null) {
                                            val name = color["name"] as? String ?: ""
                                            val colorValue = color["colorValue"] as? String ?: "#000000"
                                            ProductColor(name, colorValue)
                                        } else null
                                    } ?: emptyList(),
                                    category = data["category"] as? String ?: "",
                                    gender = data["gender"] as? String ?: "",
                                    onSale = data["onSale"] as? Boolean ?: false,
                                    freeShipping = data["freeShipping"] as? Boolean ?: false,
                                    stock = (data["stock"] as? Number)?.toInt() ?: 0
                                )
                            } catch (e: Exception) {
                                null
                            }
                        } ?: emptyList()
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = products))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch products")
                    )
                }
            }
            
            get("/{id}") {
                val productId = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(success = false, error = "Product ID is required")
                )
                
                try {
                    val doc = firestore?.collection("products")?.document(productId)?.get()?.get()
                    if (doc == null) {
                        return@get call.respond(
                            HttpStatusCode.NotFound,
                            ApiResponse<Nothing>(success = false, error = "Product not found")
                        )
                    }
                    
                    // Check if document exists - getData() returns null if document doesn't exist
                    @Suppress("UNCHECKED_CAST")
                    val data = try {
                        doc.javaClass.getMethod("getData").invoke(doc) as? Map<String, Any>
                    } catch (e: Exception) {
                        // Fallback: try accessing data property directly
                        try {
                            doc.javaClass.getField("data").get(doc) as? Map<String, Any>
                        } catch (e2: Exception) {
                            null
                        }
                    }
                    
                    if (data == null) {
                        return@get call.respond(
                            HttpStatusCode.NotFound,
                            ApiResponse<Nothing>(success = false, error = "Product not found")
                        )
                    }
                    
                    val product = Product(
                        id = doc.id,
                        name = data["name"] as? String ?: "",
                        price = data["priceString"] as? String 
                            ?: (data["price"] as? Double)?.let { "$${String.format("%.2f", it)}" } 
                            ?: "$0.00",
                        images = (data["images"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        description = data["description"] as? String ?: "",
                        sizes = (data["sizes"] as? List<*>)?.mapNotNull { it as? String } 
                            ?: listOf("S", "M", "L", "XL", "2XL"),
                        colors = (data["colors"] as? List<*>)?.mapNotNull { colorMap ->
                            val color = colorMap as? Map<*, *>
                            if (color != null) {
                                val name = color["name"] as? String ?: ""
                                val colorValue = color["colorValue"] as? String ?: "#000000"
                                ProductColor(name, colorValue)
                            } else null
                        } ?: emptyList(),
                        category = data["category"] as? String ?: "",
                        gender = data["gender"] as? String ?: "",
                        onSale = data["onSale"] as? Boolean ?: false,
                        freeShipping = data["freeShipping"] as? Boolean ?: false,
                        stock = (data["stock"] as? Number)?.toInt() ?: 0
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = product))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch product")
                    )
                }
            }
            
            get("/category/{category}") {
                val category = call.parameters["category"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(success = false, error = "Category is required")
                )
                
                try {
                    val products = firestore?.collection("products")
                        ?.whereEqualTo("category", category)
                        ?.get()?.get()
                        ?.documents
                        ?.mapNotNull { doc ->
                            try {
                                // Get data from DocumentSnapshot using reflection for compatibility
                                @Suppress("UNCHECKED_CAST")
                                val data = try {
                                    doc.javaClass.getMethod("getData").invoke(doc) as? Map<String, Any>
                                } catch (e: Exception) {
                                    // Fallback: try accessing data property directly
                                    try {
                                        doc.javaClass.getField("data").get(doc) as? Map<String, Any>
                                    } catch (e2: Exception) {
                                        null
                                    }
                                } ?: emptyMap<String, Any>()
                                Product(
                                    id = doc.id,
                                    name = data["name"] as? String ?: "",
                                    price = data["priceString"] as? String 
                                        ?: (data["price"] as? Double)?.let { "$${String.format("%.2f", it)}" } 
                                        ?: "$0.00",
                                    images = (data["images"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                    description = data["description"] as? String ?: "",
                                    sizes = (data["sizes"] as? List<*>)?.mapNotNull { it as? String } 
                                        ?: listOf("S", "M", "L", "XL", "2XL"),
                                    colors = (data["colors"] as? List<*>)?.mapNotNull { colorMap ->
                                        val color = colorMap as? Map<*, *>
                                        if (color != null) {
                                            val name = color["name"] as? String ?: ""
                                            val colorValue = color["colorValue"] as? String ?: "#000000"
                                            ProductColor(name, colorValue)
                                        } else null
                                    } ?: emptyList(),
                                    category = data["category"] as? String ?: "",
                                    gender = data["gender"] as? String ?: "",
                                    onSale = data["onSale"] as? Boolean ?: false,
                                    freeShipping = data["freeShipping"] as? Boolean ?: false,
                                    stock = (data["stock"] as? Number)?.toInt() ?: 0
                                )
                            } catch (e: Exception) {
                                null
                            }
                        } ?: emptyList()
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = products))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch products")
                    )
                }
            }
        }
        
        route("/api/stock") {
            post("/decrease") {
                val request = call.receive<DecreaseStockRequest>()
                
                try {
                    val productRef = firestore?.collection("products")?.document(request.productId)
                    if (productRef == null) {
                        return@post call.respond(
                            HttpStatusCode.InternalServerError,
                            ApiResponse<Nothing>(success = false, error = "Firestore not initialized")
                        )
                    }
                    
                    val newStock = firestore.runTransaction { transaction ->
                        val snapshot = transaction.get(productRef)
                        
                        // Get stock value directly using get() method (similar to Android SDK pattern)
                        @Suppress("UNCHECKED_CAST")
                        val currentStock = try {
                            val getMethod = snapshot.javaClass.getMethod("get", String::class.java)
                            val stockValue = getMethod.invoke(snapshot, "stock")
                            (stockValue as? Number)?.toInt() ?: 0
                        } catch (e: Exception) {
                            // Fallback: try getData() method
                            try {
                                val data = snapshot.javaClass.getMethod("getData").invoke(snapshot) as? Map<String, Any>
                                (data?.get("stock") as? Number)?.toInt() ?: 0
                            } catch (e2: Exception) {
                                throw IllegalStateException("Failed to read product stock: ${e.message}")
                            }
                        }
                        
                        val newStockValue = (currentStock - request.quantity).coerceAtLeast(0)
                        transaction.update(productRef, "stock", newStockValue)
                        newStockValue
                    }.get()
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = mapOf("newStock" to newStock)))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to decrease stock")
                    )
                }
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class DecreaseStockRequest(
    val productId: String,
    val quantity: Int
)

