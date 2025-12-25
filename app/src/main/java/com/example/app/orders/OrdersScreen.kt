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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
import com.example.app.orders.data.Order
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

val orderStatuses = listOf("Processing", "Shipped", "Delivered", "Returned", "Canceled")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrdersScreen(
    orders: List<Order> = emptyList(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
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

    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Orders",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingL),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
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

                Spacer(modifier = Modifier.height(AppDimensions.SpacingM))

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
                Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = AppColors.Surface,
            contentColor = AppColors.Primary
        )
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
            .padding(vertical = AppDimensions.SpacingS),
        contentPadding = PaddingValues(horizontal = AppDimensions.SpacingL),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingS)
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
            .clip(RoundedCornerShape(AppDimensions.RadiusL))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppColors.Primary else AppColors.SurfaceVariant
        )
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingS),
            color = if (isSelected) AppColors.TextOnPrimary else AppColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
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
            .padding(horizontal = AppDimensions.SpacingL),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
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
            .clip(RoundedCornerShape(AppDimensions.RadiusM))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.SpacingL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image (show first item's image)
            Box(
                modifier = Modifier
                    .size(AppDimensions.IconXXL)
                    .clip(RoundedCornerShape(AppDimensions.RadiusS))
            ) {
                val firstItem = order.items.firstOrNull()
                if (firstItem != null) {
                    com.example.app.ui.ProductImage(
                        productId = firstItem.productId,
                        category = firstItem.category,
                        imageUrl = firstItem.imageUrl,
                        contentDescription = firstItem.productName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback to icon if no items
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.IconMedium),
                            tint = AppColors.TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(AppDimensions.SpacingL))

            // Order details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                Text(
                    text = "${order.itemCount} items",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextSecondary
                    )
                )
            }

            // Chevron icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = AppColors.TextTertiary
            )
        }
    }
}

@Composable
private fun EmptyOrdersState(onExploreCategories: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.SpacingXXXL),
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
                tint = AppColors.Primary
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        Text(
            text = "No Orders yet",
            style = MaterialTheme.typography.titleLarge.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        // Explore Categories button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppDimensions.RadiusM))
                .clickable(onClick = onExploreCategories),
            colors = CardDefaults.cardColors(containerColor = AppColors.Primary)
        ) {
            Text(
                text = "Explore Categories",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimensions.SpacingL),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AppColors.TextOnPrimary,
                    fontWeight = FontWeight.Bold
                )
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
            .clip(RoundedCornerShape(AppDimensions.RadiusL, AppDimensions.RadiusL, 0.dp, 0.dp))
            .background(AppColors.Surface)
            .padding(horizontal = AppDimensions.SpacingXXXL, vertical = AppDimensions.SpacingM),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val homeColor = if (selectedTab == "home") AppColors.Primary else AppColors.TextPrimary
        val ordersColor = if (selectedTab == "orders") AppColors.Primary else AppColors.TextPrimary
        val notificationsColor = if (selectedTab == "notifications") AppColors.Primary else AppColors.TextPrimary
        val profileColor = if (selectedTab == "profile") AppColors.Primary else AppColors.TextPrimary

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

