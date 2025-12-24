package com.example.app.address

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
import com.example.app.profile.data.Address
import com.example.app.profile.data.UserProfileRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun AddressScreen(
    onBackClick: () -> Unit = {},
    onEditAddress: (String) -> Unit = {},
    onAddAddress: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var addresses by remember { mutableStateOf<List<Address>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    fun loadAddresses() {
        scope.launch {
            isLoading = true
            val result = UserProfileRepository.getAddresses()
            result.onSuccess {
                addresses = it
            }.onFailure {
                // Handle error
            }
            isLoading = false
        }
    }
    
    LaunchedEffect(Unit) {
        loadAddresses()
    }

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
                text = "Address",
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
                .padding(horizontal = AppDimensions.SpacingL)
        ) {
            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))

            if (isLoading) {
                Text(
                    text = "Loading addresses...",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = AppColors.TextSecondary
                    ),
                    modifier = Modifier.padding(AppDimensions.SpacingL)
                )
            } else if (addresses.isEmpty()) {
                Text(
                    text = "No addresses saved. Tap + to add one.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = AppColors.TextSecondary
                    ),
                    modifier = Modifier.padding(AppDimensions.SpacingL)
                )
            } else {
                addresses.forEach { address ->
                    AddressItem(
                        address = address,
                        onEdit = { onEditAddress(address.id) }
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))
        }
        
        // Add button
        FloatingActionButton(
            onClick = onAddAddress,
            modifier = Modifier
                .padding(AppDimensions.SpacingL)
                .align(Alignment.End),
            containerColor = AppColors.Primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Address",
                tint = AppColors.TextOnPrimary
            )
        }
    }
}

@Composable
private fun AddressItem(
    address: Address,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimensions.RadiusL)),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.SpacingL),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address.addressLine1,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextSecondary
                    )
                )
                if (address.addressLine2.isNotEmpty()) {
                    Text(
                        text = address.addressLine2,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                }
                Text(
                    text = "${address.city}, ${address.state} ${address.zipCode}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextSecondary
                    )
                )
                Text(
                    text = address.country,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextSecondary
                    )
                )
                if (address.phoneNumber.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                    Text(
                        text = "Phone: ${address.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                }
                if (address.isDefault) {
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                    Text(
                        text = "Default Address",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary
                        )
                    )
                }
            }
            Text(
                text = "Edit",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable(onClick = onEdit)
            )
        }
    }
}
