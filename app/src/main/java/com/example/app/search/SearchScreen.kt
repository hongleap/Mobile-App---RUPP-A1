package com.example.app.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

data class Category(
    val name: String
)

@Composable
fun SearchScreen(
    initialQuery: String = "",
    onBackClick: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onCategoryClick: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    val focusRequester = androidx.compose.ui.focus.FocusRequester()
    androidx.compose.runtime.LaunchedEffect(Unit) {
        // focus keyboard on enter
        kotlinx.coroutines.delay(100)
        focusRequester.requestFocus()
    }
    
    val categories = listOf(
        Category("Hoodies"),
        Category("Accessories"),
        Category("Shorts"),
        Category("Shoes"),
        Category("Bags")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Search bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF262626)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF4F4F4))
                    .focusRequester(focusRequester),
                placeholder = { Text("Search") },
                leadingIcon = {
                    IconButton(onClick = { if (searchQuery.isNotEmpty()) onSearch(searchQuery) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF666666)
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF666666)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF4F4F4),
                    unfocusedContainerColor = Color(0xFFF4F4F4),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { if (searchQuery.isNotEmpty()) onSearch(searchQuery) }
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shop by Categories title
        Text(
            text = "Shop by Categories",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories list
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEachIndexed { index, category ->
                CategoryRow(
                    category = category,
                    onClick = { onCategoryClick(category.name) },
                    showDivider = index < categories.size - 1
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8E8E8))
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = category.name,
                fontSize = 16.sp,
                color = Color(0xFF262626),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ">",
                fontSize = 18.sp,
                color = Color(0xFF666666)
            )
        }

        // Divider
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFE0E0E0))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    MaterialTheme {
        SearchScreen()
    }
}

