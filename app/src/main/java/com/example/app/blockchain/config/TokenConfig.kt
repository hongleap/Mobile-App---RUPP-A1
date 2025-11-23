package com.example.app.blockchain.config

object TokenConfig {
    // BNB Smart Chain Testnet Configuration
    // BNB Smart Chain Testnet Configuration
    // Using publicnode.com for better stability and reliability
    const val BSC_TESTNET_RPC_URL = "https://bsc-testnet.publicnode.com"
    
    // Your token contract address on BNB Testnet
    const val TOKEN_CONTRACT_ADDRESS = "0x201DA48b6EF10Ff050CF63Bc91B551d23990D153"
    
    // Token details
    const val TOKEN_NAME = "CLOT Token"
    const val TOKEN_SYMBOL = "CLOT"
    const val TOKEN_DECIMALS = 18
    
    // Chain ID for BNB Smart Chain Testnet
    const val CHAIN_ID = 97L
    
    // Store wallet address for receiving payments
    // IMPORTANT: Replace with your actual store wallet address on BNB Smart Chain Testnet
    // Format: Must be a valid Ethereum address (0x followed by 40 hex characters)
    // Example: "0xYourStoreWalletAddressHere"
    // Current address: 0xdfdd35fdf9741860e8669f30437cb70b376e7fad2 (42 chars: 0x + 40 hex)
    const val STORE_WALLET_ADDRESS = "0x65d172456a625a27bc4e43c8ba807879462d6893"
    
    init {
        // Validate store wallet address format
        require(STORE_WALLET_ADDRESS.matches(Regex("^0x[a-fA-F0-9]{40}$"))) {
            "STORE_WALLET_ADDRESS must be a valid Ethereum address (0x followed by 40 hex characters)"
        }
    }
    
    // Gas settings
    const val GAS_LIMIT = 21000L
    const val GAS_PRICE = 20000000000L // 20 Gwei
}

