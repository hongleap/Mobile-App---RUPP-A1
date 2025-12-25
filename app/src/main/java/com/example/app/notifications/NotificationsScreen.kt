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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
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
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Notifications",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingL),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
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
            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
        }
    }
}

@Composable
private fun EmptyNotificationsState(onExploreCategories: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.SpacingXXXL),
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
                tint = AppColors.Primary
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        Text(
            text = "No Notification yet",
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
private fun NotificationsList(notifications: List<Notification>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.SpacingL),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
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
            .clip(RoundedCornerShape(AppDimensions.RadiusM)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.SpacingL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bell icon with optional red dot
            Box(modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimensions.IconLarge),
                    tint = AppColors.TextTertiary
                )
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AppColors.AccentRed)
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppDimensions.SpacingL))

            // Notification message
            Text(
                text = notification.message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary,
                    lineHeight = 20.sp
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

