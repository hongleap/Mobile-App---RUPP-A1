package com.example.app.blockchain

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.app.blockchain.config.TokenConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Service to handle MetaMask integration via deep linking
 * This allows the app to send transaction requests to MetaMask
 * for user approval
 */
object MetaMaskService {
    
    /**
     * Check if MetaMask is installed on the device
     * Uses multiple methods to reliably detect MetaMask
     */
    fun isMetaMaskInstalled(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            
            // Method 1: Check if package is installed directly
            try {
                packageManager.getPackageInfo("io.metamask", 0)
                return true
            } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
                // Package not found, try other methods
            }
            
            // Method 2: Try to resolve activity with deep link
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("metamask://")
                setPackage("io.metamask")
            }
            val resolveInfo = packageManager.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveInfo != null) {
                return true
            }
            
            // Method 3: Try alternative deep link format
            val intent2 = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://metamask.app.link")
                setPackage("io.metamask")
            }
            val resolveInfo2 = packageManager.resolveActivity(intent2, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo2 != null
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Open MetaMask app to connect wallet
     * Returns the deep link URI to open MetaMask
     */
    fun getMetaMaskConnectUri(): String {
        // WalletConnect URI format for MetaMask
        // In production, you would use WalletConnect protocol
        return "metamask://wc?uri="
    }
    
    /**
     * Create a transaction request URI for MetaMask
     * This will open MetaMask with a transaction for user approval
     * 
     * @param toAddress Recipient address
     * @param amount Token amount to transfer
     * @param fromAddress Sender address (current wallet)
     * @return Deep link URI to open MetaMask
     */
    fun createTokenTransferRequest(
        toAddress: String,
        amount: BigDecimal,
        fromAddress: String
    ): String? {
        // Convert amount to Wei (18 decimals)
        val amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
        
        // EIP-681 compatible deep link for ERC20 transfer
        // Format: https://link.metamask.io/send/{contractAddress}@{chainId}/transfer?address={recipient}&uint256={amount}
        // This explicitly calls the 'transfer' function on the token contract
        return "https://link.metamask.io/send/${TokenConfig.TOKEN_CONTRACT_ADDRESS}@${TokenConfig.CHAIN_ID}/transfer?address=$toAddress&uint256=$amountInWei"
    }
    
    /**
     * Open MetaMask with a transaction request
     * This will show MetaMask app with the transaction for user to approve
     */
    suspend fun requestTokenTransfer(
        context: Context,
        toAddress: String,
        amount: BigDecimal,
        fromAddress: String
    ): Result<String> = withContext(Dispatchers.Main) {
        try {
            if (!isMetaMaskInstalled(context)) {
                return@withContext Result.failure(
                    Exception("MetaMask is not installed. Please install MetaMask from Play Store.")
                )
            }
            
            val transferData = TokenService.prepareTransferData(toAddress, amount).getOrNull()
                ?: return@withContext Result.failure(Exception("Failed to prepare transfer data"))
            
            // Create intent to open MetaMask
            val intent = Intent(Intent.ACTION_VIEW).apply {
                // Use direct deep link format
                val deepLink = createTokenTransferRequest(toAddress, amount, fromAddress)
                    ?: throw Exception("Failed to generate transfer request")
                
                data = Uri.parse(deepLink)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            context.startActivity(intent)
            
            // Return a pending transaction ID
            // In real implementation, you'd wait for MetaMask callback
            Result.success("pending_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate a WalletConnect URI for the transaction
     * This is a simplified version - in production use WalletConnect SDK
     */
    private fun generateWalletConnectUri(transferData: String, toAddress: String): String {
        // Simplified WalletConnect URI
        // Format: wc:session_id@version?bridge=bridge_url&key=key
        val sessionId = generateSessionId()
        return "wc:$sessionId@1?bridge=https://bridge.walletconnect.org&key=${generateKey()}"
    }
    
    private fun generateSessionId(): String {
        return (0..31).map { (0..15).random().toString(16) }.joinToString("")
    }
    
    private fun generateKey(): String {
        return (0..63).map { (0..15).random().toString(16) }.joinToString("")
    }
    
    /**
     * Alternative: Use Web3Modal or direct MetaMask deep link
     * This opens MetaMask app directly with transaction details
     */
    fun openMetaMaskForTransaction(
        context: Context,
        contractAddress: String,
        transferData: String
    ): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                // Direct MetaMask deep link for transaction
                data = Uri.parse(
                    "https://metamask.app.link/dapp/" +
                    "?to=$contractAddress" +
                    "&value=0" +
                    "&data=$transferData"
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                // Fallback: open MetaMask in browser
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://metamask.io/download")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

