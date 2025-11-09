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
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF262626)
                )
            }
            Text(
                text = "Address",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF262626)
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Text(
                    text = "Loading addresses...",
                    fontSize = 16.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(16.dp)
                )
            } else if (addresses.isEmpty()) {
                Text(
                    text = "No addresses saved. Tap + to add one.",
                    fontSize = 16.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                addresses.forEach { address ->
                    AddressItem(
                        address = address,
                        onEdit = { onEditAddress(address.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Add button
        FloatingActionButton(
            onClick = onAddAddress,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End),
            containerColor = Color(0xFF262626)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Address",
                tint = Color.White
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
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF262626)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address.addressLine1,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                if (address.addressLine2.isNotEmpty()) {
                    Text(
                        text = address.addressLine2,
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
                Text(
                    text = "${address.city}, ${address.state} ${address.zipCode}",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = address.country,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                if (address.phoneNumber.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phone: ${address.phoneNumber}",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
                if (address.isDefault) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Default Address",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            Text(
                text = "Edit",
                fontSize = 14.sp,
                color = Color(0xFF262626),
                modifier = Modifier.clickable(onClick = onEdit)
            )
        }
    }
}
