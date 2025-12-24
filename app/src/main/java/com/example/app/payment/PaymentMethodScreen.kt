package com.example.app.payment

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
import com.example.app.profile.data.PaymentMethod
import com.example.app.profile.data.PaymentMethodStorage

@Composable
fun PaymentMethodScreen(
    onBackClick: () -> Unit = {},
    onCardClick: (String) -> Unit = {},
    onAddCard: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var paymentMethods by remember { mutableStateOf<List<PaymentMethod>>(emptyList()) }
    
    fun loadPaymentMethods() {
        paymentMethods = PaymentMethodStorage.getPaymentMethods(context)
    }
    
    LaunchedEffect(Unit) {
        loadPaymentMethods()
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
                .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingXXXL),
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
                text = "Payment",
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

            // Cards Section
            Text(
                text = "Cards",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                ),
                modifier = Modifier.padding(vertical = AppDimensions.SpacingS)
            )

            if (paymentMethods.isEmpty()) {
                Text(
                    text = "No payment methods saved. Tap + to add one.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextSecondary
                    ),
                    modifier = Modifier.padding(vertical = AppDimensions.SpacingL)
                )
            } else {
                paymentMethods.forEach { method ->
                    PaymentCardItem(
                        method = method,
                        onClick = { onCardClick(method.id) }
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

            // CToken Message
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimensions.SpacingL),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Spend 10$, Receive 1 CToken. ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))
        }
        
        // Add button
        FloatingActionButton(
            onClick = onAddCard,
            modifier = Modifier
                .padding(AppDimensions.SpacingL)
                .align(Alignment.End),
            containerColor = AppColors.Primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Payment Method",
                tint = AppColors.TextOnPrimary
            )
        }
    }
}

@Composable
private fun PaymentCardItem(
    method: PaymentMethod,
    onClick: () -> Unit
) {
    val maskedNumber = if (method.cardNumber.length >= 4) {
        "**** ${method.cardNumber.takeLast(4)}"
    } else {
        "****"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimensions.RadiusL))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.SpacingL),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
                ) {
                    Text(
                        text = maskedNumber,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    )
                    // Card type icon placeholder
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.IconL)
                            .clip(RoundedCornerShape(AppDimensions.RadiusXS))
                            .background(AppColors.AccentRed)
                    )
                }
                if (method.cardholderName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                    Text(
                        text = method.cardholderName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                }
                if (method.expiryDate.isNotEmpty()) {
                    Text(
                        text = "Expires: ${method.expiryDate}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                }
                if (method.isDefault) {
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                    Text(
                        text = "Default",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary
                        )
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Select",
                tint = AppColors.TextPrimary
            )
        }
    }
}
