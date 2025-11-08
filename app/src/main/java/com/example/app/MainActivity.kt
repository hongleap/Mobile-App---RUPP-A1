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
import com.example.app.login.LoginPasswordScreen
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
import com.example.app.redemption.RedemptionScreen
import com.example.app.chat.ChatScreen
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
            MaterialTheme(
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
    val isLoading = ProductRepository.isLoading.collectAsState()
    
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Login screens
        AnimatedVisibility(
            visible = currentScreen == "login",
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Login(
                onContinue = { enteredEmail ->
            email = enteredEmail
            currentScreen = "password"
                },
                onCreateAccountClick = { currentScreen = "create_account" }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "password",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            LoginPasswordScreen(
                email = email,
                onBackClick = { currentScreen = "login" },
                onContinueClick = { /* Navigation handled by LaunchedEffect(isUserSignedIn) */ },
                onForgotPasswordClick = { currentScreen = "forgot_password" },
                onError = { error -> errorMessage = error }
            )
        }
        
        // Show error message
        errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Show error (you can use a Snackbar or AlertDialog here)
                kotlinx.coroutines.delay(3000)
                errorMessage = null
            }
        }
        
        AnimatedVisibility(
            visible = currentScreen == "create_account",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            ForgotPasswordScreen(
                onBackClick = { currentScreen = "login" },
                onContinueClick = { _ -> currentScreen = "reset_confirmation" },
                onError = { error -> errorMessage = error }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "reset_confirmation",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            PasswordResetConfirmationScreen(
                onBackClick = { currentScreen = "forgot_password" },
                onReturnToLoginClick = { currentScreen = "login" }
            )
        }
        
        AnimatedVisibility(
            visible = currentScreen == "onboarding",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
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
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OrdersScreen(
                        orders = orders,
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
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
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
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
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
                        onRedemptionClick = { 
                            previousScreen = "profile"
                            currentScreen = "redemption" 
                        },
                        onSupportClick = { 
                            previousScreen = "profile"
                            currentScreen = "chat" 
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
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
                            color = color
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
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            var shippingAddress by remember { mutableStateOf<String?>(null) }
            var paymentMethod by remember { mutableStateOf<String?>(null) }
            
            // Reload data when screen becomes visible
            LaunchedEffect(currentScreen) {
                if (currentScreen == "checkout") {
                    scope.launch {
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
            
            CheckoutScreen(
                subtotal = subtotal,
                shippingCost = shippingCost,
                tax = tax,
                shippingAddress = shippingAddress,
                paymentMethod = paymentMethod,
                onBackClick = { currentScreen = "cart" },
                onShippingAddressClick = { 
                    previousScreen = "checkout"
                    currentScreen = "address" 
                },
                onPaymentMethodClick = { 
                    previousScreen = "checkout"
                    currentScreen = "payment_method" 
                },
                onBankClick = { currentScreen = "qrcode" },
                onPlaceOrder = {
                    scope.launch {
                        // Create order from cart items
                        val orderItems = cartItems.map { item ->
                            OrderItem(
                                productId = item.id,
                                productName = item.name,
                                quantity = item.quantity,
                                price = item.price,
                                size = item.size,
                                color = item.color
                            )
                        }
                        val subtotal = cartItems.sumOf { it.price * it.quantity }
                        val shippingCost = 8.0
                        val tax = 0.0
                        val total = subtotal + shippingCost + tax
                        
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
                            total = total,
                            customerName = customerName,
                            customerEmail = customerEmail,
                            shippingAddress = shippingAddressText,
                            shippingPhone = shippingPhoneText,
                            status = "Processing"
                        )
                        
                        orderResult.onSuccess { orderId ->
                            // Decrease stock for all products in the order
                            val stockResult = StockRepository.decreaseStockForOrder(orderItems)
                            stockResult.onFailure {
                                println("Error decreasing stock: ${it.message}")
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
                            kotlinx.coroutines.delay(500)
                            val ordersResult = OrderRepository.getOrders()
                            ordersResult.onSuccess {
                                orders = it
                            }.onFailure {
                                // If reload fails, still try to reload when user navigates to orders screen
                            }
                            
                            // Navigate to order placed screen
                            currentScreen = "order_placed"
                        }.onFailure { exception ->
                            // Handle error - could show error message
                            println("Error creating order: ${exception.message}")
                            // For now, still navigate to order placed screen
                            currentScreen = "order_placed"
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
        
        // Redemption Screen
        AnimatedVisibility(
            visible = currentScreen == "redemption",
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            RedemptionScreen(
                balance = 10000,
                onBackClick = { 
                    currentScreen = if (previousScreen == "profile") "profile" else "home"
                },
                onProductClick = { /* Handle product click */ },
                onRedeemClick = { productId ->
                    /* Handle redemption */
                    // Deduct CToken and add product to cart/orders
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
        
    }
}
