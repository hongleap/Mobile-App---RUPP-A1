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
import androidx.compose.foundation.layout.width
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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val size: String = "",
    val color: String = "",
    val category: String = "",
    val imageUrl: String? = null
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
            .background(AppColors.Background)
    ) {
        // Header with back button, title, and Remove All
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingL),
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
                text = "Cart",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
            if (cartItems.isNotEmpty()) {
                Text(
                    text = "Remove All",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextPrimary
                    ),
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
            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
        }
    }
}

@Composable
private fun EmptyCartState(onExploreCategories: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.SpacingXXXL),
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
                tint = AppColors.Primary
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        Text(
            text = "Your cart is empty",
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
            .padding(horizontal = AppDimensions.SpacingL)
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
            Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
        }

        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))

        // Order Summary
        OrderSummarySection(
            subtotal = subtotal,
            shippingCost = shippingCost,
            tax = tax,
            total = total
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))

        // Coupon Code Input
        CouponCodeInput()

        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))

        // Checkout Button
        CheckoutButton(
            onCheckout = onCheckout,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))
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
            .clip(RoundedCornerShape(AppDimensions.RadiusM)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.SpacingL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(AppDimensions.RadiusS))
            ) {
                com.example.app.ui.ProductImage(
                    productId = item.id,
                    category = item.category,
                    imageUrl = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(AppDimensions.SpacingM))

            // Product details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                if (item.size.isNotEmpty() || item.color.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                    Text(
                        text = buildString {
                            if (item.size.isNotEmpty()) append("Size - ${item.size}")
                            if (item.size.isNotEmpty() && item.color.isNotEmpty()) append(", ")
                            if (item.color.isNotEmpty()) append("Color - ${item.color}")
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Quantity controls and delete button - vertical layout
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingS)
            ) {
                // Delete button
                IconButton(
                    onClick = onRemoveItem,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.AccentRed.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        tint = AppColors.AccentRed,
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
                        .background(AppColors.Primary)
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AppColors.TextOnPrimary
                        )
                    )
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
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
                        .background(AppColors.Primary)
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AppColors.TextOnPrimary
                        )
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
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary
                )
            )
            Text(
                text = "$${String.format("%.2f", subtotal)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Shipping Cost",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary
                )
            )
            Text(
                text = "$${String.format("%.2f", shippingCost)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tax",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary
                )
            )
            Text(
                text = "$${String.format("%.2f", tax)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "$${String.format("%.2f", total)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
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
            .clip(RoundedCornerShape(AppDimensions.RadiusM))
            .clickable { /* Open coupon input */ },
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
                // Coupon icon (green tag icon)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AppColors.PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.Primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = if (couponCode.isEmpty()) "Enter Coupon Code" else couponCode,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (couponCode.isEmpty()) AppColors.TextTertiary else AppColors.TextPrimary
                    )
                )
            }
            IconButton(
                onClick = { /* Apply coupon */ },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Apply",
                    tint = AppColors.TextOnPrimary
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
            .clip(RoundedCornerShape(AppDimensions.RadiusM))
            .clickable(onClick = onCheckout),
        colors = CardDefaults.cardColors(containerColor = AppColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = "Checkout",
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
            .clip(RoundedCornerShape(AppDimensions.RadiusL, AppDimensions.RadiusL, 0.dp, 0.dp))
            .background(AppColors.Surface)
            .padding(horizontal = AppDimensions.SpacingXXXL, vertical = AppDimensions.SpacingM),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Home",
            tint = AppColors.TextPrimary,
            modifier = Modifier.clickable(onClick = onHomeClick)
        )
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = AppColors.TextPrimary,
            modifier = Modifier.clickable(onClick = onNotificationsClick)
        )
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = "Orders",
            tint = AppColors.TextPrimary,
            modifier = Modifier.clickable(onClick = onOrdersClick)
        )
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile",
            tint = AppColors.TextPrimary,
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

