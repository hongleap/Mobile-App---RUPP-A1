package com.example.app.notifications

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.notifications.data.Notification

@Composable
fun NotificationsScreen(
    notifications: List<Notification> = emptyList(),
    onExploreCategories: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    showBottomNav: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Text(
            text = "Notifications",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF262626)
        )

        if (notifications.isEmpty()) {
            // Empty state
            EmptyNotificationsState(onExploreCategories = onExploreCategories)
        } else {
            // Notifications list
            NotificationsList(notifications = notifications)
        }

        if (showBottomNav) {
            Spacer(modifier = Modifier.weight(1f))
            BottomNavBar(
                selectedTab = "notifications",
                onHomeClick = onHomeClick,
                onOrdersClick = onOrdersClick,
                onNotificationsClick = {},
                onProfileClick = onProfileClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EmptyNotificationsState(onExploreCategories: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Large bell icon
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFFFC107)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Notification yet",
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
private fun NotificationsList(notifications: List<Notification>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        notifications.forEach { notification ->
            NotificationCard(notification = notification)
        }
    }
}

@Composable
private fun NotificationCard(notification: Notification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bell icon with optional red dot
            Box(modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF666666)
                )
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Notification message
            Text(
                text = notification.message,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color = Color(0xFF262626),
                lineHeight = 20.sp
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
fun PreviewNotificationsScreenEmpty() {
    MaterialTheme {
        NotificationsScreen(notifications = emptyList())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotificationsScreenWithItems() {
    MaterialTheme {
        NotificationsScreen(
            notifications = listOf(
                Notification(message = "Gilbert, you placed and order check your order history for full details", isRead = false),
                Notification(message = "Gilbert, Thank you for shopping with us we have canceled order #24568,", isRead = true),
                Notification(message = "Gilbert, your Order #24568 has been confirmed check your order history for full d", isRead = true)
            )
        )
    }
}

