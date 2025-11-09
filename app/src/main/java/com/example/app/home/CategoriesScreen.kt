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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CategoriesScreen(
    onBackClick: () -> Unit = {},
    onSelectCategory: (String) -> Unit = {}
) {
    val categories = listOf("Hoodies", "Jackets", "T-Shirts", "Pants", "Shoes", "Accessories")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF262626))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Shop by Categories", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF262626))

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Text(text = label, color = Color(0xFF262626))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoriesScreen() {
    MaterialTheme { CategoriesScreen() }
}


