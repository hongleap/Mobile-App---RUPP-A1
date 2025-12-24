package com.example.app.blockchain

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
import com.example.app.blockchain.config.TokenConfig
import com.example.app.blockchain.data.TokenTransaction
import com.example.app.blockchain.data.TransactionStatus
import com.example.app.blockchain.data.TransactionType
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onBackClick: () -> Unit = {},
    onTransferClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var walletAddress by remember { mutableStateOf<String?>(WalletManager.getWalletAddress(context)) }
    var tokenBalance by remember { mutableStateOf<BigDecimal?>(null) }
    var bnbBalance by remember { mutableStateOf<BigDecimal?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConnectDialog by remember { mutableStateOf(false) }
    var addressInput by remember { mutableStateOf("") }
    var showTransactionHistory by remember { mutableStateOf(false) }
    var transactions by remember { mutableStateOf<List<TokenTransaction>>(emptyList()) }
    
    fun loadBalances() {
        if (walletAddress != null) {
            isLoading = true
            errorMessage = null
            scope.launch {
                // Load token balance
                TokenService.getTokenBalance(walletAddress!!).fold(
                    onSuccess = { tokenBalance = it },
                    onFailure = { errorMessage = "Failed to load token balance: ${it.message}" }
                )
                
                // Load BNB balance
                TokenService.getBNBBalance(walletAddress!!).fold(
                    onSuccess = { bnbBalance = it },
                    onFailure = { 
                        if (errorMessage == null) {
                            errorMessage = "Failed to load BNB balance: ${it.message}"
                        }
                    }
                )
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(walletAddress) {
        if (walletAddress != null) {
            loadBalances()
            // Load transaction history
            transactions = TransactionHistory.getTransactionsByAddress(context, walletAddress!!)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Wallet",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                actions = {
                    if (walletAddress != null) {
                        IconButton(onClick = { loadBalances() }) {
                            Icon(
                                Icons.Default.Refresh, 
                                "Refresh",
                                tint = AppColors.TextPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppDimensions.SpacingL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (walletAddress == null) {
                // No wallet connected
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimensions.SpacingXXXL),
                    shape = RoundedCornerShape(AppDimensions.RadiusL),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(AppDimensions.SpacingXXXL),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Connect Wallet",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Enter your BNB Smart Chain wallet address to view your token balance",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = AppColors.TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))
                        
                        // Connect with MetaMask button
                        if (MetaMaskService.isMetaMaskInstalled(context)) {
                            Button(
                                onClick = {
                                    // Open MetaMask to get wallet address
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("metamask://wc")
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                        // Note: In full implementation, you'd get address from WalletConnect callback
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to open MetaMask. Please enter address manually."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.Primary,
                                    contentColor = AppColors.TextOnPrimary
                                ),
                                shape = RoundedCornerShape(AppDimensions.RadiusM)
                            ) {
                                Text("Connect with MetaMask")
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "OR",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            // MetaMask not installed - show install button
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("market://details?id=io.metamask")
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("https://play.google.com/store/apps/details?id=io.metamask")
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AppColors.Primary
                                ),
                                shape = RoundedCornerShape(AppDimensions.RadiusM)
                            ) {
                                Text("Install MetaMask")
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "OR",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        OutlinedTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            label = { Text("Enter Wallet Address Manually") },
                            placeholder = { Text("0x...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(AppDimensions.RadiusM),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = AppColors.Primary,
                                unfocusedBorderColor = AppColors.SurfaceVariant
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                if (WalletManager.validateAddress(addressInput)) {
                                    WalletManager.saveWalletAddress(context, addressInput)
                                    walletAddress = addressInput
                                    addressInput = ""
                                } else {
                                    errorMessage = "Invalid wallet address"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = addressInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Primary,
                                contentColor = AppColors.TextOnPrimary
                            ),
                            shape = RoundedCornerShape(AppDimensions.RadiusM)
                        ) {
                            Text("Connect Wallet")
                        }
                    }
                }
            } else {
                // Wallet connected
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = AppDimensions.SpacingL),
                    shape = RoundedCornerShape(AppDimensions.RadiusL),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.PrimaryLight.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Connected Wallet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = WalletManager.getShortAddress(walletAddress!!),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = AppColors.TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = {
                                WalletManager.clearWallet(context)
                                walletAddress = null
                                tokenBalance = null
                                bnbBalance = null
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = AppColors.AccentRed
                            )
                        ) {
                            Text("Disconnect")
                        }
                    }
                }
                
                // Token Balance Card
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
                            text = "${TokenConfig.TOKEN_SYMBOL} Balance",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = AppColors.TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = tokenBalance?.let { 
                                    TokenService.formatTokenAmount(it) 
                                } ?: "0",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = AppColors.TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                
                // BNB Balance Card
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
                            text = "BNB Balance",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = AppColors.TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = bnbBalance?.let { 
                                    TokenService.formatTokenAmount(it) 
                                } ?: "0",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = AppColors.TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = AppDimensions.SpacingL),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
                ) {
                    Button(
                        onClick = onTransferClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            contentColor = AppColors.TextOnPrimary
                        ),
                        shape = RoundedCornerShape(AppDimensions.RadiusM)
                    ) {
                        Text("Transfer")
                    }
                    OutlinedButton(
                        onClick = { showTransactionHistory = !showTransactionHistory },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.Primary
                        ),
                        shape = RoundedCornerShape(AppDimensions.RadiusM)
                    ) {
                        Text("History")
                    }
                }
                
                // Transaction History
                if (showTransactionHistory) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AppDimensions.SpacingL),
                        shape = RoundedCornerShape(AppDimensions.RadiusL),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(AppDimensions.SpacingL)
                        ) {
                            Text(
                                text = "Transaction History",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = AppColors.TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (transactions.isEmpty()) {
                                Text(
                                    text = "No transactions yet",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = AppColors.TextTertiary
                                    ),
                                    modifier = Modifier.padding(vertical = AppDimensions.SpacingL)
                                )
                            } else {
                                transactions.take(10).forEach { transaction ->
                                    TransactionItem(transaction)
                                    Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
                                }
                            }
                        }
                    }
                }
                
                // Token Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppDimensions.RadiusL),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Token Information",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow("Name", TokenConfig.TOKEN_NAME)
                        InfoRow("Symbol", TokenConfig.TOKEN_SYMBOL)
                        InfoRow("Network", "BNB Smart Chain Testnet")
                        InfoRow("Contract", WalletManager.getShortAddress(TokenConfig.TOKEN_CONTRACT_ADDRESS))
                    }
                }
            }
            
            // Error message
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(AppDimensions.SpacingL))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.AccentRed.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(AppDimensions.RadiusM)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(AppDimensions.SpacingL),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.AccentRed
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppDimensions.SpacingXS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AppColors.TextSecondary
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun TransactionItem(transaction: TokenTransaction) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(transaction.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.RadiusM),
        colors = CardDefaults.cardColors(
            containerColor = when (transaction.status) {
                TransactionStatus.COMPLETED -> AppColors.PrimaryLight.copy(alpha = 0.1f)
                TransactionStatus.FAILED -> AppColors.AccentRed.copy(alpha = 0.1f)
                TransactionStatus.PENDING -> AppColors.PrimaryLight.copy(alpha = 0.05f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.SpacingM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (transaction.type) {
                            TransactionType.SEND -> "Sent"
                            TransactionType.RECEIVE -> "Received"
                            TransactionType.PAYMENT -> "Payment"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${TokenService.formatTokenAmount(transaction.amount)} ${TokenConfig.TOKEN_SYMBOL}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                    Text(
                        text = "To: ${WalletManager.getShortAddress(transaction.toAddress)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextTertiary,
                            fontSize = 10.sp
                        )
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextTertiary,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                    Text(
                        text = transaction.status.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = when (transaction.status) {
                                TransactionStatus.COMPLETED -> AppColors.Primary
                                TransactionStatus.FAILED -> AppColors.AccentRed
                                TransactionStatus.PENDING -> AppColors.PrimaryLight
                            },
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

