package com.example.services.transaction

import com.example.shared.models.*
import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient
import io.ktor.http.*
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
import io.ktor.serialization.kotlinx.json.*
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
    
    embeddedServer(Netty, port = 8084, host = "0.0.0.0", module = Application::module)
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
        route("/api/transactions") {
            // Mark a transaction as consumed
            post("/mark-consumed") {
                val userId = call.request.header("X-User-Id")
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                val request = call.receive<MarkConsumedRequest>()
                
                try {
                    // Save to Firestore
                    val transactionData = mapOf<String, Any>(
                        "transactionHash" to request.transactionHash,
                        "userId" to userId,
                        "amount" to request.amount,
                        "timestamp" to (request.timestamp ?: System.currentTimeMillis()),
                        "createdAt" to System.currentTimeMillis()
                    )
                    
                    firestore?.collection("consumed_transactions")
                        ?.document(request.transactionHash)
                        ?.set(transactionData)
                        ?.get()
                    
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse<Nothing>(success = true, message = "Transaction marked as consumed")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to mark transaction")
                    )
                }
            }
            
            // Check if a transaction is consumed
            get("/is-consumed/{txHash}") {
                val txHash = call.parameters["txHash"]
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Nothing>(success = false, error = "Transaction hash is required")
                    )
                
                try {
                    val doc = firestore?.collection("consumed_transactions")
                        ?.document(txHash)
                        ?.get()
                        ?.get()
                    
                    val consumed = doc?.exists() == true
                    
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            data = mapOf("consumed" to consumed)
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to check transaction")
                    )
                }
            }
            
            // Get all consumed transactions for a user (for admin/debugging)
            get("/user-transactions") {
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                try {
                    val snapshot = firestore?.collection("consumed_transactions")
                        ?.whereEqualTo("userId", userId)
                        ?.get()
                        ?.get()
                    
                    val transactions = snapshot?.documents?.map { doc ->
                        val data = doc.data ?: emptyMap()
                        ConsumedTransaction(
                            transactionHash = doc.id,
                            userId = data["userId"] as? String ?: "",
                            amount = data["amount"] as? String ?: "0",
                            timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L,
                            createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L
                        )
                    } ?: emptyList()
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = transactions))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch transactions")
                    )
                }
            }
        }
        
        // Transaction History Routes
        route("/api/transactions") {
            // Save a transaction to history
            post("/save") {
                val userId = call.request.header("X-User-Id")
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                val request = call.receive<SaveTransactionRequest>()
                
                try {
                    val transactionData = mapOf<String, Any>(
                        "userId" to userId,
                        "transactionHash" to request.transactionHash,
                        "type" to request.type,
                        "amount" to request.amount,
                        "fromAddress" to request.fromAddress,
                        "toAddress" to request.toAddress,
                        "timestamp" to request.timestamp,
                        "status" to request.status,
                        "createdAt" to System.currentTimeMillis()
                    )
                    
                    firestore?.collection("transactions")
                        ?.add(transactionData)
                        ?.get()
                    
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse<Nothing>(success = true, message = "Transaction saved")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to save transaction")
                    )
                }
            }
            
            // Get transaction history for user
            get("/history") {
                val userId = call.request.header("X-User-Id")
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Nothing>(success = false, error = "User ID is required")
                    )
                
                try {
                    val snapshot = firestore?.collection("transactions")
                        ?.whereEqualTo("userId", userId)
                        ?.orderBy("timestamp", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        ?.limit(100)
                        ?.get()
                        ?.get()
                    
                    val transactions = snapshot?.documents?.map { doc ->
                        val data = doc.data ?: emptyMap()
                        TransactionHistoryItem(
                            id = doc.id,
                            transactionHash = data["transactionHash"] as? String ?: "",
                            type = data["type"] as? String ?: "",
                            amount = data["amount"] as? String ?: "0",
                            fromAddress = data["fromAddress"] as? String ?: "",
                            toAddress = data["toAddress"] as? String ?: "",
                            timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L,
                            status = data["status"] as? String ?: "pending"
                        )
                    } ?: emptyList()
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = transactions))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Nothing>(success = false, error = e.message ?: "Failed to fetch transactions")
                    )
                }
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class MarkConsumedRequest(
    val transactionHash: String,
    val amount: String,
    val timestamp: Long? = null
)

@kotlinx.serialization.Serializable
data class ConsumedTransaction(
    val transactionHash: String,
    val userId: String,
    val amount: String,
    val timestamp: Long,
    val createdAt: Long
)

@kotlinx.serialization.Serializable
data class SaveTransactionRequest(
    val transactionHash: String,
    val type: String, // "sent" or "received"
    val amount: String,
    val fromAddress: String,
    val toAddress: String,
    val timestamp: Long,
    val status: String
)

@kotlinx.serialization.Serializable
data class TransactionHistoryItem(
    val id: String,
    val transactionHash: String,
    val type: String,
    val amount: String,
    val fromAddress: String,
    val toAddress: String,
    val timestamp: Long,
    val status: String
)
