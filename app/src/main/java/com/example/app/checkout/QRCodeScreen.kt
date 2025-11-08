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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun QRCodeScreen(
    subtotal: Double = 200.0,
    shippingCost: Double = 8.0,
    tax: Double = 0.0,
    onBackClick: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    val total = subtotal + shippingCost + tax
    val scrollState = rememberScrollState()

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
                text = "QR Code",
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // QR Code placeholder
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // QR Code pattern placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE8E8E8))
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Center logo/text
                    Text(
                        text = "Clot",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF262626)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Order Summary
            OrderSummarySection(
                subtotal = subtotal,
                shippingCost = shippingCost,
                tax = tax,
                total = total
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Confirm Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onConfirm),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                Text(
                    text = "Confirm",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
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
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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

