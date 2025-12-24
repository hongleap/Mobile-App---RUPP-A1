package com.example.app.payment

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.app.profile.data.PaymentMethod
import com.example.app.profile.data.PaymentMethodStorage

@Composable
fun AddCardScreen(
    onBackClick: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var cardNumber by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var exp by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Primary)
    ) {
        // White content card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = AppDimensions.RadiusXXL, topEnd = AppDimensions.RadiusXXL)),
            colors = CardDefaults.cardColors(containerColor = AppColors.Background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimensions.SpacingL)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        text = "Add Card",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

                // Card Number
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
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

                // CCV and Exp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
                ) {
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("CVV") },
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
                        value = exp,
                        onValueChange = { exp = it },
                        label = { Text("Exp") },
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

                // Cardholder Name
                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it },
                    label = { Text("Cardholder Name") },
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
                        text = "Set as default payment method",
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
                        .clickable(enabled = !isLoading && cardNumber.isNotBlank() && cvv.isNotBlank() && exp.isNotBlank() && cardholderName.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                val paymentMethod = PaymentMethod(
                                    cardNumber = cardNumber,
                                    cardholderName = cardholderName,
                                    expiryDate = exp,
                                    cvv = cvv,
                                    isDefault = isDefault
                                )
                                PaymentMethodStorage.addPaymentMethod(context, paymentMethod)
                                isLoading = false
                                onSave()
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLoading || cardNumber.isBlank() || cvv.isBlank() || exp.isBlank() || cardholderName.isBlank()) 
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
}

