package com.example.app.blockchain

import android.content.Context
import android.content.SharedPreferences
import com.example.app.blockchain.config.TokenConfig

object WalletManager {
    private const val PREFS_NAME = "wallet_prefs"
    private const val KEY_WALLET_ADDRESS = "wallet_address"
    private const val KEY_WALLET_PRIVATE_KEY = "wallet_private_key" // Encrypted in production
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save wallet address
     */
    fun saveWalletAddress(context: Context, address: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_WALLET_ADDRESS, address)
            .apply()
    }
    
    /**
     * Get saved wallet address
     */
    fun getWalletAddress(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_WALLET_ADDRESS, null)
    }
    
    /**
     * Check if wallet is connected
     */
    fun isWalletConnected(context: Context): Boolean {
        return getWalletAddress(context) != null
    }
    
    /**
     * Clear wallet data (logout)
     */
    fun clearWallet(context: Context) {
        getSharedPreferences(context).edit()
            .remove(KEY_WALLET_ADDRESS)
            .remove(KEY_WALLET_PRIVATE_KEY)
            .apply()
    }
    
    /**
     * Validate wallet address format
     */
    fun validateAddress(address: String): Boolean {
        return TokenService.isValidAddress(address)
    }
    
    /**
     * Get short address for display (0x1234...5678)
     */
    fun getShortAddress(address: String): String {
        return if (address.length > 10) {
            "${address.take(6)}...${address.takeLast(4)}"
        } else {
            address
        }
    }
}

