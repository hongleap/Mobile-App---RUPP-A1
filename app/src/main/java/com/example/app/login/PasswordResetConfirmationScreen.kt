package com.example.app.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Email
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

@Composable
fun PasswordResetConfirmationScreen(
    onBackClick: () -> Unit = {},
    onReturnToLoginClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = AppDimensions.SpacingXXXL, vertical = AppDimensions.SpacingL),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.Start)
                .size(AppDimensions.ButtonHeightSmall)
                .clip(CircleShape)
                .background(AppColors.SurfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = AppColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(120.dp))

        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AppColors.PrimaryLight),
            tint = AppColors.Primary
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        Text(
            text = "We Sent you an Email to reset your password.",
            style = MaterialTheme.typography.titleLarge.copy(
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        ContinueButton(
            modifier = Modifier.size(width = 200.dp, height = AppDimensions.ButtonHeightMedium),
            label = "Return to Login",
            onClick = onReturnToLoginClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordResetConfirmationScreen() {
    MaterialTheme {
        Box(modifier = Modifier.size(390.dp, 844.dp)) {
            PasswordResetConfirmationScreen()
        }
    }
}

