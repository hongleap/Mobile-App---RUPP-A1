package com.example.gateway

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
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
    // Initialize Firebase Admin SDK for token verification
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
        println("WARNING: firebase-service-account.json not found. Token verification will fail.")
    }
    
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
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
                mapOf("success" to false, "error" to (cause.message ?: "Internal server error"))
            )
        }
    }
    
    install(CallLogging) {
        level = Level.INFO
    }
    
    val auth = FirebaseAuth.getInstance()
    val httpClient = HttpClient(OkHttp) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }
    
    // Service URLs
    val productServiceUrl = System.getenv("PRODUCT_SERVICE_URL") ?: "http://localhost:8081"
    val orderServiceUrl = System.getenv("ORDER_SERVICE_URL") ?: "http://localhost:8082"
    val notificationServiceUrl = System.getenv("NOTIFICATION_SERVICE_URL") ?: "http://localhost:8083"
    
    // Middleware to verify Firebase token and extract user ID
    suspend fun verifyToken(call: ApplicationCall): String? {
        val authHeader = call.request.header(HttpHeaders.Authorization)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null
        }
        
        val token = authHeader.removePrefix("Bearer ")
        return try {
            val decodedToken = auth.verifyIdToken(token)
            decodedToken.uid
        } catch (e: Exception) {
            null
        }
    }
    
    routing {
        // Product Service Routes
        route("/api/products") {
            get {
                try {
                    val response = httpClient.get("$productServiceUrl/api/products")
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Product service unavailable")
                    )
                }
            }
            
            get("/{id}") {
                val productId = call.parameters["id"]
                try {
                    val response = httpClient.get("$productServiceUrl/api/products/$productId")
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Product service unavailable")
                    )
                }
            }
            
            get("/category/{category}") {
                val category = call.parameters["category"]
                try {
                    val response = httpClient.get("$productServiceUrl/api/products/category/$category")
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Product service unavailable")
                    )
                }
            }
        }
        
        // Order Service Routes
        route("/api/orders") {
            get {
                val userId = verifyToken(call)
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("success" to false, "error" to "Unauthorized")
                    )
                
                try {
                    val response = httpClient.get("$orderServiceUrl/api/orders") {
                        header("X-User-Id", userId)
                    }
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Order service unavailable")
                    )
                }
            }
            
            get("/{id}") {
                val userId = verifyToken(call)
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("success" to false, "error" to "Unauthorized")
                    )
                
                val orderId = call.parameters["id"]
                try {
                    val response = httpClient.get("$orderServiceUrl/api/orders/$orderId") {
                        header("X-User-Id", userId)
                    }
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Order service unavailable")
                    )
                }
            }
            
            post {
                val userId = verifyToken(call)
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("success" to false, "error" to "Unauthorized")
                    )
                
                val body = call.receive<String>()
                try {
                    val response = httpClient.post("$orderServiceUrl/api/orders") {
                        header("X-User-Id", userId)
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        setBody(body)
                    }
                    val responseBody = response.body<String>()
                    call.respond(
                        response.status,
                        responseBody
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Order service unavailable")
                    )
                }
            }
            
            put("/{id}/status") {
                val orderId = call.parameters["id"]
                val body = call.receive<String>()
                try {
                    val response = httpClient.put("$orderServiceUrl/api/orders/$orderId/status") {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        setBody(body)
                    }
                    val responseBody = response.body<String>()
                    call.respond(
                        response.status,
                        responseBody
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Order service unavailable")
                    )
                }
            }
        }
        
        // Notification Service Routes
        route("/api/notifications") {
            get {
                val userId = verifyToken(call)
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("success" to false, "error" to "Unauthorized")
                    )
                
                try {
                    val response = httpClient.get("$notificationServiceUrl/api/notifications") {
                        header("X-User-Id", userId)
                    }
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Notification service unavailable")
                    )
                }
            }
            
            post {
                val body = call.receive<String>()
                try {
                    val response = httpClient.post("$notificationServiceUrl/api/notifications") {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        setBody(body)
                    }
                    val responseBody = response.body<String>()
                    call.respond(
                        response.status,
                        responseBody
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Notification service unavailable")
                    )
                }
            }
            
            put("/{id}/read") {
                val notificationId = call.parameters["id"]
                try {
                    val response = httpClient.put("$notificationServiceUrl/api/notifications/$notificationId/read")
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Notification service unavailable")
                    )
                }
            }
            
            put("/mark-all-read") {
                val userId = verifyToken(call)
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("success" to false, "error" to "Unauthorized")
                    )
                
                try {
                    val response = httpClient.put("$notificationServiceUrl/api/notifications/mark-all-read") {
                        header("X-User-Id", userId)
                    }
                    val body = response.body<String>()
                    call.respond(
                        response.status,
                        body
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("success" to false, "error" to "Notification service unavailable")
                    )
                }
            }
        }
    }
}
