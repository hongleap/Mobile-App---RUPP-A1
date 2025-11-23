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
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .padding(bottom = 40.dp)
    ) {
        // Back button and order number
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF262626)
                )
            }
            Text(
                text = "Order #$orderNumber",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF262626)
            )
        }

        // Order status timeline
        OrderStatusTimeline(statuses = statuses)

        Spacer(modifier = Modifier.height(32.dp))

        // Order Items section
        OrderItemsSection(
            items = order?.items ?: emptyList()
        )

        Spacer(modifier = Modifier.height(24.dp))

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
                                if (status.isCompleted) Color(0xFF2D2D2D) 
                                else if (status.isCurrent) Color(0xFFE0E0E0)
                                else Color(0xFF2D2D2D)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (status.isCompleted && !status.isCurrent) {
                            Text(
                                text = "✓",
                                color = Color.White,
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
                                    if (status.isCompleted) Color(0xFF2D2D2D) else Color(0xFFE0E0E0)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.size(12.dp))

                // Status text and date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = status.status,
                        fontSize = 14.sp,
                        fontWeight = if (status.isCompleted || status.isCurrent) FontWeight.Medium else FontWeight.Normal,
                        color = if (status.isCompleted || status.isCurrent) Color(0xFF262626) else Color(0xFF999999)
                    )
                    Text(
                        text = status.date,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    if (index < statuses.size - 1) {
                        Spacer(modifier = Modifier.height(32.dp))
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF262626),
            modifier = Modifier.padding(bottom = 12.dp)
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
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
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
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                com.example.app.ui.ProductImage(
                                    productId = item.productId,
                                    category = item.category,
                                    contentDescription = item.productName,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Product details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF262626)
                                )
                                Text(
                                    text = "Qty: ${item.quantity}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF999999)
                                )
                            }

                            // Price
                            Text(
                                text = "$${String.format("%.2f", item.price * item.quantity)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF262626)
                            )
                        }

                        // Add divider between items (except for last item)
                        if (index < items.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFF0F0F0))
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF262626),
            modifier = Modifier.padding(bottom = 12.dp)
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
                    fontSize = 14.sp,
                    color = Color(0xFF262626),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = phoneNumber,
                    fontSize = 14.sp,
                    color = Color(0xFF262626)
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

