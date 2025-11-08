package com.example.app.home

import androidx.compose.runtime.collectAsState

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.example.app.product.Product

@Composable
fun CategoryDetailScreen(
    category: String,
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    // Get products from Firestore by category
    val allProducts = com.example.app.data.ProductRepository.products.collectAsState().value
    val products = allProducts.filter { it.category.equals(category, ignoreCase = true) }
    val itemCount = products.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF262626)
                )
            }
            
            Text(
                text = "$category ($itemCount)",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF262626)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.name) }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Product image area with favorite icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
            ) {
                // Product image
                com.example.app.ui.ProductImage(
                    productId = product.id,
                    category = product.category,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Favorite icon in top-right corner
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE91E63) else Color(0xFF262626),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Product name, price, and stock
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    color = Color(0xFF262626),
                    maxLines = 2,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = product.price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF262626)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Stock display
                Text(
                    text = if (product.stock > 0) "In Stock: ${product.stock}" else "No Stock",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (product.stock > 0) Color(0xFF28A745) else Color(0xFFDC3545)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryDetailScreen() {
    MaterialTheme { 
        CategoryDetailScreen(category = "Hoodies")
    }
}


