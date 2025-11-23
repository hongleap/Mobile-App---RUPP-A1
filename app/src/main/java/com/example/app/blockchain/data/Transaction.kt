package com.example.app.blockchain.data

import java.math.BigDecimal

data class TokenTransaction(
    val id: String = System.currentTimeMillis().toString(),
    val type: TransactionType,
    val amount: BigDecimal,
    val fromAddress: String,
    val toAddress: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: TransactionStatus = TransactionStatus.PENDING,
    val txHash: String? = null,
    val description: String = ""
)

enum class TransactionType {
    SEND,
    RECEIVE,
    PAYMENT
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
}

