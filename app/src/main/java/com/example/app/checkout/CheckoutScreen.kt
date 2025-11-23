package com.example.app.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckoutScreen(
    subtotal: Double = 200.0,
    shippingCost: Double = 8.0,
    tax: Double = 0.0,
    shippingAddress: String? = null,
    paymentMethod: String? = null,
    selectedPaymentType: String? = null, // "card", "token", "bank"
    isPaymentVerified: Boolean = true, // Whether token payment is verified
    onBackClick: () -> Unit = {},
    onShippingAddressClick: () -> Unit = {},
    onPaymentMethodClick: () -> Unit = {},
    onTokenPaymentClick: () -> Unit = {},
    onBankClick: () -> Unit = {},
    onPlaceOrder: () -> Unit = {},
    onVerifyClick: () -> Unit = {}
) {
    val total = subtotal + shippingCost + tax
    val scrollState = rememberScrollState()
    val displayAddress = shippingAddress ?: "No address selected"
    val displayPayment = paymentMethod ?: "No payment method selected"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF262626)
                )
            }
            Text(
                text = "Checkout",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF262626)
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Shipping Address Section
            ShippingAddressSection(
                address = displayAddress,
                onClick = onShippingAddressClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Section
            PaymentMethodSection(
                cardNumber = displayPayment,
                onClick = onPaymentMethodClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Token Payment Section
            TokenPaymentSection(
                isSelected = selectedPaymentType == "token",
                onClick = onTokenPaymentClick
            )
            
            // Payment verification status for token payments
            if (selectedPaymentType == "token") {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPaymentVerified) 
                            Color(0xFFE8F5E9) 
                        else 
                            Color(0xFFFFF3E0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isPaymentVerified) "✓" else "⚠",
                            fontSize = 20.sp,
                            color = if (isPaymentVerified) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                        Text(
                            text = if (isPaymentVerified) 
                                "Token payment verified. You can place your order." 
                            else 
                                "Please complete token payment in MetaMask first.",
                            fontSize = 12.sp,
                            color = if (isPaymentVerified) Color(0xFF2E7D32) else Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (!isPaymentVerified) {
                            TextButton(
                                onClick = onVerifyClick,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFFE65100)
                                )
                            ) {
                                Text("Check Status", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bank Section
            BankSection(
                onClick = onBankClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Order Summary
            OrderSummarySection(
                subtotal = subtotal,
                shippingCost = shippingCost,
                tax = tax,
                total = total
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Bottom bar with total and Place Order button
        BottomActionBar(
            total = total,
            isPaymentVerified = isPaymentVerified,
            selectedPaymentType = selectedPaymentType,
            onPlaceOrder = onPlaceOrder
        )
    }
}

@Composable
private fun ShippingAddressSection(
    address: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFF262626),
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Shipping Address",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = address,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF262626)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Edit",
                tint = Color(0xFF262626)
            )
        }
    }
}

@Composable
private fun PaymentMethodSection(
    cardNumber: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF262626)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💳",
                        fontSize = 16.sp
                    )
                }
                Column {
                    Text(
                        text = "Payment Method",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = cardNumber,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF262626)
                        )
                        // Mastercard icon placeholder
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEB001B))
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Edit",
                tint = Color(0xFF262626)
            )
        }
    }
}

@Composable
private fun TokenPaymentSection(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF0F0F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF7931E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🪙",
                        fontSize = 16.sp
                    )
                }
                Column {
                    Text(
                        text = "Token Payment",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pay with ${com.example.app.blockchain.config.TokenConfig.TOKEN_SYMBOL}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF262626)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Select Token Payment",
                tint = Color(0xFF262626)
            )
        }
    }
}

@Composable
private fun BankSection(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF262626)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏦",
                        fontSize = 16.sp
                    )
                }
                Column {
                    Text(
                        text = "Bank",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get QRCode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF262626)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Get QRCode",
                tint = Color(0xFF262626)
            )
        }
    }
}

@Composable
private fun OrderSummarySection(
    subtotal: Double,
    shippingCost: Double,
    tax: Double,
    total: Double
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Subtotal",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", subtotal)}",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Shipping Cost",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", shippingCost)}",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tax",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", tax)}",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", total)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    total: Double,
    isPaymentVerified: Boolean = true,
    selectedPaymentType: String? = null,
    onPlaceOrder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$${String.format("%.2f", total)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            val isButtonEnabled = selectedPaymentType != null && (selectedPaymentType != "token" || isPaymentVerified)
            
            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (isButtonEnabled) {
                            Modifier.clickable(onClick = onPlaceOrder)
                        } else {
                            Modifier
                        }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isButtonEnabled) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                )
            ) {
                Text(
                    text = if (selectedPaymentType == "token" && !isPaymentVerified) 
                        "Complete Payment First" 
                    else 
                        "Place Order",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

