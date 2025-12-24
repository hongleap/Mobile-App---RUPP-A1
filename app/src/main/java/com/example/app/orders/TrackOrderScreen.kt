package com.example.app.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
import com.example.app.orders.data.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OrderStatus(
    val status: String,
    val date: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean = false
)

@Composable
fun TrackOrderScreen(
    order: Order? = null,
    onBackClick: () -> Unit = {}
) {
    // Generate status timeline based on order status
    val statuses = if (order != null) {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val orderDate = dateFormat.format(Date(order.createdAt))
        
        val allStatuses = listOf("Order Placed", "Order Confirmed", "Shipped", "Delivered", "Returned", "Canceled")
        val currentStatusIndex = allStatuses.indexOf(order.status).takeIf { it >= 0 } ?: 0
        
        // Map status names to match order status
        val statusMap = mapOf(
            "Processing" to "Order Placed",
            "Order Confirmed" to "Order Confirmed",
            "Shipped" to "Shipped",
            "Delivered" to "Delivered",
            "Returned" to "Returned",
            "Canceled" to "Canceled"
        )
        val mappedStatus = statusMap[order.status] ?: order.status
        val mappedStatusIndex = allStatuses.indexOf(mappedStatus).takeIf { it >= 0 } ?: 0
        
        allStatuses.mapIndexed { index, status ->
            val isCompleted = index < mappedStatusIndex
            val isCurrent = index == mappedStatusIndex
            OrderStatus(
                status = status,
                date = orderDate,
                isCompleted = isCompleted,
                isCurrent = isCurrent
            )
        }.filter { 
            // Only show relevant statuses based on current status
            val statusIndex = allStatuses.indexOf(it.status)
            statusIndex <= mappedStatusIndex + 1 || it.isCompleted
        }
    } else {
        emptyList()
    }
    
    val orderNumber = order?.orderNumber ?: "N/A"
    val itemCount = order?.itemCount ?: 0
    val shippingAddress = order?.shippingAddress ?: "No address available"
    val phoneNumber = order?.shippingPhone ?: order?.customerEmail ?: "No phone available"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = AppDimensions.SpacingL)
            .padding(top = AppDimensions.SpacingL)
            .padding(bottom = AppDimensions.SpacingXXL)
    ) {
        // Back button and order number
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = AppDimensions.SpacingXXXL)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.TextPrimary
                )
            }
            Text(
                text = "Order #$orderNumber",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
        }

        // Order status timeline
        OrderStatusTimeline(statuses = statuses)

        Spacer(modifier = Modifier.height(32.dp))

        // Order Items section
        OrderItemsSection(
            items = order?.items ?: emptyList()
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        // Shipping details
        ShippingDetailsSection(
            address = shippingAddress,
            phoneNumber = phoneNumber
        )
    }
}

@Composable
private fun OrderStatusTimeline(statuses: List<OrderStatus>) {
    Column {
        statuses.forEachIndexed { index, status ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Timeline indicator column
                Column(
                    modifier = Modifier.width(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (status.isCompleted) AppColors.Primary 
                                else if (status.isCurrent) AppColors.SurfaceVariant
                                else AppColors.Primary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (status.isCompleted && !status.isCurrent) {
                            Text(
                                text = "✓",
                                color = AppColors.TextOnPrimary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    
                    // Vertical line connector (only if not last item)
                    if (index < statuses.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(50.dp)
                                .background(
                                    if (status.isCompleted) AppColors.Primary else AppColors.SurfaceVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.size(12.dp))

                // Status text and date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = status.status,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (status.isCompleted || status.isCurrent) FontWeight.Medium else FontWeight.Normal,
                            color = if (status.isCompleted || status.isCurrent) AppColors.TextPrimary else AppColors.TextTertiary
                        )
                    )
                    Text(
                        text = status.date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextTertiary
                        )
                    )
                    if (index < statuses.size - 1) {
                        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItemsSection(
    items: List<com.example.app.orders.data.OrderItem> = emptyList()
) {
    Column {
        Text(
            text = "Order Items",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            ),
            modifier = Modifier.padding(bottom = AppDimensions.SpacingM)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (items.isEmpty()) {
                    Text(
                        text = "No items in this order",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.TextTertiary
                        )
                    )
                } else {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Product image
                            Box(
                                modifier = Modifier
                                    .size(AppDimensions.IconXXL)
                                    .clip(RoundedCornerShape(AppDimensions.RadiusM))
                            ) {
                                com.example.app.ui.ProductImage(
                                    productId = item.productId,
                                    category = item.category,
                                    contentDescription = item.productName,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(AppDimensions.SpacingM))

                            // Product details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.TextPrimary
                                    )
                                )
                                Text(
                                    text = "Qty: ${item.quantity}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AppColors.TextSecondary
                                    )
                                )
                            }

                            // Price
                            Text(
                                text = "$${String.format("%.2f", item.price * item.quantity)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextPrimary
                                )
                            )
                        }

                        // Add divider between items (except for last item)
                        if (index < items.size - 1) {
                            Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(AppColors.SurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShippingDetailsSection(
    address: String = "No address available",
    phoneNumber: String = "No phone available"
) {
    Column {
        Text(
            text = "Shipping details",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            ),
            modifier = Modifier.padding(bottom = AppDimensions.SpacingM)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextPrimary,
                        lineHeight = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextPrimary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackOrderScreen() {
    MaterialTheme {
        TrackOrderScreen()
    }
}

