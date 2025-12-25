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
import androidx.compose.foundation.layout.width
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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
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
            .background(AppColors.Background)
            .padding(horizontal = AppDimensions.SpacingL)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.TextPrimary
                )
            }
            
            Text(
                text = "$category ($itemCount)",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(vertical = AppDimensions.SpacingL),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
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
            .clip(RoundedCornerShape(AppDimensions.RadiusL))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
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
                    imageUrl = product.images.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Favorite icon in top-right corner
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppDimensions.SpacingS)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AppColors.AccentRed else AppColors.TextPrimary,
                        modifier = Modifier.size(AppDimensions.IconS)
                    )
                }
            }

            // Product name, price, and stock
            Column(
                modifier = Modifier.padding(horizontal = AppDimensions.SpacingM, vertical = AppDimensions.SpacingM)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppColors.TextPrimary,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                )
                
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                
                // Stock display
                Text(
                    text = if (product.stock > 0) "In Stock: ${product.stock}" else "No Stock",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (product.stock > 0) AppColors.Primary else AppColors.AccentRed
                    )
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


