package com.example.app.search

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SearchProduct(
    val id: String,
    val name: String,
    val price: String,
    val category: String = "",
    val gender: String = "Men",
    val onSale: Boolean = false,
    val freeShipping: Boolean = false,
    val stock: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    searchQuery: String = "",
    products: List<SearchProduct> = emptyList(),
    onBackClick: () -> Unit = {},
    onClearSearch: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onExploreCategories: () -> Unit = {},
    onFilterClosed: () -> Unit = {},
    openFiltersOnLaunch: Boolean = false
) {
    var sortOption by remember { mutableStateOf("Recommended") }
    var selectedGender by remember { mutableStateOf("Men") }
    var onSaleSelected by remember { mutableStateOf(false) }
    var freeShippingSelected by remember { mutableStateOf(false) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    
    var showSortModal by remember { mutableStateOf(false) }
    var showGenderModal by remember { mutableStateOf(false) }
    var showDealsModal by remember { mutableStateOf(false) }
    var showPriceModal by remember { mutableStateOf(false) }
    var queryText by remember { mutableStateOf(searchQuery) }
    
    val activeFiltersCount = listOf(
        if (onSaleSelected) 1 else 0,
        if (selectedGender != "All") 1 else 0
    ).sum()

    androidx.compose.runtime.LaunchedEffect(openFiltersOnLaunch) {
        if (openFiltersOnLaunch) {
            showDealsModal = true
        }
    }

    if (products.isEmpty()) {
        EmptySearchResults(
            searchQuery = searchQuery,
            onBackClick = onBackClick,
            onClearSearch = onClearSearch,
            onExploreCategories = onExploreCategories
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Search bar
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
                    value = queryText,
                    onValueChange = { queryText = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF4F4F4)),
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        IconButton(onClick = { onSearch.invoke(queryText) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF666666)
                            )
                        }
                    },
                    trailingIcon = {
                        Row {
                            if (queryText.isNotEmpty()) {
                                IconButton(onClick = {
                                    queryText = ""
                                    onClearSearch()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF666666)
                                    )
                                }
                            }
                            IconButton(onClick = { showDealsModal = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Filters",
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
                    readOnly = false,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onSearch = { onSearch.invoke(queryText) }
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filter icon chip
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF262626))
                        Text(
                            text = if (activeFiltersCount > 0) "$activeFiltersCount" else "Filter",
                            color = Color(0xFF262626),
                            fontSize = 12.sp
                        )
                    }
                }
                FilterChip(
                    label = "On Sale",
                    isSelected = onSaleSelected,
                    onClick = { showDealsModal = true }
                )
                FilterChip(
                    label = "Price",
                    onClick = { showPriceModal = true }
                )
                FilterChip(
                    label = "Sort by",
                    onClick = { showSortModal = true }
                )
                FilterChip(
                    label = selectedGender,
                    onClick = { showGenderModal = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Results count
            Text(
                text = "${products.size} Results Found",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Product grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductCard(product = product)
                }
            }
        }

        // Sort Modal
        if (showSortModal) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = {
                    showSortModal = false
                },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                SortFilterModal(
                    selectedOption = sortOption,
                    onSelect = {
                        sortOption = it
                        showSortModal = false
                    },
                    onClear = {
                        sortOption = "Recommended"
                        showSortModal = false
                        onFilterClosed()
                    }
                )
            }
        }

        // Gender Modal
        if (showGenderModal) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = {
                    showGenderModal = false
                },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                GenderFilterModal(
                    selectedGender = selectedGender,
                    onSelect = {
                        selectedGender = it
                        showGenderModal = false
                    },
                    onClear = {
                        selectedGender = "All"
                        showGenderModal = false
                        onFilterClosed()
                    }
                )
            }
        }

        // Deals Modal
        if (showDealsModal) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = {
                    showDealsModal = false
                },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                DealsFilterModal(
                    onSaleSelected = onSaleSelected,
                    freeShippingSelected = freeShippingSelected,
                    onOnSaleToggle = { onSaleSelected = it },
                    onFreeShippingToggle = { freeShippingSelected = it },
                    onClear = {
                        onSaleSelected = false
                        freeShippingSelected = false
                        showDealsModal = false
                        onFilterClosed()
                    },
                    onDismiss = {
                        showDealsModal = false
                        onFilterClosed()
                    }
                )
            }
        }

        // Price Modal
        if (showPriceModal) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = {
                    showPriceModal = false
                },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                PriceFilterModal(
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    onMinPriceChange = { minPrice = it },
                    onMaxPriceChange = { maxPrice = it },
                    onClear = {
                        minPrice = ""
                        maxPrice = ""
                        showPriceModal = false
                        onFilterClosed()
                    },
                    onDismiss = {
                        showPriceModal = false
                        onFilterClosed()
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2D2D2D) else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF262626),
                fontSize = 12.sp
            )
            if (!isSelected) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF262626))
            }
        }
    }
}

