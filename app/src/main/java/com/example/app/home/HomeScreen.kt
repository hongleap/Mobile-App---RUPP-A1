package com.example.app.home

import androidx.compose.runtime.collectAsState
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.app.blockchain.WalletManager
import com.example.app.blockchain.TokenService
import com.example.app.blockchain.config.TokenConfig
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import java.math.BigDecimal

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
    val context = LocalContext.current
    var tokenBalance by remember { mutableStateOf<BigDecimal?>(null) }
    
    // Load token balance
    LaunchedEffect(Unit) {
        val walletAddress = WalletManager.getWalletAddress(context)
        if (walletAddress != null) {
            TokenService.getTokenBalance(walletAddress).fold(
                onSuccess = { tokenBalance = it },
                onFailure = { tokenBalance = BigDecimal.ZERO }
            )
        } else {
            tokenBalance = BigDecimal.ZERO
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(scrollState)
            .padding(horizontal = AppDimensions.SpacingL)
    ) {
        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))
        HeaderRow(
            onCartClick = onCartClick,
            tokenBalance = tokenBalance
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))
        SearchBar(onSearchSubmit = onSearchSubmit, onFilterClick = onFilterClick)

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXL))
        
        // Promotional Banner
        PromotionBanner()
        
        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXL))
        SectionHeader(title = "Categories", action = "See All", onAction = onSeeAllCategories)
        Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
        CategoriesRow(onSelectCategory = onSelectCategory)

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXL))
        SectionHeader(title = "Top Selling", action = "See All", onAction = {})
        Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
        ProductCarousel(onProductClick = onProductClick)

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXL))
        SectionHeader(title = "New In", action = "See All", onAction = {})
        Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
        ProductCarousel(onProductClick = onProductClick)
        
        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXL))
        // Special Offers Section
        SpecialOffersSection()
        
        // Bottom padding for scroll
        Spacer(modifier = Modifier.height(120.dp))
    }
    
    // Bottom navigation bar and floating chat button (outside scrollable content)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showBottomNav) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimensions.SpacingL)
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
                .padding(bottom = 72.dp, start = AppDimensions.SpacingL),
            onClick = onChatClick
        )
    }
}

@Composable
private fun HeaderRow(
    onCartClick: () -> Unit = {},
    tokenBalance: BigDecimal? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppDimensions.SpacingXS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
    ) {
        // Profile card with avatar and CToken
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { },
            shape = RoundedCornerShape(AppDimensions.RadiusM),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = AppDimensions.SpacingM, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.Border),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = AppColors.TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                // Token balance text
                Text(
                    text = tokenBalance?.let { 
                        "${TokenService.formatTokenAmount(it)} ${TokenConfig.TOKEN_SYMBOL}"
                    } ?: "0 ${TokenConfig.TOKEN_SYMBOL}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }
        }

        // Gender button with dropdown
        Card(
            modifier = Modifier.clickable { },
            shape = RoundedCornerShape(AppDimensions.RadiusM),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = AppDimensions.SpacingL, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Men",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        // Cart icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AppColors.PrimaryLight)
                .clickable(onClick = onCartClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart, 
                contentDescription = "Cart", 
                tint = AppColors.Primary,
                modifier = Modifier.size(22.dp)
            )
        }
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
            .clip(RoundedCornerShape(AppDimensions.RadiusL))
            .background(AppColors.SurfaceVariant),
        placeholder = { Text("Search products...", color = AppColors.TextTertiary) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = AppColors.TextSecondary)
        },
        trailingIcon = {
            androidx.compose.material3.IconButton(onClick = onFilterClick) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Filter", tint = AppColors.TextSecondary)
            }
        },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearchSubmit(query) }
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.SurfaceVariant,
            unfocusedContainerColor = AppColors.SurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = AppColors.Primary
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
        Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AppColors.TextPrimary))
        if (action != null) Text(text = action, style = MaterialTheme.typography.bodyMedium.copy(color = AppColors.Primary), modifier = Modifier.clickable(onClick = onAction))
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
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingL), 
        contentPadding = PaddingValues(horizontal = AppDimensions.SpacingXS)
    ) {
        items(categories) { label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier
                        .size(64.dp)
                        .clickable { onSelectCategory(label) },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.ElevationLow)
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
                                color = AppColors.TextPrimary,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label, 
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun ProductCarousel(onProductClick: (String) -> Unit = {}) {
    val products = com.example.app.data.ProductRepository.products.collectAsState().value
    val displayProducts = products.take(3) // Show first 3 products
    
    LazyRow(horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingL)) {
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
            .size(width = 180.dp, height = 270.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(AppDimensions.RadiusM),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.ElevationLow)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(AppColors.SurfaceVariant)
            ) {
                com.example.app.ui.ProductImage(
                    productId = productId,
                    category = category,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Favorite button
                Box(
                    modifier = Modifier
                        .padding(AppDimensions.SpacingS)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.Surface.copy(alpha = 0.8f))
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = AppColors.TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(AppDimensions.SpacingM)) {
                Text(
                    text = title, 
                    fontSize = 14.sp, 
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                Text(
                    text = price, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 16.sp,
                    color = AppColors.Primary
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
                // Stock display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (stock > 0) AppColors.Success else AppColors.Error)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (stock > 0) "In Stock: $stock" else "Out of Stock",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stock > 0) AppColors.Success else AppColors.Error
                    )
                }
            }
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
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.ElevationMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.SpacingXXXL, vertical = AppDimensions.SpacingL),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val homeColor = if (selectedTab == "home") AppColors.Primary else AppColors.TextSecondary
            val ordersColor = if (selectedTab == "orders") AppColors.Primary else AppColors.TextSecondary
            val notificationsColor = if (selectedTab == "notifications") AppColors.Primary else AppColors.TextSecondary
            val profileColor = if (selectedTab == "profile") AppColors.Primary else AppColors.TextSecondary

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
            .height(160.dp),
        shape = RoundedCornerShape(AppDimensions.RadiusL),
        colors = CardDefaults.cardColors(containerColor = AppColors.Primary)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background pattern or image could go here
            Column(
                modifier = Modifier
                    .padding(AppDimensions.SpacingXXL)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Summer Sale",
                    color = AppColors.TextOnPrimary.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
                Text(
                    text = "50% OFF",
                    color = AppColors.TextOnPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Surface),
                    shape = RoundedCornerShape(AppDimensions.RadiusS),
                    contentPadding = PaddingValues(horizontal = AppDimensions.SpacingL, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Shop Now",
                        color = AppColors.Primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
        colors = CardDefaults.cardColors(containerColor = AppColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.ElevationHigh)
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
        Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
        
        val offers = listOf(
            Offer("Flash Sale", "Up to 50% OFF", Color(0xFFFF6B6B)),
            Offer("Free Shipping", "On orders over $100", Color(0xFF4ECDC4)),
            Offer("New Collection", "Shop now", Color(0xFFFFD93D))
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
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
        shape = RoundedCornerShape(AppDimensions.RadiusL),
        colors = CardDefaults.cardColors(containerColor = offer.color),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.ElevationLow)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimensions.SpacingL),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = offer.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
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



