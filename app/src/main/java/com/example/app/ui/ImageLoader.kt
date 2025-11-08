package com.example.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Get category image name from category string
 */
private fun getCategoryImageName(category: String?): String {
    if (category.isNullOrEmpty()) return "product"
    
    return when (category.lowercase()) {
        "hoodies" -> "hoodie"
        "jackets" -> "jacket"
        "t-shirts", "tshirts" -> "t_shirt"
        "pants" -> "pants"
        "shoes" -> "shoes"
        "accessories" -> "accessories"
        else -> "product"
    }
}

/**
 * Get resource ID for an image by name (product ID)
 * Falls back to category image, then "product" if image not found
 */
private fun getImageResourceId(
    context: android.content.Context,
    imageName: String?,
    category: String? = null
): Int {
    // First, try to find image with name matching product ID (case-insensitive)
    if (!imageName.isNullOrEmpty()) {
        val lowerName = imageName.lowercase()
        var resourceId = context.resources.getIdentifier(lowerName, "drawable", context.packageName)
        
        // If not found, try with original case
        if (resourceId == 0) {
            resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        }
        
        // If found, return it
        if (resourceId != 0) {
            return resourceId
        }
    }
    
    // If product image not found, try category image
    if (!category.isNullOrEmpty()) {
        val categoryImageName = getCategoryImageName(category)
        var resourceId = context.resources.getIdentifier(categoryImageName, "drawable", context.packageName)
        
        if (resourceId != 0) {
            return resourceId
        }
    }
    
    // Fall back to "product"
    var resourceId = context.resources.getIdentifier("product", "drawable", context.packageName)
    
    // Final fallback to system image if product.png doesn't exist
    if (resourceId == 0) {
        resourceId = android.R.drawable.ic_menu_report_image
    }
    
    return resourceId
}

/**
 * Product image loader that loads from local resources
 * Uses product ID to find image (e.g., "HO1" -> ho1.png)
 * Falls back to category image (e.g., "Shoes" -> shoes.png) if product image not found
 * Falls back to Product.png if category image not found
 */
@Composable
fun ProductImage(
    productId: String?,
    category: String? = null,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resourceId = getImageResourceId(context, productId, category)
    
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = contentDescription,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

