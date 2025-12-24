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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

@Composable
fun ProfileScreen(
    userName: String = "Gilbert Jones",
    userEmail: String = "Gilbertjones001@gmail.com",
    userPhone: String = "121-224-7890",
    onPaymentMethodClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Main content card
        Card(
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = AppColors.Background),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = AppDimensions.SpacingL)
            ) {
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

                // Profile Header
                ProfileHeader(
                    userName = userName,
                    userEmail = userEmail,
                    userPhone = userPhone,
                    onEditClick = onEditProfileClick
                )

                Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

                // Menu Items
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Address",
                    onClick = onAddressClick
                )

                Spacer(modifier = Modifier.height(AppDimensions.SpacingM))

                ProfileMenuItem(
                    icon = Icons.Default.Favorite,
                    title = "Wishlist",
                    onClick = onWishlistClick
                )

                Spacer(modifier = Modifier.height(AppDimensions.SpacingM))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Payment",
                    onClick = onPaymentMethodClick
                )

                Spacer(modifier = Modifier.height(AppDimensions.SpacingM))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Wallet",
                    onClick = onWalletClick
                )

                Spacer(modifier = Modifier.height(AppDimensions.SpacingM))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "AI Assistant",
                    onClick = onSupportClick
                )

                Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

                // Sign Out Button
                Text(
                    text = "Sign Out",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSignOutClick),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.AccentRed,
                        fontWeight = FontWeight.Bold
                    )
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
                .background(AppColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = AppColors.TextTertiary,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))

        // Name
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingS))

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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                Text(
                    text = userPhone,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextSecondary
                    )
                )
            }
            Spacer(modifier = Modifier.width(AppDimensions.SpacingL))
            Text(
                text = "Edit",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Primary,
                    fontWeight = FontWeight.Bold
                ),
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
            .clip(RoundedCornerShape(AppDimensions.RadiusM))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.SpacingL),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(AppDimensions.IconMedium)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = AppColors.TextTertiary
            )
        }
    }
}

