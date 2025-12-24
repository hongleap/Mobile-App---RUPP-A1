package com.example.app.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

@Composable
fun OrderPlacedScreen(
    onSeeOrderDetails: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimensions.SpacingXXXL),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Illustration placeholder (top half)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for illustration
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(AppDimensions.RadiusM))
                        .background(AppColors.Surface.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 80.sp,
                        color = AppColors.TextOnPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Success message card (bottom half)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = AppDimensions.RadiusXXL, topEnd = AppDimensions.RadiusXXL)),
                colors = CardDefaults.cardColors(containerColor = AppColors.Background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.SpacingXXXL),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Order Placed Successfully",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.SpacingM))

                    Text(
                        text = "You will receive an email confirmation",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.TextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

                    // See Order Details button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppDimensions.RadiusM))
                            .clickable(onClick = onSeeOrderDetails),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Primary)
                    ) {
                        Text(
                            text = "See Order details",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppDimensions.SpacingL),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextOnPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

