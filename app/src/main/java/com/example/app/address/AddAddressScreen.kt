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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.app.profile.data.Address
import com.example.app.profile.data.UserProfileRepository

@Composable
fun AddAddressScreen(
    onBackClick: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var addressLine1 by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

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
                text = "Add Address",
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
                .padding(horizontal = AppDimensions.SpacingL)
        ) {
            Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address Line 1
            OutlinedTextField(
                value = addressLine1,
                onValueChange = { addressLine1 = it },
                label = { Text("Address Line 1") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address Line 2 (Optional)
            OutlinedTextField(
                value = addressLine2,
                onValueChange = { addressLine2 = it },
                label = { Text("Address Line 2 (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // City
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // State and Zip Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
            ) {
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AppColors.SurfaceVariant,
                        unfocusedContainerColor = AppColors.SurfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(AppDimensions.RadiusM)
                )
                OutlinedTextField(
                    value = zipCode,
                    onValueChange = { zipCode = it },
                    label = { Text("Zip Code") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AppColors.SurfaceVariant,
                        unfocusedContainerColor = AppColors.SurfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(AppDimensions.RadiusM)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Country
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Default checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it }
                )
                Text(
                    text = "Set as default address",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.RadiusM))
                    .clickable(enabled = !isLoading && fullName.isNotBlank() && addressLine1.isNotBlank() && city.isNotBlank() && state.isNotBlank() && zipCode.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            val address = Address(
                                fullName = fullName,
                                phoneNumber = phoneNumber,
                                addressLine1 = addressLine1,
                                addressLine2 = addressLine2,
                                city = city,
                                state = state,
                                zipCode = zipCode,
                                country = country,
                                isDefault = isDefault
                            )
                            val result = UserProfileRepository.saveAddress(address)
                            result.onSuccess {
                                isLoading = false
                                onSave()
                            }.onFailure {
                                isLoading = false
                                // Handle error
                            }
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isLoading || fullName.isBlank() || addressLine1.isBlank() || city.isBlank() || state.isBlank() || zipCode.isBlank()) 
                        AppColors.TextTertiary else AppColors.Primary
                )
            ) {
                Text(
                    text = if (isLoading) "Saving..." else "Save",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimensions.SpacingL),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextOnPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))
        }
    }
}

