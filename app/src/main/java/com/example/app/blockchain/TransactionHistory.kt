package com.example.app.blockchain

import android.content.Context
import android.content.SharedPreferences
import com.example.app.blockchain.data.TokenTransaction
import com.example.app.blockchain.data.TransactionStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TransactionHistory {
    private const val PREFS_NAME = "token_transactions_prefs"
    private const val KEY_TRANSACTIONS = "transactions"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    
    /**
     * Save transaction to both local storage and server
     */
    suspend fun saveTransaction(context: Context, transaction: TokenTransaction) {
        // Save to local storage (immediate, works offline)
        val transactions = getTransactions(context).toMutableList()
        transactions.add(0, transaction) // Add to beginning (most recent first)
        val limitedTransactions = transactions.take(100)
        saveTransactions(context, limitedTransactions)
        
        // Save to server (persistent, cross-device)
        try {
            TransactionHistoryApi.saveTransaction(transaction)
        } catch (e: Exception) {
            android.util.Log.e("TransactionHistory", "Failed to save to server: ${e.message}")
        }
    }
    
    fun getTransactions(context: Context): List<TokenTransaction> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(KEY_TRANSACTIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<TokenTransaction>>() {}.type
        return try {
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveTransactions(context: Context, transactions: List<TokenTransaction>) {
        val prefs = getSharedPreferences(context)
        val json = Gson().toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }
    
    fun updateTransactionStatus(
        context: Context,
        transactionId: String,
        status: TransactionStatus,
        txHash: String? = null
    ) {
        val transactions = getTransactions(context).toMutableList()
        val index = transactions.indexOfFirst { it.id == transactionId }
        if (index != -1) {
            transactions[index] = transactions[index].copy(
                status = status,
                txHash = txHash ?: transactions[index].txHash
            )
            saveTransactions(context, transactions)
        }
    }
    
    fun getTransactionsByAddress(context: Context, address: String): List<TokenTransaction> {
        return getTransactions(context).filter {
            it.fromAddress.equals(address, ignoreCase = true) ||
            it.toAddress.equals(address, ignoreCase = true)
        }
    }
    
    /**
     * Sync transaction history from server
     * Call this on app launch to get latest transactions
     */
    suspend fun syncWithServer(context: Context): Result<Unit> {
        return try {
            val result = TransactionHistoryApi.getTransactionHistory()
            if (result.isSuccess) {
                val serverTransactions = result.getOrNull() ?: emptyList()
                if (serverTransactions.isNotEmpty()) {
                    // Replace local with server data (server is source of truth)
                    saveTransactions(context, serverTransactions)
                }
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Sync failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

