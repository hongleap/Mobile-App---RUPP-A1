package com.example.app.redemption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

data class RedemptionProduct(
    val id: String,
    val name: String,
    val priceInCToken: Int,
    val imageUrl: String = ""
)

@Composable
fun RedemptionScreen(
    balance: Int = 10000,
    products: List<RedemptionProduct> = listOf(
        RedemptionProduct("1", "Club Fleece Mens Jacket", 6969),
        RedemptionProduct("2", "Club Fleece Mens Jacket", 6969),
        RedemptionProduct("3", "Club Fleece Mens Jacket", 6969),
        RedemptionProduct("4", "Club Fleece Mens Jacket", 6969)
    ),
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onRedeemClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Primary)
    ) {
        // Main content card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = AppDimensions.RadiusXXL, topEnd = AppDimensions.RadiusXXL)),
            colors = CardDefaults.cardColors(containerColor = AppColors.Background),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimensions.SpacingL)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimensions.SpacingXXXL),
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
                        text = "Redemption",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Balance Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimensions.SpacingL),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Balance :",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextPrimary
                        )
                    )
                    Text(
                        text = "$balance CToken",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.SpacingS))

                // Products Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = AppDimensions.SpacingS),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(products.size) { index ->
                        val product = products[index]
                        RedemptionProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onRedeemClick = { onRedeemClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RedemptionProductCard(
    product: RedemptionProduct,
    onClick: () -> Unit,
    onRedeemClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimensions.RadiusL))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) AppColors.AccentRed else AppColors.TextPrimary,
                    modifier = Modifier.size(AppDimensions.IconS)
                )
            }

            // Product info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.SpacingM)
                    .align(Alignment.BottomStart)
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
                    text = "${product.priceInCToken} CToken",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                )
            }
        }
    }
}

