package com.example.app.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

data class FavoriteProduct(
    val id: String,
    val name: String,
    val price: String,
    val imageUrl: String = ""
)

@Composable
fun MyFavouritesScreen(
    products: List<FavoriteProduct> = listOf(
        FavoriteProduct("1", "Nike Fuel Pack", "$32.00"),
        FavoriteProduct("2", "Nike Show X Rush", "$204"),
        FavoriteProduct("3", "Men's T-Shirt", "$45.00"),
        FavoriteProduct("4", "Men's Skate T-Shirt", "$45.00")
    ),
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingXXL),
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
                text = "My Favourites (${products.size})",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingS),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
        ) {
            items(products) { product ->
                FavoriteProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteProductCard(
    product: FavoriteProduct,
    onClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimensions.RadiusL))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = AppDimensions.RadiusL, topEnd = AppDimensions.RadiusL))
                    .background(AppColors.SurfaceVariant)
            )

            // Favorite icon
            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppDimensions.SpacingS)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = AppColors.AccentRed,
                    modifier = Modifier.size(AppDimensions.IconS)
                )
            }

            // Product info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.SpacingM)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingXS))
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                )
            }
        }
    }
}

