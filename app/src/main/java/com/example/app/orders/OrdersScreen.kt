package com.example.app.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.orders.data.Order

val orderStatuses = listOf("Processing", "Shipped", "Delivered", "Returned", "Canceled")

@Composable
fun OrdersScreen(
    orders: List<Order> = emptyList(),
    onOrderClick: (String) -> Unit = {},
    onExploreCategories: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    showBottomNav: Boolean = true
) {
    var selectedStatus by remember { mutableStateOf("Processing") }
    
    val filteredOrders = if (orders.isEmpty()) {
        emptyList()
    } else {
        orders.filter { it.status == selectedStatus }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Text(
            text = "Orders",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF262626)
        )

        if (orders.isEmpty()) {
            // Empty state
            EmptyOrdersState(onExploreCategories = onExploreCategories)
        } else {
            // Status filters
            StatusFilters(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Orders list
            OrdersList(
                orders = filteredOrders,
                onOrderClick = onOrderClick
            )
        }

        if (showBottomNav) {
            Spacer(modifier = Modifier.weight(1f))
            BottomNavBar(
                selectedTab = "orders",
                onHomeClick = onHomeClick,
                onOrdersClick = {},
                onNotificationsClick = onNotificationsClick,
                onProfileClick = onProfileClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatusFilters(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orderStatuses) { status ->
            StatusChip(
                status = status,
                isSelected = status == selectedStatus,
                onClick = { onStatusSelected(status) }
            )
        }
    }
}

@Composable
private fun StatusChip(
    status: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2D2D2D) else Color(0xFFE0E0E0)
        )
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (isSelected) Color.White else Color(0xFF262626),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun OrdersList(
    orders: List<Order>,
    onOrderClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        orders.forEach { order ->
            OrderCard(order = order, onClick = { onOrderClick(order.id) })
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image (show first item's image)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                val firstItem = order.items.firstOrNull()
                if (firstItem != null) {
                    com.example.app.ui.ProductImage(
                        productId = firstItem.productId,
                        category = firstItem.category,
                        contentDescription = firstItem.productName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback to icon if no items
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF666666)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Order details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order #${order.orderNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF262626)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${order.itemCount} items",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            // Chevron icon
            Text(
                text = ">",
                fontSize = 20.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun EmptyOrdersState(onExploreCategories: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Shopping cart icon
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFFF9800)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Orders yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Explore Categories button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onExploreCategories),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
        ) {
            Text(
                text = "Explore Categories",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    selectedTab: String = "home",
    onHomeClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .background(Color(0xFFF6F6F6))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val homeColor = if (selectedTab == "home") Color(0xFF6B5BFF) else Color(0xFF262626)
        val ordersColor = if (selectedTab == "orders") Color(0xFF6B5BFF) else Color(0xFF262626)
        val notificationsColor = if (selectedTab == "notifications") Color(0xFF6B5BFF) else Color(0xFF262626)
        val profileColor = if (selectedTab == "profile") Color(0xFF6B5BFF) else Color(0xFF262626)

        Box {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = homeColor,
                modifier = Modifier.clickable(onClick = onHomeClick)
            )
            if (selectedTab == "home") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 24.dp, height = 2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF262626))
                        .padding(top = 20.dp)
                )
            }
        }
        
        Box {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Orders",
                tint = ordersColor,
                modifier = Modifier.clickable(onClick = onOrdersClick)
            )
            if (selectedTab == "orders") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 24.dp, height = 2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF262626))
                        .padding(top = 20.dp)
                )
            }
        }
        
        Box {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = notificationsColor,
                modifier = Modifier.clickable(onClick = onNotificationsClick)
            )
            if (selectedTab == "notifications") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 24.dp, height = 2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF262626))
                        .padding(top = 20.dp)
                )
            }
        }
        
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile",
            tint = profileColor,
            modifier = Modifier.clickable(onClick = onProfileClick)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrdersScreenEmpty() {
    MaterialTheme {
        OrdersScreen(orders = emptyList())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrdersScreenWithOrders() {
    MaterialTheme {
        OrdersScreen(
            orders = listOf(
                Order("1", "456765", 4, "Processing"),
                Order("2", "454569", 2, "Processing"),
                Order("3", "454809", 1, "Processing")
            )
        )
    }
}

