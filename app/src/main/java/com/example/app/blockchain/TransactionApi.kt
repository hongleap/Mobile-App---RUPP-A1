package com.example.app.blockchain

import com.example.app.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal

/**
 * API client for blockchain transaction tracking
 * Communicates with backend to track consumed transactions
 */
object TransactionApi {
    
    /**
     * Mark a transaction as consumed on the server
     * This prevents the transaction from being reused for another order
     */
    suspend fun markTransactionAsConsumed(
        transactionHash: String,
        amount: BigDecimal
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("transactionHash", transactionHash)
                put("amount", amount.toString())
                put("timestamp", System.currentTimeMillis())
            }
            
            val response = ApiClient.makeAuthenticatedRequest(
                endpoint = "/api/transactions/mark-consumed",
                method = "POST",
                body = requestBody.toString()
            )
            
            val json = JSONObject(response)
            if (json.getBoolean("success")) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(json.optString("message", "Failed to mark transaction as consumed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if a transaction has been consumed (used for an order)
     * Queries the server database
     */
    suspend fun isTransactionConsumed(transactionHash: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.makeAuthenticatedRequest(
                endpoint = "/api/transactions/is-consumed/$transactionHash",
                method = "GET"
            )
            
            val json = JSONObject(response)
            if (json.getBoolean("success")) {
                Result.success(json.getBoolean("consumed"))
            } else {
                Result.failure(Exception(json.optString("message", "Failed to check transaction status")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
