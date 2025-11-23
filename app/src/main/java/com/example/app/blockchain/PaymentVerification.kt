package com.example.app.blockchain

import android.content.Context
import android.content.SharedPreferences
import com.example.app.blockchain.data.TransactionStatus
import java.math.BigDecimal
import com.example.app.blockchain.config.TokenConfig

/**
 * Manages payment verification for checkout orders
 * Ensures token payment is completed before order can be placed
 */
object PaymentVerification {
    private const val PREFS_NAME = "payment_verification_prefs"
    private const val KEY_PENDING_PAYMENT = "pending_payment_tx"
    private const val KEY_PAYMENT_AMOUNT = "pending_payment_amount"
    private const val KEY_PAYMENT_TIMESTAMP = "pending_payment_timestamp"
    private const val KEY_PAYMENT_FROM_ADDRESS = "pending_payment_from"
    private const val KEY_CONSUMED_TX_HASHES = "consumed_tx_hashes"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Mark a payment as pending (when transaction is sent to MetaMask)
     */
    fun setPendingPayment(
        context: Context,
        transactionHash: String?,
        amount: java.math.BigDecimal,
        recipientAddress: String,
        fromAddress: String
    ) {
        val prefs = getSharedPreferences(context)
        prefs.edit().apply {
            putString(KEY_PENDING_PAYMENT, transactionHash ?: "pending_${System.currentTimeMillis()}")
            putString(KEY_PAYMENT_AMOUNT, amount.toString())
            putString(KEY_PAYMENT_FROM_ADDRESS, fromAddress)
            putLong(KEY_PAYMENT_TIMESTAMP, System.currentTimeMillis())
            apply()
        }
    }
    
    /**
     * Verify if payment is completed
     * Returns true if payment transaction is confirmed
     */
    fun isPaymentVerified(context: Context, requiredAmount: java.math.BigDecimal): Boolean {
        val prefs = getSharedPreferences(context)
        val txHash = prefs.getString(KEY_PENDING_PAYMENT, null) ?: return false
        
        // Check if transaction exists in history and is completed
        val transactions = TransactionHistory.getTransactions(context)
        val paymentTransaction = transactions.find { 
            it.txHash == txHash || it.id == txHash 
        }
        
        if (paymentTransaction != null) {
            // Verify amount matches
            val paymentAmount = try {
                java.math.BigDecimal(prefs.getString(KEY_PAYMENT_AMOUNT, "0") ?: "0")
            } catch (e: Exception) {
                return false
            }
            
            // Check if amount matches (within 0.0001 tolerance for rounding)
            val amountDiff = paymentAmount.subtract(requiredAmount).abs()
            if (amountDiff.compareTo(java.math.BigDecimal("0.0001")) > 0) {
                return false
            }
            
            // Check if transaction is completed
            return paymentTransaction.status == TransactionStatus.COMPLETED
        }
        
        return false
    }
    
    /**
     * Verify pending payment by checking blockchain
     * @return true if verified
     */
    suspend fun verifyPendingPayment(context: Context): Pair<Boolean, String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val pendingTxId = prefs.getString(KEY_PENDING_PAYMENT, null)
        val amountStr = prefs.getString(KEY_PAYMENT_AMOUNT, null)
        val fromAddress = prefs.getString(KEY_PAYMENT_FROM_ADDRESS, null)
        
        if (pendingTxId == null || amountStr == null || fromAddress == null) {
            return Pair(false, "No pending payment found")
        }
        
        // Get list of consumed transaction hashes from local storage (fallback)
        val localConsumedHashes = prefs.getStringSet(KEY_CONSUMED_TX_HASHES, emptySet()) ?: emptySet()
        
        // Try to get consumed hashes from server (preferred)
        val serverConsumedHashes = mutableSetOf<String>()
        // Note: We'll check individual transactions during verification
        
        // Check blockchain for successful transaction
        val (txHash, debugMsg) = TokenService.verifyTransaction(
            fromAddress = fromAddress,
            toAddress = TokenConfig.STORE_WALLET_ADDRESS,
            amount = BigDecimal(amountStr),
            consumedHashes = localConsumedHashes // Use local for now, server check below
        )
        
        if (txHash != null) {
            // Check server to see if transaction was already consumed
            val serverCheck = TransactionApi.isTransactionConsumed(txHash)
            if (serverCheck.isSuccess && serverCheck.getOrNull() == true) {
                return Pair(false, "Transaction already used for a previous order")
            }
            
            markPaymentAsVerified(context, txHash)
            return Pair(true, "Success")
        }
        
        return Pair(false, debugMsg)
    }
    
    /**
     * Clear pending payment after order is placed
     */
    fun clearPendingPayment(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().apply {
            remove(KEY_PENDING_PAYMENT)
            remove(KEY_PAYMENT_AMOUNT)
            remove(KEY_PAYMENT_FROM_ADDRESS)
            remove(KEY_PAYMENT_TIMESTAMP)
            apply()
        }
    }
    
    /**
     * Get pending payment transaction hash
     */
    fun getPendingPaymentHash(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_PENDING_PAYMENT, null)
    }
    
    /**
     * Mark payment as verified (when transaction hash is received from MetaMask)
     */
    fun markPaymentAsVerified(
        context: Context,
        transactionHash: String
    ) {
        val prefs = getSharedPreferences(context)
        val pendingTx = prefs.getString(KEY_PENDING_PAYMENT, null)
        
        // Update stored hash to real hash
        prefs.edit().putString(KEY_PENDING_PAYMENT, transactionHash).apply()
        
        if (pendingTx != null) {
            // Update transaction in history with real hash
            val transactions = TransactionHistory.getTransactions(context)
            val transaction = transactions.find { it.id == pendingTx || it.txHash == pendingTx }
            
            if (transaction != null) {
                TransactionHistory.updateTransactionStatus(
                    context = context,
                    transactionId = transaction.id,
                    status = TransactionStatus.COMPLETED,
                    txHash = transactionHash
                )
            }
        }
    }
    
    /**
     * Check if a transaction hash has already been used for an order
     */
    fun isTransactionConsumed(context: Context, txHash: String): Boolean {
        val prefs = getSharedPreferences(context)
        val consumedHashes = prefs.getStringSet(KEY_CONSUMED_TX_HASHES, emptySet()) ?: emptySet()
        return consumedHashes.contains(txHash)
    }
    
    /**
     * Mark a transaction as consumed (used for an order)
     * Saves to both local storage and server database
     */
    suspend fun markTransactionAsConsumed(context: Context, txHash: String, amount: java.math.BigDecimal) {
        // Save to local storage (immediate, works offline)
        val prefs = getSharedPreferences(context)
        val consumedHashes = prefs.getStringSet(KEY_CONSUMED_TX_HASHES, emptySet())?.toMutableSet() ?: mutableSetOf()
        consumedHashes.add(txHash)
        prefs.edit().putStringSet(KEY_CONSUMED_TX_HASHES, consumedHashes).apply()
        
        // Save to server (persistent, cross-device)
        try {
            TransactionApi.markTransactionAsConsumed(txHash, amount)
        } catch (e: Exception) {
            // Log error but don't fail - local storage is sufficient
            android.util.Log.e("PaymentVerification", "Failed to mark transaction on server: ${e.message}")
        }
    }
}

