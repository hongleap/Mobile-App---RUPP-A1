package com.example.app.home

import androidx.compose.runtime.collectAsState

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R

@Composable
fun HomeScreen(
    onSeeAllCategories: () -> Unit = {},
    onSelectCategory: (String) -> Unit = {},
    onCartClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchSubmit: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    showBottomNav: Boolean = true
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderRow(onCartClick = onCartClick)

        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(onSearchSubmit = onSearchSubmit, onFilterClick = onFilterClick)

        Spacer(modifier = Modifier.height(20.dp))
        
        // Promotional Banner
        PromotionBanner()
        
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "Categories", action = "See All", onAction = onSeeAllCategories)
        Spacer(modifier = Modifier.height(12.dp))
        CategoriesRow(onSelectCategory = onSelectCategory)

        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "Top Selling", action = "See All", onAction = {})
        Spacer(modifier = Modifier.height(12.dp))
        ProductCarousel(onProductClick = onProductClick)

        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "New In", action = "See All", onAction = {})
        Spacer(modifier = Modifier.height(12.dp))
        ProductCarousel(onProductClick = onProductClick)
        
        Spacer(modifier = Modifier.height(20.dp))
        // Special Offers Section
        SpecialOffersSection()
        
        // Bottom padding for scroll
        Spacer(modifier = Modifier.height(80.dp))
    }
    
    // Bottom navigation bar and floating chat button (outside scrollable content)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showBottomNav) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                BottomNavBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedTab = "home",
                    onHomeClick = {},
                    onOrdersClick = onOrdersClick,
                    onNotificationsClick = onNotificationsClick,
                    onProfileClick = onProfileClick
                )
            }
        }
        
        // Floating Chat Button - Bottom Left (always visible on home screen)
        FloatingChatButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 72.dp, start = 16.dp),
            onClick = onChatClick
        )
    }
}

@Composable
private fun HeaderRow(onCartClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Profile card with avatar and CToken
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8E8E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(24.dp)
                    )
                }
                // CToken text
                Text(
                    text = "69 CToken",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF262626)
                )
            }
        }

        // Gender button with dropdown
        Card(
            modifier = Modifier.clickable { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Men",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF262626)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        // Cart icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDEAFD))
                .clickable(onClick = onCartClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart, 
                contentDescription = "Cart", 
                tint = Color(0xFF6B5BFF),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun Chip(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color(0xFF262626),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SearchBar(onSearchSubmit: (String) -> Unit = {}, onFilterClick: () -> Unit = {}) {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF4F4F4)),
        placeholder = { Text("Search") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = Color(0xFF666666))
        },
        trailingIcon = {
            androidx.compose.material3.IconButton(onClick = onFilterClick) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Filter", tint = Color(0xFF666666))
            }
        },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearchSubmit(query) }
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF4F4F4),
            unfocusedContainerColor = Color(0xFFF4F4F4),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SectionHeader(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF262626))
        if (action != null) Text(text = action, color = Color(0xFF6B5BFF), modifier = Modifier.clickable(onClick = onAction))
    }
}

