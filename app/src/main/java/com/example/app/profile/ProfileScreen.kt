package com.example.app.profile

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
fun ProfileScreen(
    userName: String = "Gilbert Jones",
    userEmail: String = "Gilbertjones001@gmail.com",
    userPhone: String = "121-224-7890",
    onPaymentMethodClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onRedemptionClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2D2D2D))
    ) {
        // Main content card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp)
                .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Header
                ProfileHeader(
                    userName = userName,
                    userEmail = userEmail,
                    userPhone = userPhone,
                    onEditClick = onEditProfileClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Menu Items
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Address",
                    onClick = onAddressClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Favorite,
                    title = "Wishlist",
                    onClick = onWishlistClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Payment",
                    onClick = onPaymentMethodClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Redemption",
                    onClick = onRedemptionClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "AI Assistant",
                    onClick = onSupportClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Sign Out Button
                Text(
                    text = "Sign Out",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSignOutClick),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFF0000)
                )

                // Add bottom padding to account for bottom navigation bar
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    userPhone: String,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8E8E8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color(0xFF999999),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = userName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email and Phone with Edit button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userPhone,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Edit",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF262626),
                modifier = Modifier.clickable(onClick = onEditClick)
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
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
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF262626),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF262626)
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color(0xFF262626)
            )
        }
    }
}

