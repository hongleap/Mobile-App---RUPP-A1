package com.example.app.blockchain

import com.example.app.api.ApiClient
import com.example.app.blockchain.data.TokenTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

/**
 * API client for transaction history
 * Syncs transaction history with backend server
 */
object TransactionHistoryApi {
    
    /**
     * Save a transaction to server
     */
    suspend fun saveTransaction(transaction: TokenTransaction): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("transactionHash", transaction.txHash ?: "")
                put("type", transaction.type.name.lowercase())
                put("amount", transaction.amount.toString())
                put("fromAddress", transaction.fromAddress)
                put("toAddress", transaction.toAddress)
                put("timestamp", transaction.timestamp)
                put("status", transaction.status.name.lowercase())
            }
            
            val response = ApiClient.makeAuthenticatedRequest(
                endpoint = "/api/transactions/save",
                method = "POST",
                body = requestBody.toString()
            )
            
            val json = JSONObject(response)
            if (json.getBoolean("success")) {
                android.util.Log.d("TransactionHistoryApi", "Transaction saved successfully: ${transaction.txHash}")
                Result.success(Unit)
            } else {
                val error = json.optString("message", "Failed to save transaction")
                android.util.Log.e("TransactionHistoryApi", "Failed to save transaction: $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            android.util.Log.e("TransactionHistoryApi", "Error saving transaction: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get transaction history from server
     */
    suspend fun getTransactionHistory(): Result<List<TokenTransaction>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.makeAuthenticatedRequest(
                endpoint = "/api/transactions/history",
                method = "GET"
            )
            
            val json = JSONObject(response)
            if (json.getBoolean("success")) {
                val dataArray = json.getJSONArray("data")
                val transactions = mutableListOf<TokenTransaction>()
                
                for (i in 0 until dataArray.length()) {
                    val item = dataArray.getJSONObject(i)
                    val typeString = item.getString("type").lowercase()
                    val transactionType = when (typeString) {
                        "send", "sent" -> com.example.app.blockchain.data.TransactionType.SEND
                        "receive", "received" -> com.example.app.blockchain.data.TransactionType.RECEIVE
                        "payment" -> com.example.app.blockchain.data.TransactionType.PAYMENT
                        else -> com.example.app.blockchain.data.TransactionType.SEND
                    }
                    
                    transactions.add(
                        TokenTransaction(
                            id = item.getString("id"),
                            txHash = item.getString("transactionHash"),
                            type = transactionType,
                            amount = java.math.BigDecimal(item.getString("amount")),
                            fromAddress = item.getString("fromAddress"),
                            toAddress = item.getString("toAddress"),
                            timestamp = item.getLong("timestamp"),
                            status = when (item.getString("status").lowercase()) {
                                "completed" -> com.example.app.blockchain.data.TransactionStatus.COMPLETED
                                "failed" -> com.example.app.blockchain.data.TransactionStatus.FAILED
                                "pending" -> com.example.app.blockchain.data.TransactionStatus.PENDING
                                else -> com.example.app.blockchain.data.TransactionStatus.PENDING
                            }
                        )
                    )
                }
                
                Result.success(transactions)
            } else {
                Result.failure(Exception(json.optString("message", "Failed to fetch transactions")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
