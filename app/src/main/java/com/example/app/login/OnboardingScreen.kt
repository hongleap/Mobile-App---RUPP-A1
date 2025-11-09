package com.example.app.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onBackClick: () -> Unit = {},
    onFinishClick: (shopFor: String, ageRange: String) -> Unit = { _, _ -> }
) {
    var selectedShopFor by remember { mutableStateOf("Men") }
    var ageRange by remember { mutableStateOf("Age Range") }
    var isAgeDropdownExpanded by remember { mutableStateOf(false) }

    val chipBackgroundSelected = Color(0xFF2D2D2D)
    val chipBackgroundUnselected = Color(0xFFF5F5F5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF2F2F2))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF262626)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tell us About yourself",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Who do you shop for ?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingToggleChip(
                label = "Men",
                isSelected = selectedShopFor == "Men",
                selectedBackground = chipBackgroundSelected,
                unselectedBackground = chipBackgroundUnselected,
                modifier = Modifier.weight(1f),
                onSelect = { selectedShopFor = "Men" }
            )

            OnboardingToggleChip(
                label = "Women",
                isSelected = selectedShopFor == "Women",
                selectedBackground = chipBackgroundSelected,
                unselectedBackground = chipBackgroundUnselected,
                modifier = Modifier.weight(1f),
                onSelect = { selectedShopFor = "Women" }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "How Old are you ?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = isAgeDropdownExpanded,
            onExpandedChange = { isAgeDropdownExpanded = !isAgeDropdownExpanded }
        ) {
            OutlinedTextField(
                value = ageRange,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5)),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAgeDropdownExpanded) },
                readOnly = true,
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            val ageOptions = listOf("18-24", "25-34", "35-44", "45+")
            DropdownMenu(
                expanded = isAgeDropdownExpanded,
                onDismissRequest = { isAgeDropdownExpanded = false }
            ) {
                ageOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            ageRange = option
                            isAgeDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ContinueButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            label = "Finish",
            onClick = { onFinishClick(selectedShopFor, ageRange) }
        )
    }
}

@Composable
private fun OnboardingToggleChip(
    label: String,
    isSelected: Boolean,
    selectedBackground: Color,
    unselectedBackground: Color,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val background = if (isSelected) selectedBackground else unselectedBackground
    val textColor = if (isSelected) Color.White else Color(0xFF262626)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .clickable(onClick = onSelect)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboardingScreen() {
    MaterialTheme {
        Box(modifier = Modifier.size(390.dp, 844.dp)) {
            OnboardingScreen()
        }
    }
}

