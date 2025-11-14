package com.example.services.notification

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
    
    embeddedServer(Netty, port = 8083, host = "0.0.0.0", module = Application::module)
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
        route("/api/notifications") {
            get {
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                try {
                    val snapshot = firestore?.collection("notifications")
                        ?.whereEqualTo("userId", userId)
                        ?.orderBy("createdAt", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        ?.get()?.get()
                    
                    val notifications = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val data = doc.data ?: emptyMap<String, Any>()
                            Notification(
                                id = doc.id,
                                userId = userId,
                                message = data["message"] as? String ?: "",
                                isRead = data["isRead"] as? Boolean ?: false,
                                createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                                type = data["type"] as? String ?: "order"
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = notifications))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch notifications")
                    )
                }
            }
            
            post {
                val request = call.receive<CreateNotificationRequest>()
                
                try {
                    val notificationData = mapOf<String, Any>(
                        "userId" to request.userId,
                        "message" to request.message,
                        "isRead" to false,
                        "type" to request.type,
                        "createdAt" to System.currentTimeMillis()
                    )
                    
                    val docRef = firestore?.collection("notifications")?.add(notificationData)?.get()
                    if (docRef == null) {
                        return@post call.respond(
                            HttpStatusCode.InternalServerError,
                            ApiResponse<Nothing>(success = false, error = "Failed to create notification")
                        )
                    }
                    
                    val notification = Notification(
                        id = docRef.id,
                        userId = request.userId,
                        message = request.message,
                        isRead = false,
                        createdAt = System.currentTimeMillis(),
                        type = request.type
                    )
                    
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = notification))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to create notification")
                    )
                }
            }
            
            put("/{id}/read") {
                val notificationId = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(success = false, error = "Notification ID is required")
                )
                
                try {
                    firestore?.collection("notifications")?.document(notificationId)
                        ?.update("isRead", true)?.get()
                    call.respond(HttpStatusCode.OK, ApiResponse<Nothing>(success = true, message = "Notification marked as read"))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to update notification")
                    )
                }
            }
            
            put("/mark-all-read") {
                val userId = call.request.header("X-User-Id")
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                try {
                    val snapshot = firestore?.collection("notifications")
                        ?.whereEqualTo("userId", userId)
                        ?.whereEqualTo("isRead", false)
                        ?.get()?.get()
                    
                    snapshot?.documents?.forEach { doc ->
                        firestore.collection("notifications").document(doc.id)
                            .update("isRead", true).get()
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse<Nothing>(success = true, message = "All notifications marked as read"))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to update notifications")
                    )
                }
            }
        }
    }
}

