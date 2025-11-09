package com.example.app.notifications.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Notification(
    val id: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val type: String = "order" // order, promotion, system, etc.
)

object NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    private fun getNotificationsCollection() = db.collection("notifications")
    
    suspend fun createNotification(
        message: String,
        type: String = "order"
    ): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val notificationData = hashMapOf(
                "userId" to userId,
                "message" to message,
                "isRead" to false,
                "type" to type,
                "createdAt" to System.currentTimeMillis()
            )
            
            val docRef = getNotificationsCollection().add(notificationData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val snapshot = getNotificationsCollection()
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val notifications = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    Notification(
                        id = doc.id,
                        message = data["message"] as? String ?: "",
                        isRead = data["isRead"] as? Boolean ?: false,
                        createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        type = data["type"] as? String ?: "order"
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(notifications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            getNotificationsCollection()
                .document(notificationId)
                .update("isRead", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val snapshot = getNotificationsCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()
            
            snapshot.documents.forEach { doc ->
                doc.reference.update("isRead", true).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

