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
                text = "Payment",
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

            // Cards Section
            Text(
                text = "Cards",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (paymentMethods.isEmpty()) {
                Text(
                    text = "No payment methods saved. Tap + to add one.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                paymentMethods.forEach { method ->
                    PaymentCardItem(
                        method = method,
                        onClick = { onCardClick(method.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CToken Message
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Spend 10$, Receive 1 CToken. ",
                    fontSize = 14.sp,
                    color = Color(0xFF262626)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Add button
        FloatingActionButton(
            onClick = onAddCard,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End),
            containerColor = Color(0xFF262626)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Payment Method",
                tint = Color.White
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
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = maskedNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF262626)
                    )
                    // Card type icon placeholder
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEB001B))
                    )
                }
                if (method.cardholderName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = method.cardholderName,
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
                if (method.expiryDate.isNotEmpty()) {
                    Text(
                        text = "Expires: ${method.expiryDate}",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                if (method.isDefault) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Default",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Select",
                tint = Color(0xFF262626)
            )
        }
    }
}
