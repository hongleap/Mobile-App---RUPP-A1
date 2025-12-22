package com.example.app.notifications.data



data class Notification(
    val id: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val type: String = "order" // order, promotion, system, etc.
)

object NotificationRepository {
    
    suspend fun createNotification(
        message: String,
        type: String = "order"
    ): Result<String> {
        return try {
            com.example.app.api.ApiClient.createNotification(message, type)
            Result.success("notification-created")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val notifications = com.example.app.api.ApiClient.getNotifications()
            Result.success(notifications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            com.example.app.api.ApiClient.markNotificationAsRead(notificationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markAllAsRead(): Result<Unit> {
        return try {
            com.example.app.api.ApiClient.markAllNotificationsAsRead()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