@Composable
private fun CategoriesRow(onSelectCategory: (String) -> Unit) {
    val categories = listOf("Hoodies", "Jackets", "T-Shirts", "Pants", "Shoes", "Accessories")
    
    // Map category names to image resource names (must be lowercase for Android resources)
    fun getCategoryImageName(category: String): String {
        return when (category) {
            "Hoodies" -> "hoodie"
            "Jackets" -> "jacket"
            "T-Shirts" -> "t_shirt"
            "Pants" -> "pants"
            "Shoes" -> "shoes"
            "Accessories" -> "accessories"
            else -> "product"
        }
    }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp), 
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier
                        .size(64.dp)
                        .clickable { onSelectCategory(label) },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    val imageName = getCategoryImageName(label)
                    val context = LocalContext.current
                    val imageResourceId = context.resources.getIdentifier(
                        imageName.lowercase(),
                        "drawable",
                        context.packageName
                    )
                    
                    if (imageResourceId != 0) {
                        Image(
                            painter = painterResource(id = imageResourceId),
                            contentDescription = label,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label.first().toString(), 
                                color = Color(0xFF262626),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label, 
                    fontSize = 12.sp, 
                    color = Color(0xFF262626),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ProductCarousel(onProductClick: (String) -> Unit = {}) {
    val products = com.example.app.data.ProductRepository.products.collectAsState().value
    val displayProducts = products.take(3) // Show first 3 products
    
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(displayProducts) { product ->
            ProductCardSmall(
                title = product.name,
                price = product.price,
                productId = product.id,
                category = product.category,
                stock = product.stock,
                onClick = { onProductClick(product.name) }
            )
        }
    }
}

@Composable
private fun ProductCardSmall(
    title: String,
    price: String,
    productId: String? = null,
    category: String? = null,
    stock: Int = 0,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .size(width = 180.dp, height = 260.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            com.example.app.ui.ProductImage(
                productId = productId,
                category = category,
                contentDescription = title,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title, 
                fontSize = 14.sp, 
                color = Color(0xFF262626),
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = price, 
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp,
                color = Color(0xFF262626)
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Stock display
            Text(
                text = if (stock > 0) "In Stock: $stock" else "No Stock",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (stock > 0) Color(0xFF28A745) else Color(0xFFDC3545)
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    modifier: Modifier = Modifier,
    selectedTab: String = "home",
    onHomeClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val homeColor = if (selectedTab == "home") Color(0xFF6B5BFF) else Color(0xFF666666)
            val ordersColor = if (selectedTab == "orders") Color(0xFF6B5BFF) else Color(0xFF666666)
            val notificationsColor = if (selectedTab == "notifications") Color(0xFF6B5BFF) else Color(0xFF666666)
            val profileColor = if (selectedTab == "profile") Color(0xFF6B5BFF) else Color(0xFF666666)

        Box {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = homeColor,
                modifier = Modifier.clickable(onClick = onHomeClick)
            )
            if (selectedTab == "home") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 24.dp, height = 2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF262626))
                        .padding(top = 20.dp)
                )
            }
        }
        
        Box {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Orders",
                tint = ordersColor,
                modifier = Modifier.clickable(onClick = onOrdersClick)
            )
            if (selectedTab == "orders") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 24.dp, height = 2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF262626))
                        .padding(top = 20.dp)
                )
            }
        }
        
        Box {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = notificationsColor,
                modifier = Modifier.clickable(onClick = onNotificationsClick)
            )
            if (selectedTab == "notifications") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 24.dp, height = 2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFF262626))
                        .padding(top = 20.dp)
                )
            }
        }
        
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile",
            tint = profileColor,
            modifier = Modifier.clickable(onClick = onProfileClick)
        )
        }
    }
}

// Exposed simple bottom bar for root to keep it persistent
@Composable
fun BottomNavForRoot(
    selectedTab: String,
    onHomeClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
        selectedTab = when (selectedTab) {
            "orders" -> "orders"
            "notifications" -> "notifications"
            "profile" -> "profile"
            else -> "home"
        },
        onHomeClick = onHomeClick,
        onOrdersClick = onOrdersClick,
        onNotificationsClick = onNotificationsClick,
        onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun PromotionBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6B5BFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = "Special Offer",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get 30% OFF",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "On all new arrivals",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun FloatingChatButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .size(56.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6B5BFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💬",
                fontSize = 28.sp
            )
        }
    }
}

@Composable
private fun SpecialOffersSection() {
    Column {
        SectionHeader(title = "Special Offers", action = "See All", onAction = {})
        Spacer(modifier = Modifier.height(12.dp))
        
        val offers = listOf(
            Offer("Flash Sale", "Up to 50% OFF", Color(0xFFFF6B6B)),
            Offer("Free Shipping", "On orders over $100", Color(0xFF4ECDC4)),
            Offer("New Collection", "Shop now", Color(0xFFFFD93D))
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(offers) { offer ->
                OfferCard(offer = offer)
            }
        }
    }
}

data class Offer(
    val title: String,
    val subtitle: String,
    val color: Color
)

@Composable
private fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = offer.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = offer.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = offer.subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen()
    }
}



