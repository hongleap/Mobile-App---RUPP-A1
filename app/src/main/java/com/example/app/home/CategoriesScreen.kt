package com.example.app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

@Composable
fun CategoriesScreen(
    onBackClick: () -> Unit = {},
    onSelectCategory: (String) -> Unit = {}
) {
    val categories = listOf("Hoodies", "Jackets", "T-Shirts", "Pants", "Shoes", "Accessories")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingL)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = AppColors.TextPrimary)
        }

        Spacer(modifier = Modifier.height(AppDimensions.SpacingS))

        Text(
            text = "Shop by Categories",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))

        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)) {
            categories.forEach { label ->
                CategoryRow(label = label, onClick = { onSelectCategory(label) })
            }
        }
    }
}

@Composable
private fun CategoryRow(label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimensions.RadiusM))
            .background(AppColors.Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingL)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoriesScreen() {
    MaterialTheme { CategoriesScreen() }
}


