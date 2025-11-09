package com.example.app.product

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val images: List<String> = emptyList(),
    val description: String = "",
    val sizes: List<String> = listOf("S", "M", "L", "XL", "2XL"),
    val colors: List<ProductColor> = emptyList(),
    val category: String = "",
    val gender: String = "",
    val onSale: Boolean = false,
    val freeShipping: Boolean = false,
    val stock: Int = 0
)

data class ProductColor(
    val name: String,
    val colorValue: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPageScreen(
    product: Product = Product(
        id = "1",
        name = "Men's Harrington Jacket",
        price = "$148",
        images = listOf("image1", "image2", "image3"),
        description = "Built for life and made to last, this full-zip corduroy jacket is part of our Nike Life collection. The spacious fit gives you plenty of room to layer underneath, while the soft corduroy keeps it casual and timeless.",
        sizes = listOf("S", "M", "L", "XL", "2XL"),
        colors = listOf(
            ProductColor("Orange", Color(0xFFFF9800)),
            ProductColor("Black", Color(0xFF000000)),
            ProductColor("Red", Color(0xFFFF0000)),
            ProductColor("Yellow", Color(0xFFFFFF00)),
            ProductColor("Blue", Color(0xFF0000FF))
        ),
        stock = 10
    ),
    onBackClick: () -> Unit = {},
    onAddToCart: (String, String, String, String, Double, Int) -> Unit = { _, _, _, _, _, _ -> }
) {
    var selectedSize by remember { mutableStateOf("S") }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull()?.name ?: "") }
    var quantity by remember { mutableStateOf(1) }
    var isFavorite by remember { mutableStateOf(false) }
    var showSizeModal by remember { mutableStateOf(false) }
    var showColorModal by remember { mutableStateOf(false) }
    
    val priceValue = product.price.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0

    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
        ) {
            // Header with back and favorite buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF0000) else Color(0xFF262626)
                    )
                }
            }

            // Product images carousel
            ProductImageCarousel(productId = product.id, category = product.category)

                   // Product details
                   Card(
                       modifier = Modifier.fillMaxWidth(),
                       colors = CardDefaults.cardColors(containerColor = Color.White),
                       elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                   ) {
                       Column(
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(16.dp)
                       ) {
                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF262626)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = product.price,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF262626)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Stock display
                Text(
                    text = if (product.stock > 0) "In Stock: ${product.stock}" else "No Stock",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (product.stock > 0) Color(0xFF28A745) else Color(0xFFDC3545)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Size selector
                SizeColorSelector(
                    label = "Size",
                    value = selectedSize,
                    onClick = { showSizeModal = true }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Color selector
                SizeColorSelector(
                    label = "Color",
                    value = selectedColor,
                    onClick = { showColorModal = true },
                    showColorSwatch = true,
                    colorValue = product.colors.find { it.name == selectedColor }?.colorValue
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Quantity selector
                QuantitySelector(
                    quantity = quantity,
                    onDecrease = { if (quantity > 1) quantity-- },
                    onIncrease = { quantity++ }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Shipping & Returns
                Text(
                    text = "Shipping & Returns",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF262626)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Free standard shipping and free 60-day returns.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                       }
                   }
            
            // Add bottom padding to account for bottom action bar
            Spacer(modifier = Modifier.height(100.dp))
        }
        
        // Bottom bar with price and Add to Bag button
        BottomActionBar(
            price = product.price,
            onAddToBag = {
                onAddToCart(
                    product.id,
                    product.name,
                    selectedSize,
                    selectedColor,
                    priceValue,
                    quantity
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    
    // Size Modal
    if (showSizeModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSizeModal = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            SizeSelectionModal(
                sizes = product.sizes,
                selectedSize = selectedSize,
                onSelect = { selectedSize = it; showSizeModal = false },
                onClose = { showSizeModal = false }
            )
        }
    }
    
    // Color Modal
    if (showColorModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showColorModal = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            ColorSelectionModal(
                colors = product.colors,
                selectedColor = selectedColor,
                onSelect = { selectedColor = it; showColorModal = false },
                onClose = { showColorModal = false }
            )
        }
    }
}

@Composable
private fun ProductImageCarousel(productId: String?, category: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        com.example.app.ui.ProductImage(
            productId = productId,
            category = category,
            contentDescription = "Product image",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
    }
}

@Composable
private fun SizeColorSelector(
    label: String,
    value: String,
    onClick: () -> Unit,
    showColorSwatch: Boolean = false,
    colorValue: Color? = null
) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color(0xFF262626)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showColorSwatch && colorValue != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colorValue)
                    )
                }
                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = Color(0xFF262626),
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quantity",
                fontSize = 16.sp,
                color = Color(0xFF262626)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2D2D2D))
                ) {
                    Text(
                        text = "−",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF262626)
                )
                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2D2D2D))
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    price: String,
    onAddToBag: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = price,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onAddToBag),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                Text(
                    text = "Add to Bag",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SizeSelectionModal(
    sizes: List<String>,
    selectedSize: String,
    onSelect: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Size",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF262626)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        sizes.forEach { size ->
            SizeOptionRow(
                size = size,
                isSelected = size == selectedSize,
                onClick = { onSelect(size) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SizeOptionRow(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2D2D2D) else Color(0xFFF0F0F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = size,
                fontSize = 16.sp,
                color = if (isSelected) Color.White else Color(0xFF262626),
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
            if (isSelected) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ColorSelectionModal(
    colors: List<ProductColor>,
    selectedColor: String,
    onSelect: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Color",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF262626)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        colors.forEach { color ->
            ColorOptionRow(
                color = color,
                isSelected = color.name == selectedColor,
                onClick = { onSelect(color.name) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ColorOptionRow(
    color: ProductColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2D2D2D) else Color(0xFFF0F0F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = color.name,
                fontSize = 16.sp,
                color = if (isSelected) Color.White else Color(0xFF262626),
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.colorValue)
                )
                if (isSelected) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