@Composable
private fun ProductCard(product: SearchProduct) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Product image
                com.example.app.ui.ProductImage(
                    productId = product.id,
                    category = product.category,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Favorite icon
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

            Column(
                modifier = Modifier.padding(12.dp)
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

@Composable
private fun EmptySearchResults(
    searchQuery: String,
    onBackClick: () -> Unit,
    onClearSearch: () -> Unit,
    onExploreCategories: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Search bar
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
                onValueChange = { },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF4F4F4)),
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF666666)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color(0xFF666666)
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF4F4F4),
                    unfocusedContainerColor = Color(0xFFF4F4F4),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                readOnly = true,
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Empty state
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Color(0xFFFFC107)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sorry, we couldn't find any matching result for your Search.",
                fontSize = 16.sp,
                color = Color(0xFF262626),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onExploreCategories),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                Text(
                    text = "Explore Categories",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SortFilterModal(
    selectedOption: String,
    onSelect: (String) -> Unit,
    onClear: () -> Unit
) {
    val options = listOf("Recommended", "Newest", "Lowest - Highest Price", "Highest - Lowest Price")

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
                text = "Clear",
                color = Color(0xFF666666),
                modifier = Modifier.clickable(onClick = onClear)
            )
            Text(
                text = "Sort by",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF262626))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        options.forEach { option ->
            SortOptionRow(
                option = option,
                isSelected = option == selectedOption,
                onClick = { onSelect(option) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SortOptionRow(
    option: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option,
            fontSize = 16.sp,
            color = if (isSelected) Color(0xFF262626) else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
        if (isSelected) {
            Text(text = "✓", color = Color(0xFF262626), fontSize = 18.sp)
        }
    }
}

@Composable
private fun GenderFilterModal(
    selectedGender: String,
    onSelect: (String) -> Unit,
    onClear: () -> Unit
) {
    val genders = listOf("Men", "Women", "Kids")

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
                text = "Clear",
                color = Color(0xFF666666),
                modifier = Modifier.clickable(onClick = onClear)
            )
            Text(
                text = "Gender",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF262626))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        genders.forEach { gender ->
            GenderOptionRow(
                gender = gender,
                isSelected = gender == selectedGender,
                onClick = { onSelect(gender) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GenderOptionRow(
    gender: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = gender,
            fontSize = 16.sp,
            color = if (isSelected) Color(0xFF262626) else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
        if (isSelected) {
            Text(text = "✓", color = Color(0xFF262626), fontSize = 18.sp)
        }
    }
}

@Composable
private fun DealsFilterModal(
    onSaleSelected: Boolean,
    freeShippingSelected: Boolean,
    onOnSaleToggle: (Boolean) -> Unit,
    onFreeShippingToggle: (Boolean) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
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
                text = "Clear",
                color = Color(0xFF666666),
                modifier = Modifier.clickable(onClick = onClear)
            )
            Text(
                text = "Deals",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF262626))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DealOptionRow(
            label = "On sale",
            isSelected = onSaleSelected,
            onClick = { onOnSaleToggle(!onSaleSelected) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DealOptionRow(
            label = "Free Shipping Eligible",
            isSelected = freeShippingSelected,
            onClick = { onFreeShippingToggle(!freeShippingSelected) }
        )
    }
}

@Composable
private fun DealOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = if (isSelected) Color(0xFF262626) else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
        if (isSelected) {
            Text(text = "✓", color = Color(0xFF262626), fontSize = 18.sp)
        }
    }
}

@Composable
private fun PriceFilterModal(
    minPrice: String,
    maxPrice: String,
    onMinPriceChange: (String) -> Unit,
    onMaxPriceChange: (String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
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
                text = "Clear",
                color = Color(0xFF666666),
                modifier = Modifier.clickable(onClick = onClear)
            )
            Text(
                text = "Price",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF262626))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = onMinPriceChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Min") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            OutlinedTextField(
                value = maxPrice,
                onValueChange = onMaxPriceChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Max") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchResultsScreen() {
    MaterialTheme {
        SearchResultsScreen(
            searchQuery = "Jacket",
            products = listOf(
                SearchProduct(id = "1", name = "Club Fleece Mens Jacket", price = "$56.97", stock = 10),
                SearchProduct(id = "2", name = "Skate Jacket", price = "$150.97", stock = 5),
                SearchProduct(id = "3", name = "Therma Fit Puffer Jacket", price = "$280.97", stock = 0),
                SearchProduct(id = "4", name = "Men's Workwear Jacket", price = "$128.97", stock = 8)
            )
        )
    }
}

