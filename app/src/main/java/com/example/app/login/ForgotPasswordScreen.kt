package com.example.app.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.auth.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (email: String) -> Unit = {},
    onError: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val authError by AuthRepository.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = AppDimensions.SpacingXXXL, vertical = AppDimensions.SpacingL),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
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

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimensions.ButtonHeightMedium),
            placeholder = { Text("Enter Email address", color = AppColors.TextTertiary) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AppColors.SurfaceVariant,
                unfocusedContainerColor = AppColors.SurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = AppColors.TextPrimary,
                unfocusedTextColor = AppColors.TextPrimary
            ),
            shape = RoundedCornerShape(AppDimensions.RadiusM),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        ContinueButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimensions.ButtonHeightMedium),
            onClick = {
                if (email.isNotBlank()) {
                    isLoading = true
                    scope.launch {
                        val result = AuthRepository.sendPasswordResetEmail(email)
                        isLoading = false
                        result.onSuccess {
                            onContinueClick(email)
                        }.onFailure { exception ->
                            onError(exception.message ?: "Failed to send password reset email")
                        }
                    }
                } else {
                    onError("Please enter your email address")
                }
            },
            enabled = !isLoading
        )
        
        // Show error message if any
        authError?.let { error ->
            LaunchedEffect(error) {
                onError(error)
                AuthRepository.clearError()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordScreen() {
    MaterialTheme {
        Box(modifier = Modifier.size(390.dp, 844.dp)) {
            ForgotPasswordScreen()
        }
    }
}

