package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.app.cart.CartScreen
import com.example.app.home.CategoriesScreen
import com.example.app.home.CategoryDetailScreen
import com.example.app.home.HomeScreen
import com.example.app.login.CreateAccountScreen
import com.example.app.login.ForgotPasswordScreen
import com.example.app.login.Login
import com.example.app.login.OnboardingScreen
import com.example.app.login.PasswordResetConfirmationScreen
import com.example.app.auth.AuthRepository
import androidx.compose.runtime.collectAsState
import com.example.app.notifications.NotificationsScreen
import com.example.app.notifications.data.Notification
import com.example.app.orders.OrdersScreen
import com.example.app.orders.data.Order
import com.example.app.orders.data.OrderRepository
import com.example.app.orders.data.OrderItem
import com.example.app.notifications.data.NotificationRepository
import com.example.app.data.StockRepository
import com.example.app.orders.TrackOrderScreen
import com.example.app.search.SearchProduct
import com.example.app.search.SearchResultsScreen
import com.example.app.search.SearchScreen
import com.example.app.product.Product
import com.example.app.product.ProductColor
import com.example.app.product.ProductPageScreen
import com.example.app.checkout.CheckoutScreen
import com.example.app.checkout.QRCodeScreen
import com.example.app.checkout.OrderPlacedScreen
import com.example.app.payment.PaymentMethodScreen
import com.example.app.payment.AddCardScreen
import com.example.app.address.AddressScreen
import com.example.app.address.AddAddressScreen
import com.example.app.wishlist.WishlistScreen
import com.example.app.wishlist.MyFavouritesScreen
import com.example.app.profile.ProfileScreen
import com.example.app.chat.ChatScreen
import com.example.app.blockchain.WalletScreen
import com.example.app.blockchain.TokenTransferScreen
import com.example.app.chat.config.ApiConfig
import com.example.app.data.ProductRepository
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val colorScheme = if (isDarkTheme) com.example.app.ui.AppDarkColorScheme else com.example.app.ui.AppLightColorScheme
            
            androidx.compose.material3.MaterialTheme(
                colorScheme = colorScheme,
                typography = com.example.app.ui.AppTypography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Check authentication state
    val currentUser by AuthRepository.currentUser.collectAsState()
    val isUserSignedIn = currentUser != null
    
    // Simple in-file navigation across login-related screens
    var currentScreen by remember { mutableStateOf(if (isUserSignedIn) "home" else "login") }
    var previousScreen by remember { mutableStateOf("") }
    var selectedPaymentType by remember { mutableStateOf<String?>(null) } // "card", "token", "bank"
    var paymentVerified by remember { mutableStateOf(false) } // Track if token payment is verified
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Listen to auth state changes
    LaunchedEffect(isUserSignedIn) {
        if (isUserSignedIn) {
            // User signed in - navigate to home from any login screen
            if (currentScreen in setOf("login", "password", "create_account", "forgot_password", "reset_confirmation", "onboarding")) {
                currentScreen = "home"
            }
        } else {
            // User signed out - navigate to login (but not if already on login screens)
            if (currentScreen !in setOf("login", "password", "create_account", "forgot_password", "reset_confirmation", "onboarding")) {
                currentScreen = "login"
            }
        }
    }
    
    // Notifications from Firestore
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    
    // Cart items
    var cartItems by remember { mutableStateOf(listOf<com.example.app.cart.CartItem>()) }
    
    // Orders from Firestore
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    
    var selectedOrderId by remember { mutableStateOf("") }
    
    // Load products from Firestore
    val scope = rememberCoroutineScope()
    val productsState = ProductRepository.products.collectAsState()
    
    // Load orders and notifications from Firestore
    LaunchedEffect(isUserSignedIn) {
        if (isUserSignedIn) {
            scope.launch {
                // Load orders
                val ordersResult = OrderRepository.getOrders()
                ordersResult.onSuccess {
                    orders = it
                }.onFailure {
                    // Handle error - orders will be empty list
                    orders = emptyList()
                }
                
                // Load notifications
                val notificationsResult = NotificationRepository.getNotifications()
                notificationsResult.onSuccess {
                    notifications = it
                }.onFailure {
                    // Handle error - notifications will be empty list
                    notifications = emptyList()
                }
            }
        } else {
            // Clear orders and notifications when signed out
            orders = emptyList()
            notifications = emptyList()
        }
    }
    
    // Reload orders and notifications when screen changes
    LaunchedEffect(currentScreen) {
        if (isUserSignedIn && (currentScreen == "orders" || currentScreen == "notifications")) {
            scope.launch {
                if (currentScreen == "orders") {
                    val ordersResult = OrderRepository.getOrders()
                    ordersResult.onSuccess {
                        orders = it
                    }.onFailure {
                        // Handle error
                        orders = emptyList()
                    }
                } else if (currentScreen == "notifications") {
                    val notificationsResult = NotificationRepository.getNotifications()
                    notificationsResult.onSuccess {
                        notifications = it
                    }.onFailure {
                        // Handle error
                        notifications = emptyList()
                    }
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        ProductRepository.loadProducts()
    }
    
    // Search state - now using products from Firestore
    val allProducts = remember(productsState.value) {
        ProductRepository.getProductsAsSearchProducts()
    }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(allProducts) }
    var searchResultsSource by remember { mutableStateOf("") } // Track where we came from: "home" or "search"
    
    // Product page state
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    
    // Orders refresh state
    var isRefreshingOrders by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Login screens
        AnimatedVisibility(
            visible = currentScreen == "login",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { -it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Login(
                onContinue = { 
                    // Navigation handled by LaunchedEffect(isUserSignedIn) 
                },
                onCreateAccountClick = { currentScreen = "create_account" },
                onForgotPasswordClick = { currentScreen = "forgot_password" },
                onError = { error -> errorMessage = error }
            )
        }
        
        // Show error message popup
        errorMessage?.let { error ->
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("Authentication Error") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { errorMessage = null }) {
                        Text("Dismiss")
                    }
                }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "create_account",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            CreateAccountScreen(
                onBackClick = { currentScreen = "login" },
                onContinueClick = { f, l, e, _ ->
                    firstName = f
                    lastName = l
                    email = e
                    currentScreen = "onboarding"
                },
                onForgotPasswordClick = { currentScreen = "forgot_password" },
                onError = { error -> errorMessage = error }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "forgot_password",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            ForgotPasswordScreen(
                onBackClick = { currentScreen = "login" },
                onContinueClick = { _ -> currentScreen = "reset_confirmation" },
                onError = { error -> errorMessage = error }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "reset_confirmation",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            PasswordResetConfirmationScreen(
                onBackClick = { currentScreen = "forgot_password" },
                onReturnToLoginClick = { currentScreen = "login" }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "onboarding",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            OnboardingScreen(
                onBackClick = { currentScreen = "login" },
                onFinishClick = { _, _ -> currentScreen = "home" }
            )
        }
        
        // Main app screens
        // Top content area with persistent bottom nav below for main tabs
        run {
            val showRootNav = currentScreen in setOf("home", "orders", "notifications", "profile")
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = currentScreen == "home",
                    enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { -it }),
                    exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    HomeScreen(
                        onSeeAllCategories = { currentScreen = "categories" },
                        onSelectCategory = { categoryName ->
                            email = categoryName
                            currentScreen = "category_detail"
                        },
                        onCartClick = { currentScreen = "cart" },
                        onNotificationsClick = { currentScreen = "notifications" },
                        onOrdersClick = { currentScreen = "orders" },
                        onProfileClick = { currentScreen = "profile" },
                        onChatClick = { 
                            previousScreen = "home"
                            currentScreen = "chat" 
                        },
                        onProductClick = { productName ->
                            // Find product from repository by name
                            val product = ProductRepository.products.value.find { it.name == productName }
                            if (product != null) {
                                selectedProduct = product
                                currentScreen = "product_page"
                            }
                        },
                        onBannerClick = { banner ->
                            // Find product by ID first, then by name
                            val product = if (!banner.productId.isNullOrEmpty()) {
                                ProductRepository.products.value.find { it.id == banner.productId }
                            } else {
                                ProductRepository.products.value.find { it.name == banner.title }
                            }
                            
                            if (product != null) {
                                // Create a copy of the product with banner prices
                                selectedProduct = product.copy(
                                    price = banner.discount ?: product.price,
                                    originalPrice = banner.originalPrice ?: product.price
                                )
                                currentScreen = "product_page"
                            }
                        },
                        onSearchSubmit = { q ->
                            searchQuery = q
                            searchResults = if (q.isNotEmpty()) {
                                allProducts.filter { it.name.contains(q, ignoreCase = true) }
                            } else allProducts
                            currentScreen = "search"
                        },
                        onFilterClick = {
                            searchResults = allProducts
                            searchResultsSource = "home"
                            currentScreen = "search_filters"
                        },
                        showBottomNav = false
                    )
                }
                
                AnimatedVisibility(
                    visible = currentScreen == "orders" || currentScreen == "track_order",
                    enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
                ) {
                    OrdersScreen(
                        orders = orders,
                        isRefreshing = isRefreshingOrders,
                        onRefresh = {
                            scope.launch {
                                isRefreshingOrders = true
                                val result = OrderRepository.getOrders()
                                result.onSuccess { newOrders ->
                                    orders = newOrders
                                }
                                isRefreshingOrders = false
                            }
                        },
                        onOrderClick = { orderId ->
                            selectedOrderId = orderId
                            currentScreen = "track_order"
                        },
                        onExploreCategories = { currentScreen = "categories" },
                        onHomeClick = { currentScreen = "home" },
                        onNotificationsClick = { currentScreen = "notifications" },
                        onProfileClick = { currentScreen = "profile" },
                        showBottomNav = false
                    )
                }

                AnimatedVisibility(
                    visible = currentScreen == "notifications",
                    enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
                ) {
                    NotificationsScreen(
                        notifications = notifications,
                        onExploreCategories = { currentScreen = "categories" },
                        onHomeClick = { currentScreen = "home" },
                        onOrdersClick = { currentScreen = "orders" },
                        onProfileClick = { currentScreen = "profile" },
                        showBottomNav = false
                    )
                }
                
                AnimatedVisibility(
                    visible = currentScreen == "profile",
                    enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
                ) {
                    ProfileScreen(
                        userName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "User",
                        userEmail = currentUser?.email ?: "",
                        userPhone = currentUser?.phoneNumber ?: "",
                        onPaymentMethodClick = { 
                            previousScreen = "profile"
                            currentScreen = "payment_method" 
                        },
                        onAddressClick = { 
                            previousScreen = "profile"
                            currentScreen = "address" 
                        },
                        onWishlistClick = { 
                            previousScreen = "profile"
                            currentScreen = "wishlist" 
                        },
                        onSupportClick = { 
                            previousScreen = "profile"
                            currentScreen = "chat" 
                        },
                        onWalletClick = { 
                            previousScreen = "profile"
                            currentScreen = "wallet" 
                        },
                        onEditProfileClick = { /* Handle edit profile */ },
                        onSignOutClick = { 
                            AuthRepository.signOut()
                            currentScreen = "login"
                        }
                    )
                }
                
                if (showRootNav) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                    com.example.app.home.BottomNavForRoot(
                            selectedTab = currentScreen,
                            onHomeClick = { currentScreen = "home" },
                            onOrdersClick = { currentScreen = "orders" },
                            onNotificationsClick = { currentScreen = "notifications" },
                        onProfileClick = { currentScreen = "profile" },
                        modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
        
        AnimatedVisibility(
            visible = currentScreen == "categories",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            CategoriesScreen(
                onBackClick = { currentScreen = "home" },
                onSelectCategory = { label ->
                    email = label
                    currentScreen = "category_detail"
                }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "category_detail",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            CategoryDetailScreen(
                category = email.ifEmpty { "Category" },
                onBackClick = { currentScreen = "categories" },
                onProductClick = { productName ->
                    // Find product from repository by name
                    val product = ProductRepository.products.value.find { it.name == productName }
                    if (product != null) {
                        selectedProduct = product
                        currentScreen = "product_page"
                    }
                }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "cart",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            CartScreen(
                cartItems = cartItems,
                onExploreCategories = { currentScreen = "categories" },
                onCheckout = { currentScreen = "checkout" },
                onQuantityChange = { itemId, newQuantity ->
                    cartItems = cartItems.map { item ->
                        if (item.id == itemId) {
                            item.copy(quantity = newQuantity)
                        } else {
                            item
                        }
                    }
                },
                onRemoveItem = { itemId ->
                    cartItems = cartItems.filter { it.id != itemId }
                },
                onRemoveAll = { cartItems = emptyList() },
                onBackClick = { currentScreen = "home" },
                showBottomNav = false
            )
        }
        
        // Track Order Modal
        if (currentScreen == "track_order") {
            val order = orders.find { it.id == selectedOrderId }
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { currentScreen = "orders" },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = Color(0xFFF5F5F5)
            ) {
                TrackOrderScreen(
                    order = order,
                    onBackClick = { currentScreen = "orders" }
                )
            }
        }
        
        AnimatedVisibility(
            visible = currentScreen == "search",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            SearchScreen(
                initialQuery = searchQuery,
                onBackClick = { currentScreen = "home" },
                onSearch = { query ->
                    searchQuery = query
                    searchResults = if (query.isNotEmpty()) {
                        allProducts.filter { it.name.contains(query, ignoreCase = true) }
                    } else allProducts
                    searchResultsSource = "search"
                    currentScreen = "search_results"
                },
                onCategoryClick = { categoryName ->
                    email = categoryName
                    currentScreen = "category_detail"
                }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "search_results" || currentScreen == "search_filters",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            SearchResultsScreen(
                searchQuery = searchQuery,
                products = searchResults,
                onBackClick = {
                    // Navigate back based on where we came from
                    currentScreen = if (searchResultsSource == "home") "home" else "search"
                },
                onClearSearch = {
                    searchQuery = ""
                    searchResults = allProducts
                    currentScreen = "home"
                },
                onSearch = { query ->
                    searchQuery = query
                    searchResults = if (query.isNotEmpty()) {
                        allProducts.filter { it.name.contains(query, ignoreCase = true) }
                    } else allProducts
                },
                onExploreCategories = { currentScreen = "home" },
                onFilterClosed = { currentScreen = "home" },
                openFiltersOnLaunch = currentScreen == "search_filters"
            )
        }
        
        // Product Page
        AnimatedVisibility(
            visible = currentScreen == "product_page",
            enter = androidx.compose.animation.fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = androidx.compose.animation.fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            selectedProduct?.let { product ->
                ProductPageScreen(
                    product = product,
                    onBackClick = { currentScreen = "home" },
                    onAddToCart = { id, name, size, color, price, quantity ->
                        val newItem = com.example.app.cart.CartItem(
                            id = id,
                            name = name,
                            price = price,
                            quantity = quantity,
                            size = size,
                            color = color,
                            category = product.category,
                            imageUrl = product.images.firstOrNull()
                        )
                        cartItems = cartItems + newItem
                        currentScreen = "cart"
                    }
                )
            }
        }
        
        // Checkout Screen
        AnimatedVisibility(
            visible = currentScreen == "checkout",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            val subtotal = cartItems.sumOf { it.price * it.quantity }
            val shippingCost = 8.0
            val tax = 0.0
            
            // Load default address and payment method
            val checkoutScope = rememberCoroutineScope()
            val context = LocalContext.current
            var shippingAddress by remember { mutableStateOf<String?>(null) }
            var paymentMethod by remember { mutableStateOf<String?>(null) }
            
            // Reload data when screen becomes visible
            LaunchedEffect(currentScreen) {
                if (currentScreen == "checkout") {
                    checkoutScope.launch {
                        // Load default address
                        val addressesResult = com.example.app.profile.data.UserProfileRepository.getAddresses()
                        addressesResult.onSuccess { addresses ->
                            val defaultAddress = addresses.find { it.isDefault } ?: addresses.firstOrNull()
                            defaultAddress?.let { address ->
                                shippingAddress = "${address.addressLine1}, ${address.city}, ${address.state} ${address.zipCode}"
                            } ?: run {
                                shippingAddress = null
                            }
                        }
                        
                        // Load default payment method
                        val defaultPayment = com.example.app.profile.data.PaymentMethodStorage.getDefaultPaymentMethod(context)
                        defaultPayment?.let { method ->
                            val maskedNumber = if (method.cardNumber.length >= 4) {
                                "**** ${method.cardNumber.takeLast(4)}"
                            } else {
                                "****"
                            }
                            paymentMethod = maskedNumber
                        } ?: run {
                            paymentMethod = null
                        }
                    }
                }
            }
            
            // Poll for payment verification when on checkout screen with token payment
            LaunchedEffect(currentScreen, selectedPaymentType) {
                if (currentScreen == "checkout" && selectedPaymentType == "token") {
                    while (true) {
                        val (isVerified, _) = com.example.app.blockchain.PaymentVerification.verifyPendingPayment(context)
                        if (isVerified) {
                            // Refresh UI to show verified status
                            // We need to trigger a recomposition, so we toggle a state or just rely on the loop
                            // But verifyPendingPayment updates SharedPreferences which is read below
                            // To force UI update, we can update a local state if we had one exposed, 
                            // but since we don't, we rely on the fact that verifyPendingPayment updates the underlying data
                            // and the next check below will pick it up? No, that's not reactive.
                            // We need to force a refresh.
                            // Let's update the paymentVerified state we defined earlier
                            paymentVerified = true
                            break
                        }
                        kotlinx.coroutines.delay(5000) // Check every 5 seconds
                    }
                }
            }
            
            // Check payment status for token payments
            val isTokenPaymentVerified = if (selectedPaymentType == "token") {
                val total = subtotal + shippingCost + tax
                val totalAmount = java.math.BigDecimal(total.toString())
                // Check both the reactive state and the persistent storage
                paymentVerified || com.example.app.blockchain.PaymentVerification.isPaymentVerified(context, totalAmount)
            } else {
                true // Non-token payments don't need verification
            }
            
            CheckoutScreen(
                subtotal = subtotal,
                shippingCost = shippingCost,
                tax = tax,
                shippingAddress = shippingAddress,
                paymentMethod = paymentMethod,
                selectedPaymentType = selectedPaymentType,
                isPaymentVerified = isTokenPaymentVerified,
                onBackClick = { currentScreen = "cart" },
                onShippingAddressClick = { 
                    previousScreen = "checkout"
                    currentScreen = "address" 
                },
                onPaymentMethodClick = { 
                    previousScreen = "checkout"
                    currentScreen = "payment_method" 
                },
                onTokenPaymentClick = { 
                    selectedPaymentType = "token"
                    // Navigate to token payment/transfer screen
                    previousScreen = "checkout"
                    currentScreen = "token_transfer" 
                },
                onVerifyClick = {
                    checkoutScope.launch {
                        val (isVerified, debugMsg) = com.example.app.blockchain.PaymentVerification.verifyPendingPayment(context)
                        if (isVerified) {
                            paymentVerified = true
                            android.widget.Toast.makeText(context, "Payment Verified!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            android.widget.Toast.makeText(context, "Not Verified: $debugMsg", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onBankClick = { 
                    selectedPaymentType = "bank"
                    currentScreen = "qrcode" 
                },
                onPlaceOrder = onPlaceOrder@{
                    // Verify payment if using token payment
                    if (selectedPaymentType == "token") {
                        val total = subtotal + shippingCost + tax
                        val totalAmount = java.math.BigDecimal(total.toString())
                        
                        // Check both the reactive state and the persistent storage
                        if (!paymentVerified && !com.example.app.blockchain.PaymentVerification.isPaymentVerified(context, totalAmount)) {
                            // Payment not verified - show error
                            errorMessage = "Please complete the token payment first. The transaction must be approved in MetaMask before placing the order."
                            return@onPlaceOrder
                        }
                    }
                    
                    checkoutScope.launch {
                        // Create order from cart items
                        val orderItems = cartItems.map { item ->
                            OrderItem(
                                productId = item.id,
                                productName = item.name,
                                quantity = item.quantity,
                                price = item.price,
                                size = item.size,
                                color = item.color,
                                category = item.category,
                                imageUrl = item.imageUrl
                            )
                        }
                        val orderSubtotal = cartItems.sumOf { it.price * it.quantity }
                        val orderShippingCost = 8.0
                        val orderTax = 0.0
                        val orderTotal = orderSubtotal + orderShippingCost + orderTax
                        
                        val orderNumber = OrderRepository.generateOrderNumber()
                        val customerName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "User"
                        val customerEmail = currentUser?.email ?: ""
                        
                        // Get shipping address from checkout screen
                        val addressesResult = com.example.app.profile.data.UserProfileRepository.getAddresses()
                        val defaultAddress = addressesResult.getOrNull()?.find { it.isDefault } ?: addressesResult.getOrNull()?.firstOrNull()
                        val shippingAddressText = defaultAddress?.let { address ->
                            "${address.addressLine1}, ${address.city}, ${address.state} ${address.zipCode}"
                        } ?: shippingAddress ?: "No address provided"
                        val shippingPhoneText = defaultAddress?.phoneNumber ?: ""
                        
                        val orderResult = OrderRepository.createOrder(
                            orderNumber = orderNumber,
                            items = orderItems,
                            total = orderTotal,
                            customerName = customerName,
                            customerEmail = customerEmail,
                            shippingAddress = shippingAddressText,
                            shippingPhone = shippingPhoneText,
                            status = "Processing"
                        )
                        
                        orderResult.onSuccess { _ ->
                            // Mark transaction as consumed and clear payment verification after successful order
                            if (selectedPaymentType == "token") {
                                checkoutScope.launch {
                                    // Get the transaction hash and amount before clearing
                                    val txHash = com.example.app.blockchain.PaymentVerification.getPendingPaymentHash(context)
                                    if (txHash != null) {
                                        val total = orderSubtotal + orderShippingCost + orderTax
                                        val totalAmount = java.math.BigDecimal(total.toString())
                                        // Mark as consumed to prevent reuse (saves to both local and server)
                                        com.example.app.blockchain.PaymentVerification.markTransactionAsConsumed(context, txHash, totalAmount)
                                    }
                                    com.example.app.blockchain.PaymentVerification.clearPendingPayment(context)
                                    com.example.app.blockchain.PaymentVerification.clearPendingPayment(context)
                                    // paymentVerified = false // Moved to after navigation to prevent UI glitch
                                }
                            }
                            
                            // Decrease stock for all products in the order
                            val stockResult = StockRepository.decreaseStockForOrder(orderItems)
                            stockResult.onFailure {
                                // Log.e("MainActivity", "Error decreasing stock: ${it.message}")
                                // Continue even if stock update fails
                            }
                            
                            // Reload products to reflect updated stock
                            ProductRepository.loadProducts()
                            
                            // Create notification
                            val userName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User"
                            val notificationMessage = "$userName, you placed an order. Check your order history for full details."
                            NotificationRepository.createNotification(
                                message = notificationMessage,
                                type = "order"
                            )
                            
                            // Clear cart
                            cartItems = emptyList()
                            
                            // Wait a bit for database write to complete, then reload orders
                            kotlinx.coroutines.delay(1000) // Increased to 1s to ensure DB consistency

                            val ordersResult = OrderRepository.getOrders()
                            ordersResult.onSuccess {

                                orders = it
                            }.onFailure {

                                // If reload fails, still try to reload when user navigates to orders screen
                            }
                            
                            // Navigate to order placed screen
                            currentScreen = "order_placed"
                            
                            // Reset payment verification AFTER navigating away to avoid UI glitch
                            if (selectedPaymentType == "token") {
                                paymentVerified = false
                            }
                        }.onFailure { exception ->
                            // Handle error - show error message and DO NOT navigate
                            // Log.e("MainActivity", "Error creating order: ${exception.message}")
                            errorMessage = "Failed to place order: ${exception.message}"
                            // Do NOT navigate to order_placed on failure
                        }
                    }
                }
            )
        }
        
        // QR Code Screen
        AnimatedVisibility(
            visible = currentScreen == "qrcode",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            val subtotal = cartItems.sumOf { it.price * it.quantity }
            val shippingCost = 8.0
            val tax = 0.0
            
            QRCodeScreen(
                subtotal = subtotal,
                shippingCost = shippingCost,
                tax = tax,
                onBackClick = { currentScreen = "checkout" },
                onConfirm = { currentScreen = "order_placed" }
            )
        }
        
        // Order Placed Screen
        AnimatedVisibility(
            visible = currentScreen == "order_placed",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            OrderPlacedScreen(
                onSeeOrderDetails = { currentScreen = "orders" }
            )
        }
        
        // Payment Method Screen
        AnimatedVisibility(
            visible = currentScreen == "payment_method",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            PaymentMethodScreen(
                onBackClick = { 
                    // Preserve original previousScreen, don't change it
                    currentScreen = if (previousScreen == "profile") "profile" else if (previousScreen == "checkout") "checkout" else "profile"
                },
                onCardClick = { 
                    // Don't change previousScreen when navigating to add screen
                    currentScreen = "add_card" 
                },
                onAddCard = {
                    // Don't change previousScreen when navigating to add screen
                    currentScreen = "add_card"
                }
            )
        }
        
        // Add Card Screen
        AnimatedVisibility(
            visible = currentScreen == "add_card",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            AddCardScreen(
                onBackClick = { 
                    // Go back to payment_method, preserving original previousScreen
                    currentScreen = "payment_method" 
                },
                onSave = { 
                    // Go back to payment_method, preserving original previousScreen
                    currentScreen = "payment_method"
                    // Refresh payment methods list will happen automatically when screen is shown
                }
            )
        }
        
        // Address Screen
        AnimatedVisibility(
            visible = currentScreen == "address",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            AddressScreen(
                onBackClick = { 
                    // Preserve original previousScreen, don't change it
                    currentScreen = if (previousScreen == "profile") "profile" else if (previousScreen == "checkout") "checkout" else "profile"
                },
                onEditAddress = { 
                    // Don't change previousScreen when navigating to add screen
                    currentScreen = "add_address" 
                },
                onAddAddress = { 
                    // Don't change previousScreen when navigating to add screen
                    currentScreen = "add_address" 
                }
            )
        }
        
        // Add Address Screen
        AnimatedVisibility(
            visible = currentScreen == "add_address",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            AddAddressScreen(
                onBackClick = { 
                    // Go back to address, preserving original previousScreen
                    currentScreen = "address" 
                },
                onSave = { 
                    // Go back to address, preserving original previousScreen
                    currentScreen = "address"
                    // Refresh address list will happen automatically when screen is shown
                }
            )
        }
        
        // Wishlist Screen
        AnimatedVisibility(
            visible = currentScreen == "wishlist",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            WishlistScreen(
                onBackClick = { 
                    currentScreen = if (previousScreen == "profile") "profile" else "home"
                },
                onCategoryClick = { 
                    previousScreen = "wishlist"
                    currentScreen = "my_favourites" 
                }
            )
        }
        
        // My Favourites Screen
        AnimatedVisibility(
            visible = currentScreen == "my_favourites",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            MyFavouritesScreen(
                onBackClick = { 
                    previousScreen = "my_favourites"
                    currentScreen = "wishlist" 
                },
                onProductClick = { /* Handle product click */ }
            )
        }
        
        // Wallet Screen
        AnimatedVisibility(
            visible = currentScreen == "wallet",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            WalletScreen(
                onBackClick = { 
                    currentScreen = if (previousScreen == "profile") "profile" else "profile"
                },
                onTransferClick = {
                    previousScreen = "wallet"
                    currentScreen = "token_transfer"
                }
            )
        }
        
        // Chat Screen
        AnimatedVisibility(
            visible = currentScreen == "chat",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            ChatScreen(
                onBackClick = { 
                    currentScreen = if (previousScreen == "profile") "profile" else "home"
                },
                apiKey = ApiConfig.GEMINI_API_KEY
            )
        }
        
        // Token Transfer Screen
        AnimatedVisibility(
            visible = currentScreen == "token_transfer",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            val context = LocalContext.current
            val totalAmount = if (previousScreen == "checkout") {
                val subtotal = cartItems.sumOf { it.price * it.quantity }
                val shippingCost = 8.0
                val tax = 0.0
                java.math.BigDecimal((subtotal + shippingCost + tax).toString()).setScale(2, java.math.RoundingMode.HALF_UP)
            } else null
            
            TokenTransferScreen(
                recipientAddress = if (previousScreen == "checkout") {
                    com.example.app.blockchain.config.TokenConfig.STORE_WALLET_ADDRESS
                } else "",
                amount = totalAmount,
                onBackClick = { 
                    currentScreen = if (previousScreen == "wallet") "wallet" 
                    else if (previousScreen == "checkout") "checkout" 
                    else "home"
                },
                onTransferComplete = { txHash ->
                    // If coming from checkout, mark payment as pending
                    if (previousScreen == "checkout") {
                        // Use context from outer composable scope (line 979)
                        val subtotal = cartItems.sumOf { it.price * it.quantity }
                        val shippingCost = 8.0
                        val tax = 0.0
                        val total = subtotal + shippingCost + tax
                        val transferTotalAmount = java.math.BigDecimal(total.toString())
                        
                        // Mark payment as pending (will be verified when MetaMask confirms)
                        val fromAddress = com.example.app.blockchain.WalletManager.getWalletAddress(context) ?: ""
                        com.example.app.blockchain.PaymentVerification.setPendingPayment(
                            context = context,
                            transactionHash = txHash,
                            amount = transferTotalAmount,
                            recipientAddress = com.example.app.blockchain.config.TokenConfig.STORE_WALLET_ADDRESS,
                            fromAddress = fromAddress
                        )
                        
                        // If we have a real transaction hash, mark as verified
                        if (txHash.startsWith("0x") && txHash.length > 10) {
                            com.example.app.blockchain.PaymentVerification.markPaymentAsVerified(context, txHash)
                            paymentVerified = true
                        }
                        
                        selectedPaymentType = "token"
                        // Navigate back to checkout - user can now place order
                        currentScreen = "checkout"
                    } else {
                        // Just go back
                        currentScreen = if (previousScreen == "wallet") "wallet" else "home"
                    }
                }
            )
        }
        
    }
}
