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
import androidx.compose.foundation.layout.width
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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

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
            .background(AppColors.Background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingL),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.TextPrimary
                )
            }
            Text(
                text = "Checkout",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = AppDimensions.SpacingL)
        ) {
            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))

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
                Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPaymentVerified) 
                            AppColors.PrimaryLight.copy(alpha = 0.1f) 
                        else 
                            AppColors.AccentRed.copy(alpha = 0.1f)
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
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = if (isPaymentVerified) AppColors.Primary else AppColors.AccentRed
                            )
                        )
                        Text(
                            text = if (isPaymentVerified) 
                                "Token payment verified. You can place your order." 
                            else 
                                "Please complete token payment in MetaMask first.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isPaymentVerified) AppColors.Primary else AppColors.AccentRed
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (!isPaymentVerified) {
                            TextButton(
                                onClick = onVerifyClick,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = AppColors.AccentRed
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

            Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

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
                    tint = AppColors.TextPrimary,
                    modifier = Modifier.size(AppDimensions.IconMedium)
                )
                Column {
                    Text(
                        text = "Shipping Address",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextTertiary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
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
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextTertiary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingS)
                    ) {
                        Text(
                            text = cardNumber,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        // Mastercard icon placeholder
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(AppDimensions.RadiusXS))
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
            .clip(RoundedCornerShape(AppDimensions.RadiusM))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppColors.PrimaryLight.copy(alpha = 0.1f) else AppColors.Surface
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
                        .size(AppDimensions.IconMedium)
                        .clip(RoundedCornerShape(AppDimensions.RadiusXS))
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
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextTertiary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pay with ${com.example.app.blockchain.config.TokenConfig.TOKEN_SYMBOL}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Select Token Payment",
                tint = AppColors.TextPrimary
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
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextTertiary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get QRCode",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Get QRCode",
                tint = AppColors.TextPrimary
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
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary
                )
            )
            Text(
                text = "$${String.format("%.2f", subtotal)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Shipping Cost",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary
                )
            )
            Text(
                text = "$${String.format("%.2f", shippingCost)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tax",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary
                )
            )
            Text(
                text = "$${String.format("%.2f", tax)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "$${String.format("%.2f", total)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
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
            .clip(RoundedCornerShape(topStart = AppDimensions.RadiusL, topEnd = AppDimensions.RadiusL)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.SpacingL),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$${String.format("%.2f", total)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = AppColors.TextOnPrimary,
                    fontWeight = FontWeight.Bold
                )
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
                    containerColor = if (isButtonEnabled) AppColors.PrimaryLight else AppColors.SurfaceVariant
                )
            ) {
                Text(
                    text = if (selectedPaymentType == "token" && !isPaymentVerified) 
                        "Complete Payment First" 
                    else 
                        "Place Order",
                    modifier = Modifier.padding(horizontal = AppDimensions.SpacingXXXL, vertical = AppDimensions.SpacingL),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isButtonEnabled) AppColors.Primary else AppColors.TextTertiary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

