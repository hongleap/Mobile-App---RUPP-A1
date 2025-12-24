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
import androidx.compose.foundation.layout.width
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
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

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
                .background(AppColors.Background)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.SpacingL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.TextPrimary
                    )
                }

                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(AppDimensions.RadiusXL))
                        .background(AppColors.SurfaceVariant),
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        IconButton(onClick = { onSearch.invoke(queryText) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = AppColors.TextSecondary
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
                                        tint = AppColors.TextSecondary
                                    )
                                }
                            }
                            IconButton(onClick = { showDealsModal = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Filters",
                                    tint = AppColors.TextSecondary
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AppColors.SurfaceVariant,
                        unfocusedContainerColor = AppColors.SurfaceVariant,
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
                    .padding(horizontal = AppDimensions.SpacingL),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingS)
            ) {
                // Filter icon chip
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AppDimensions.RadiusXL)),
                    colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppDimensions.SpacingM, vertical = AppDimensions.SpacingS),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingXS)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(AppDimensions.IconXS), tint = AppColors.TextPrimary)
                        Text(
                            text = if (activeFiltersCount > 0) "$activeFiltersCount" else "Filter",
                            color = AppColors.TextPrimary,
                            style = MaterialTheme.typography.bodySmall
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
                modifier = Modifier.padding(horizontal = AppDimensions.SpacingL),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AppColors.TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Product grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingS),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
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
                .clip(RoundedCornerShape(AppDimensions.RadiusXL))
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) AppColors.Primary else AppColors.SurfaceVariant
            )
        ) {
        Row(
            modifier = Modifier.padding(horizontal = AppDimensions.SpacingM, vertical = AppDimensions.SpacingS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingXS)
        ) {
            Text(
                text = label,
                color = if (isSelected) AppColors.TextOnPrimary else AppColors.TextPrimary,
                style = MaterialTheme.typography.bodySmall
            )
            if (!isSelected) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(AppDimensions.IconXS), tint = AppColors.TextPrimary)
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
            .clip(RoundedCornerShape(AppDimensions.RadiusL)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
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

            Column(
                modifier = Modifier.padding(AppDimensions.SpacingM)
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
            .background(AppColors.Background)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.SpacingL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.TextPrimary
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(AppDimensions.RadiusXL))
                    .background(AppColors.SurfaceVariant),
                placeholder = { Text("Search", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AppColors.TextSecondary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = AppColors.TextSecondary
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
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
                .padding(horizontal = AppDimensions.SpacingXXXL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = AppColors.Primary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sorry, we couldn't find any matching result for your Search.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = AppColors.TextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.RadiusM))
                    .clickable(onClick = onExploreCategories),
                colors = CardDefaults.cardColors(containerColor = AppColors.Primary)
            ) {
                Text(
                    text = "Explore Categories",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimensions.SpacingL),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AppColors.TextOnPrimary,
                        fontWeight = FontWeight.Medium
                    )
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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
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
            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
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
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isSelected) AppColors.TextPrimary else AppColors.TextSecondary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
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
            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
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
            .padding(vertical = AppDimensions.SpacingM),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = gender,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isSelected) AppColors.TextPrimary else AppColors.TextSecondary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
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
        Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
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
            .padding(vertical = AppDimensions.SpacingM),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isSelected) AppColors.TextPrimary else AppColors.TextSecondary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        )
        if (isSelected) {
            Text(text = "✓", color = AppColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF262626))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingS)
        ) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = onMinPriceChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Min", style = MaterialTheme.typography.bodyMedium) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
            )
            OutlinedTextField(
                value = maxPrice,
                onValueChange = onMaxPriceChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Max", style = MaterialTheme.typography.bodyMedium) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM)
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

