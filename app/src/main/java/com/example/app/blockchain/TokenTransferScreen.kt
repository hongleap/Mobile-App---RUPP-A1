package com.example.app.blockchain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.blockchain.config.TokenConfig
import com.example.app.blockchain.data.TokenTransaction
import com.example.app.blockchain.data.TransactionStatus
import com.example.app.blockchain.data.TransactionType
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.math.BigDecimal
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenTransferScreen(
    recipientAddress: String = "",
    amount: BigDecimal? = null,
    onBackClick: () -> Unit = {},
    onTransferComplete: (String) -> Unit = {} // Returns transaction hash
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var walletAddress by remember { mutableStateOf<String?>(WalletManager.getWalletAddress(context)) }
    var tokenBalance by remember { mutableStateOf<BigDecimal?>(null) }
    
    // Check if this is a store payment (recipient is store address)
    val isStorePayment = remember(recipientAddress) {
        recipientAddress.equals(com.example.app.blockchain.config.TokenConfig.STORE_WALLET_ADDRESS, ignoreCase = true)
    }
    
    // Normalize recipient address - ensure it starts with 0x (not Ox) and is valid
    var recipient by remember { 
        mutableStateOf(
            recipientAddress.trim().let { addr ->
                if (addr.isNotEmpty()) {
                    // Fix common issues: replace O with 0, ensure it starts with 0x
                    var normalized = addr.replaceFirst(Regex("^[Oo]x"), "0x")
                    // Ensure lowercase for hex characters (but keep 0x prefix)
                    if (normalized.startsWith("0x") && normalized.length > 2) {
                        normalized = "0x" + normalized.substring(2).lowercase()
                    }
                    normalized
                } else {
                    ""
                }
            }
        )
    }
    var transferAmount by remember { mutableStateOf(amount?.toString() ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isMetaMaskInstalled by remember { mutableStateOf(false) }
    
    // Check MetaMask installation status
    LaunchedEffect(Unit) {
        isMetaMaskInstalled = MetaMaskService.isMetaMaskInstalled(context)
    }
    
    LaunchedEffect(walletAddress) {
        walletAddress?.let { address ->
            TokenService.getTokenBalance(address).fold(
                onSuccess = { tokenBalance = it },
                onFailure = { }
            )
        }
    }
    
    fun handleTransfer() {
        if (walletAddress == null) {
            errorMessage = "Please connect your wallet first"
            return
        }
        
        if (recipient.isBlank()) {
            errorMessage = "Please enter recipient address"
            return
        }
        
        if (!TokenService.isValidAddress(recipient)) {
            errorMessage = "Invalid recipient address"
            return
        }
        
        val amountValue = try {
            BigDecimal(transferAmount).setScale(2, java.math.RoundingMode.HALF_UP)
        } catch (e: Exception) {
            errorMessage = "Invalid amount"
            return
        }
        
        if (amountValue <= BigDecimal.ZERO) {
            errorMessage = "Amount must be greater than 0"
            return
        }
        
        if (tokenBalance != null && amountValue > tokenBalance!!) {
            errorMessage = "Insufficient balance"
            return
        }
        
        isLoading = true
        errorMessage = null
        
        scope.launch {
            // Prepare transfer data
            val transferDataResult = TokenService.prepareTransferData(recipient, amountValue)
            
            transferDataResult.fold(
                onSuccess = { encodedData ->
                    // Check if MetaMask is installed (re-check in case status changed)
                    val metaMaskCheck = MetaMaskService.isMetaMaskInstalled(context)
                    if (!metaMaskCheck) {
                        errorMessage = "MetaMask is not installed. Please install MetaMask from Play Store to complete transactions."
                        isLoading = false
                        isMetaMaskInstalled = false
                        
                        // Offer to open Play Store
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=io.metamask")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback to web browser
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://play.google.com/store/apps/details?id=io.metamask")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        }
                        return@launch
                    }
                    isMetaMaskInstalled = true
                    
                    // Create transaction request and open MetaMask
                    val transferRequestResult = MetaMaskService.requestTokenTransfer(
                        context = context,
                        toAddress = recipient,
                        amount = amountValue,
                        fromAddress = walletAddress!!
                    )
                    
                    transferRequestResult.fold(
                        onSuccess = { pendingTxId ->
                            // Transaction sent to MetaMask for approval
                            // Save as pending transaction
                            val transaction = TokenTransaction(
                                type = TransactionType.SEND,
                                amount = amountValue,
                                fromAddress = walletAddress!!,
                                toAddress = recipient,
                                status = TransactionStatus.PENDING,
                                txHash = null,
                                description = "Pending MetaMask approval"
                            )
                            
                            // Save to local and server
                            scope.launch {
                                TransactionHistory.saveTransaction(context, transaction)
                            }
                            
                            // Mark payment as pending verification
                            if (isStorePayment) {
                                PaymentVerification.setPendingPayment(
                                    context = context,
                                    transactionHash = transaction.id,
                                    amount = amountValue,
                                    recipientAddress = recipient,
                                    fromAddress = walletAddress!!
                                )
                            }
                            
                            successMessage = "Transaction sent to MetaMask!\n\n" +
                                    "Please approve the transaction in MetaMask app.\n" +
                                    "After approval, return to this app to complete your order."
                            isLoading = false
                            
                            // Note: Transaction is pending until user approves in MetaMask
                            // The transaction hash will be available after MetaMask approval
                            // For now, we use a pending ID - in production, get real hash from MetaMask callback
                        },
                        onFailure = { error ->
                            // Try alternative: direct MetaMask deep link
                            val opened = MetaMaskService.openMetaMaskForTransaction(
                                context = context,
                                contractAddress = TokenConfig.TOKEN_CONTRACT_ADDRESS,
                                transferData = encodedData
                            )
                            
                            if (opened) {
                                val transaction = TokenTransaction(
                                    type = TransactionType.SEND,
                                    amount = amountValue,
                                    fromAddress = walletAddress!!,
                                    toAddress = recipient,
                                    status = TransactionStatus.PENDING,
                                    txHash = null,
                                    description = "Pending MetaMask approval"
                                )
                                
                                // Save to local and server
                                scope.launch {
                                    TransactionHistory.saveTransaction(context, transaction)
                                }
                                
                                // Mark payment as pending verification
                                if (isStorePayment) {
                                    PaymentVerification.setPendingPayment(
                                        context = context,
                                        transactionHash = transaction.id,
                                        amount = amountValue,
                                        recipientAddress = recipient,
                                        fromAddress = walletAddress!!
                                    )
                                }
                                
                                successMessage = "Opening MetaMask...\n\nPlease approve the transaction in MetaMask."
                                isLoading = false
                            } else {
                                errorMessage = "Failed to open MetaMask: ${error.message}\n\nPlease make sure MetaMask is installed and try again."
                                isLoading = false
                            }
                        }
                    )
                },
                onFailure = {
                    errorMessage = "Failed to prepare transfer: ${it.message}"
                    isLoading = false
                }
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Tokens") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (walletAddress == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Please connect your wallet first",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            } else {
                // MetaMask Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMetaMaskInstalled) 
                            Color(0xFFE8F5E9) 
                        else 
                            Color(0xFFFFF3E0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isMetaMaskInstalled) "✓" else "⚠",
                            fontSize = 24.sp,
                            color = if (isMetaMaskInstalled) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMetaMaskInstalled) "MetaMask Ready" else "MetaMask Not Detected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isMetaMaskInstalled) Color(0xFF2E7D32) else Color(0xFFE65100)
                            )
                            Text(
                                text = if (isMetaMaskInstalled) 
                                    "You can proceed with token transfer" 
                                else 
                                    "Please install MetaMask to complete transactions",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isMetaMaskInstalled) Color(0xFF2E7D32) else Color(0xFFE65100)
                            )
                        }
                    }
                }
                
                // Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tokenBalance?.let { 
                                "${TokenService.formatTokenAmount(it)} ${TokenConfig.TOKEN_SYMBOL}"
                            } ?: "Loading...",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Recipient Address
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { newValue ->
                        // Only allow changes if it's not a store payment
                        if (!isStorePayment) {
                            // Normalize input: fix Ox -> 0x, ensure lowercase
                            val normalized = newValue.trim().let { addr ->
                                if (addr.isNotEmpty()) {
                                    var fixed = addr.replaceFirst(Regex("^[Oo]x"), "0x")
                                    if (fixed.startsWith("0x") && fixed.length > 2) {
                                        fixed = "0x" + fixed.substring(2).lowercase()
                                    }
                                    fixed
                                } else {
                                    ""
                                }
                            }
                            recipient = normalized
                        }
                    },
                    label = { Text(if (isStorePayment) "Store Address (Fixed)" else "Recipient Address") },
                    placeholder = { Text("0x...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading && !isStorePayment, // Disable if store payment
                    readOnly = isStorePayment, // Make read-only for store payments
                    isError = recipient.isNotBlank() && !TokenService.isValidAddress(recipient),
                    supportingText = {
                        if (isStorePayment) {
                            Text("Store payment address (cannot be changed)", color = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                        } else if (recipient.isNotBlank() && !TokenService.isValidAddress(recipient)) {
                            Text("Invalid address format. Must be 0x followed by 40 hex characters.")
                        } else if (recipient.isBlank() && recipientAddress.isNotBlank()) {
                            Text("Store address: ${WalletManager.getShortAddress(recipientAddress)}")
                        } else if (recipient.isNotBlank() && TokenService.isValidAddress(recipient)) {
                            Text("Valid address", color = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Amount
                OutlinedTextField(
                    value = transferAmount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            transferAmount = it
                        }
                    },
                    label = { Text("Amount (${TokenConfig.TOKEN_SYMBOL})") },
                    placeholder = { Text("0.0") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Max button
                TextButton(
                    onClick = {
                        tokenBalance?.let { transferAmount = it.toString() }
                    },
                    enabled = !isLoading && tokenBalance != null
                ) {
                    Text("Use Max")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Transfer Button
                if (!successMessage.isNullOrEmpty()) {
                    // Transfer initiated, show Verify button
                    Button(
                        onClick = {
                            isLoading = true
                            scope.launch {
                                val (isVerified, debugMsg) = com.example.app.blockchain.PaymentVerification.verifyPendingPayment(context)
                                if (isVerified) {
                                    val txHash = com.example.app.blockchain.PaymentVerification.getPendingPaymentHash(context)
                                    if (txHash != null) {
                                        onTransferComplete(txHash)
                                    } else {
                                        // Should not happen if verified, but fallback
                                        onTransferComplete("verified_tx")
                                    }
                                } else {
                                    isLoading = false
                                    android.widget.Toast.makeText(context, "Not Verified: $debugMsg", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying...")
                        } else {
                            Text("Check Payment Status")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Back button
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Return to Checkout")
                    }
                } else {
                    // Normal Transfer Button
                    Button(
                        onClick = { handleTransfer() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && walletAddress != null && isMetaMaskInstalled && 
                                  recipient.isNotBlank() && TokenService.isValidAddress(recipient) &&
                                  transferAmount.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        } else if (!isMetaMaskInstalled) {
                            Text("MetaMask Required")
                        } else {
                            Text("Transfer")
                        }
                    }
                }
                
                // Error message
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                // Success message
                successMessage?.let { success ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = success,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

