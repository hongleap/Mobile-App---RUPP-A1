package com.example.app.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val size: String = "",
    val color: String = "",
    val category: String = ""
)

@Composable
fun CartScreen(
    cartItems: List<CartItem> = emptyList(),
    onExploreCategories: () -> Unit = {},
    onCheckout: () -> Unit = {},
    onQuantityChange: (String, Int) -> Unit = { _, _ -> },
    onRemoveItem: (String) -> Unit = { _ -> },
    onRemoveAll: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showBottomNav: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header with back button, title, and Remove All
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
                text = "Cart",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF262626)
            )
            if (cartItems.isNotEmpty()) {
                Text(
                    text = "Remove All",
                    fontSize = 14.sp,
                    color = Color(0xFF262626),
                    modifier = Modifier.clickable(onClick = onRemoveAll)
                )
            } else {
                Spacer(modifier = Modifier.size(60.dp))
            }
        }

        if (cartItems.isEmpty()) {
            // Empty cart state
            Spacer(modifier = Modifier.weight(1f))
            EmptyCartState(onExploreCategories = onExploreCategories)
        } else {
            // Cart items list - scrollable
            CartItemsList(
                items = cartItems,
                onCheckout = onCheckout,
                onQuantityChange = onQuantityChange,
                onRemoveItem = onRemoveItem,
                modifier = Modifier.weight(1f)
            )
        }

        if (showBottomNav) {
            BottomNavBar(
                onHomeClick = {},
                onOrdersClick = {},
                onNotificationsClick = {},
                onProfileClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EmptyCartState(onExploreCategories: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Shopping cart icon
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFFF9800)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your cart is empty",
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
private fun CartItemsList(
    items: List<CartItem>,
    onCheckout: () -> Unit,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val subtotal = items.sumOf { it.price * it.quantity }
    val shippingCost = 8.00
    val tax = 0.00
    val total = subtotal + shippingCost + tax
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // Cart items
        items.forEach { item ->
            CartItemRow(
                item = item,
                onQuantityChange = { newQuantity ->
                    onQuantityChange(item.id, newQuantity)
                },
                onRemoveItem = {
                    onRemoveItem(item.id)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order Summary
        OrderSummarySection(
            subtotal = subtotal,
            shippingCost = shippingCost,
            tax = tax,
            total = total
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Coupon Code Input
        CouponCodeInput()

        Spacer(modifier = Modifier.height(16.dp))

        // Checkout Button
        CheckoutButton(
            onCheckout = onCheckout,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit
) {
    var quantity by remember { mutableStateOf(item.quantity) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8E8E8))
            )

            Spacer(modifier = Modifier.size(12.dp))

            // Product details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF262626)
                )
                if (item.size.isNotEmpty() || item.color.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildString {
                            if (item.size.isNotEmpty()) append("Size - ${item.size}")
                            if (item.size.isNotEmpty() && item.color.isNotEmpty()) append(", ")
                            if (item.color.isNotEmpty()) append("Color - ${item.color}")
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF262626)
                )
            }

            // Quantity controls and delete button - vertical layout
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Delete button
                IconButton(
                    onClick = onRemoveItem,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF5252))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Quantity controls
                IconButton(
                    onClick = {
                        quantity++
                        onQuantityChange(quantity)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2D2D2D))
                ) {
                    Text(
                        text = "+",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF262626)
                )
                IconButton(
                    onClick = {
                        if (quantity > 1) {
                            quantity--
                            onQuantityChange(quantity)
                        } else {
                            // If quantity is 1, remove the item
                            onRemoveItem()
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2D2D2D))
                ) {
                    Text(
                        text = "−",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderSummarySection(
    subtotal: Double,
    shippingCost: Double,
    tax: Double,
    total: Double
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Subtotal",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", subtotal)}",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Shipping Cost",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", shippingCost)}",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tax",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", tax)}",
                fontSize = 14.sp,
                color = Color(0xFF262626)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            Text(
                text = "$${String.format("%.2f", total)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
        }
    }
}

@Composable
private fun CouponCodeInput() {
    var couponCode by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { /* Open coupon input */ },
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Coupon icon (green tag icon)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (couponCode.isEmpty()) "Enter Coupon Code" else couponCode,
                    fontSize = 14.sp,
                    color = if (couponCode.isEmpty()) Color(0xFF999999) else Color(0xFF262626)
                )
            }
            IconButton(
                onClick = { /* Apply coupon */ },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D2D2D))
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Apply",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun CheckoutButton(
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onCheckout),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = "Checkout",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun BottomNavBar(
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
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Home",
            tint = Color(0xFF262626),
            modifier = Modifier.clickable(onClick = onHomeClick)
        )
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = Color(0xFF262626),
            modifier = Modifier.clickable(onClick = onNotificationsClick)
        )
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = "Orders",
            tint = Color(0xFF262626),
            modifier = Modifier.clickable(onClick = onOrdersClick)
        )
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile",
            tint = Color(0xFF262626),
            modifier = Modifier.clickable(onClick = onProfileClick)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartScreenEmpty() {
    MaterialTheme {
        CartScreen(cartItems = emptyList())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartScreenWithItems() {
    MaterialTheme {
        CartScreen(
            cartItems = listOf(
                CartItem("1", "Men's Harrington Jacket", 148.00, 1),
                CartItem("2", "Max Cirro Men's Slides", 55.00, 2)
            )
        )
    }
}

