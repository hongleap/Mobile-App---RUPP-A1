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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

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
            .background(AppColors.Background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingXXL),
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
                text = "QR Code",
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
                .padding(horizontal = AppDimensions.SpacingL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

            // QR Code placeholder
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(AppDimensions.RadiusL))
                    .background(AppColors.Surface)
                    .padding(AppDimensions.SpacingL),
                contentAlignment = Alignment.Center
            ) {
                // QR Code pattern placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColors.SurfaceVariant)
                        .clip(RoundedCornerShape(AppDimensions.RadiusS)),
                    contentAlignment = Alignment.Center
                ) {
                    // Center logo/text
                    Text(
                        text = "Clot",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
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
                    .clip(RoundedCornerShape(AppDimensions.RadiusM))
                    .clickable(onClick = onConfirm),
                colors = CardDefaults.cardColors(containerColor = AppColors.Primary)
            ) {
                Text(
                    text = "Confirm",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimensions.SpacingL),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextOnPrimary
                    )
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
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary
                )
            )
            Text(
                text = "$${String.format("%.2f", subtotal)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary
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
                    color = AppColors.TextPrimary
                )
            )
            Text(
                text = "$${String.format("%.2f", shippingCost)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary
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
                    color = AppColors.TextPrimary
                )
            )
            Text(
                text = "$${String.format("%.2f", tax)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary
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
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
            Text(
                text = "$${String.format("%.2f", total)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
        }
    }
}

