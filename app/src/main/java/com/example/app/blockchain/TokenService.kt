package com.example.app.blockchain

import com.example.app.blockchain.config.TokenConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

object TokenService {
    private val web3j: Web3j = Web3j.build(HttpService(TokenConfig.BSC_TESTNET_RPC_URL))
    
    /**
     * Get token balance for a wallet address
     * @param walletAddress The wallet address to check balance for
     * @return Token balance as BigDecimal, or null if error
     */
    suspend fun getTokenBalance(walletAddress: String): Result<BigDecimal> = withContext(Dispatchers.IO) {
        try {
            // ERC20 balanceOf function
            val function = Function(
                "balanceOf",
                listOf(org.web3j.abi.datatypes.Address(walletAddress)),
                listOf(object : TypeReference<Uint256>() {})
            )
            
            val encodedFunction = FunctionEncoder.encode(function)
            
            val ethCall = web3j.ethCall(
                Transaction.createEthCallTransaction(
                    walletAddress,
                    TokenConfig.TOKEN_CONTRACT_ADDRESS,
                    encodedFunction
                ),
                DefaultBlockParameterName.LATEST
            ).send()
            
            if (ethCall.hasError()) {
                return@withContext Result.failure(Exception("RPC Error: ${ethCall.error.message}"))
            }
            
            val value = ethCall.value
            val decoded = FunctionReturnDecoder.decode(value, function.outputParameters)
            
            if (decoded.isEmpty()) {
                return@withContext Result.failure(Exception("Empty response from contract"))
            }
            
            val balance = decoded[0].value as BigInteger
            val balanceInEther = Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
            
            Result.success(balanceInEther)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get BNB balance for a wallet address
     * @param walletAddress The wallet address to check balance for
     * @return BNB balance as BigDecimal, or null if error
     */
    suspend fun getBNBBalance(walletAddress: String): Result<BigDecimal> = withContext(Dispatchers.IO) {
        try {
            val ethGetBalance = web3j.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).send()
            
            if (ethGetBalance.hasError()) {
                return@withContext Result.failure(Exception("RPC Error: ${ethGetBalance.error.message}"))
            }
            
            val balance = ethGetBalance.balance
            val balanceInBNB = Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
            
            Result.success(balanceInBNB)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if a wallet address is valid
     * @param address The address to validate
     * @return true if valid, false otherwise
     */
    fun isValidAddress(address: String): Boolean {
        if (address.isBlank()) return false
        
        // Ethereum/BNB address format: 0x followed by 40 hexadecimal characters
        val addressPattern = Regex("^0x[a-fA-F0-9]{40}$")
        if (!addressPattern.matches(address)) {
            return false
        }
        
        // Additional validation: check if it's not all zeros
        val addressWithoutPrefix = address.substring(2)
        return addressWithoutPrefix.any { it != '0' }
    }
    
    /**
     * Format token amount for display
     * @param amount The token amount
     * @param decimals Number of decimal places to show
     * @return Formatted string
     */
    fun formatTokenAmount(amount: BigDecimal, decimals: Int = 4): String {
        return if (amount.compareTo(BigDecimal.ZERO) == 0) {
            "0"
        } else if (amount.compareTo(BigDecimal("0.0001")) < 0) {
            "< 0.0001"
        } else {
            amount.setScale(decimals, java.math.RoundingMode.DOWN).toPlainString()
        }
    }
    
    /**
     * Prepare token transfer data (for signing with wallet)
     * @param toAddress Recipient address
     * @param amount Token amount to transfer
     * @return Encoded function data for transfer
     */
    fun prepareTransferData(toAddress: String, amount: BigDecimal): Result<String> {
        return try {
            // Convert amount to Wei (assuming 18 decimals)
            val amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
            
            // ERC20 transfer function
            val function = Function(
                "transfer",
                listOf(
                    org.web3j.abi.datatypes.Address(toAddress),
                    org.web3j.abi.datatypes.generated.Uint256(amountInWei)
                ),
                listOf(object : TypeReference<org.web3j.abi.datatypes.Bool>() {})
            )
            
            val encodedFunction = FunctionEncoder.encode(function)
            Result.success(encodedFunction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Estimate gas for token transfer
     * @param fromAddress Sender address
     * @param toAddress Recipient address
     * @param amount Token amount
     * @return Estimated gas
     */
    suspend fun estimateTransferGas(
        fromAddress: String,
        toAddress: String,
        amount: BigDecimal
    ): Result<BigInteger> = withContext(Dispatchers.IO) {
        try {
            val amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
            val function = Function(
                "transfer",
                listOf(
                    org.web3j.abi.datatypes.Address(toAddress),
                    org.web3j.abi.datatypes.generated.Uint256(amountInWei)
                ),
                listOf(object : TypeReference<org.web3j.abi.datatypes.Bool>() {})
            )
            
            val encodedFunction = FunctionEncoder.encode(function)
            
            val ethEstimateGas = web3j.ethEstimateGas(
                Transaction.createEthCallTransaction(
                    fromAddress,
                    TokenConfig.TOKEN_CONTRACT_ADDRESS,
                    encodedFunction
                )
            ).send()
            
            if (ethEstimateGas.hasError()) {
                return@withContext Result.failure(Exception("Gas estimation error: ${ethEstimateGas.error.message}"))
            }
            
            // Get estimated gas amount - Web3j returns BigInteger
            val estimatedGas = try {
                ethEstimateGas.amountUsed
            } catch (e: Exception) {
                // Fallback to default gas limit for ERC20 transfer
                BigInteger.valueOf(65000)
            }
            
            Result.success(estimatedGas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /**
     * Verify if a transaction exists on blockchain
     * @param fromAddress Sender address
     * @param toAddress Recipient address
     * @param amount Amount to verify
     * @param consumedHashes Set of transaction hashes that have already been used
     * @return Transaction hash if found, null otherwise
     */
    suspend fun verifyTransaction(
        fromAddress: String, 
        toAddress: String, 
        amount: BigDecimal,
        consumedHashes: Set<String> = emptySet()
    ): Pair<String?, String> = withContext(Dispatchers.IO) {
        try {
            if (!isValidAddress(fromAddress) || !isValidAddress(toAddress)) {
                return@withContext Pair(null, "Invalid address format")
            }
            // Event signature for Transfer(address,address,uint256)
            val eventSignature = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"
            
            // Pad addresses to 32 bytes (64 hex chars)
            // Address is 20 bytes (40 hex chars), so we need 12 bytes (24 zeros) padding
            // IMPORTANT: Lowercase addresses for matching
            val cleanFrom = fromAddress.trim().lowercase().removePrefix("0x")
            val cleanTo = toAddress.trim().lowercase().removePrefix("0x")
            
            val paddedFrom = "0x000000000000000000000000" + cleanFrom
            val paddedTo = "0x000000000000000000000000" + cleanTo
            
            // Get current block number to limit search range
            val blockNumber = web3j.ethBlockNumber().send().blockNumber
            // Look back ~5000 blocks (approx 4 hours on BSC) to be safe
            val fromBlock = blockNumber.subtract(BigInteger.valueOf(5000))
            
            // Create filter for Transfer events
            val filter = org.web3j.protocol.core.methods.request.EthFilter(
                org.web3j.protocol.core.DefaultBlockParameter.valueOf(fromBlock),
                DefaultBlockParameterName.LATEST,
                TokenConfig.TOKEN_CONTRACT_ADDRESS
            )
            // Topic 0: Event Signature
            filter.addSingleTopic(eventSignature)
            // Topic 1: From Address (Wildcard - fetch all, filter in code)
            filter.addNullTopic()
            // Topic 2: To Address
            filter.addSingleTopic(paddedTo)
            
            val logs = web3j.ethGetLogs(filter).send()
            
            if (logs.hasError()) {
                return@withContext Pair(null, "RPC Error: ${logs.error.message}")
            }
            
            val logsList = logs.logs
            var debugMsg = "Found ${logsList.size} transfers. "
            
            val amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
            
            // Find matching log (excluding consumed transactions)
            val match = logsList.find { log ->
                val logObject = log as org.web3j.protocol.core.methods.response.Log
                val topics = logObject.topics
                
                // Verify it's a transfer to us (Topic 2) - redundant but safe
                if (topics.size < 3) return@find false
                
                // Skip if this transaction was already consumed
                val txHash = logObject.transactionHash
                if (consumedHashes.contains(txHash)) {
                    debugMsg += "Found tx but already used. "
                    return@find false
                }
                
                // Check Sender (Topic 1)
                // Topic 1 is the "From" address (padded)
                val logFrom = topics[1].removePrefix("0x").takeLast(40)
                val expectedFrom = cleanFrom.takeLast(40)
                
                if (!logFrom.equals(expectedFrom, ignoreCase = true)) {
                    return@find false
                }
                
                // Data contains the amount (non-indexed parameter)
                val data = logObject.data.removePrefix("0x")
                if (data.isNotEmpty()) {
                    val value = BigInteger(data, 16)
                    
                    // Check amount match with tolerance (1% difference allowed)
                    // This handles potential precision issues or rounding
                    val diff = value.subtract(amountInWei).abs()
                    val tolerance = amountInWei.divide(BigInteger.valueOf(100)) // 1%
                    
                    val isMatch = diff <= tolerance
                    if (!isMatch) {
                        debugMsg += "Amt mismatch: ${value} vs ${amountInWei}. "
                    }
                    isMatch
                } else {
                    false
                }
            }
            
            val txHash = if (match != null) {
                Pair((match as org.web3j.protocol.core.methods.response.Log).transactionHash, "Success")
            } else {
                if (logsList.isEmpty()) debugMsg = "No transfers found in last 5000 blocks."
                else if (debugMsg == "Found ${logsList.size} transfers. ") debugMsg += "No sender match."
                Pair(null, debugMsg)
            }
            txHash
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, "Exception: ${e.message}")
        }
    }
}

