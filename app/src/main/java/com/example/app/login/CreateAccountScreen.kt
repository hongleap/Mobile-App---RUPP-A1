package com.example.app.login

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
fun CreateAccountScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (firstName: String, lastName: String, email: String, password: String) -> Unit = { _, _, _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        val textFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.SurfaceVariant,
            unfocusedContainerColor = AppColors.SurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = AppColors.TextPrimary,
            unfocusedTextColor = AppColors.TextPrimary
        )

        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingL)) {
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.ButtonHeightMedium),
                placeholder = { Text("Firstname", color = AppColors.TextTertiary) },
                colors = textFieldColors,
                shape = RoundedCornerShape(AppDimensions.RadiusM),
                singleLine = true
            )

            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.ButtonHeightMedium),
                placeholder = { Text("Lastname", color = AppColors.TextTertiary) },
                colors = textFieldColors,
                shape = RoundedCornerShape(AppDimensions.RadiusM),
                singleLine = true
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.ButtonHeightMedium),
                placeholder = { Text("Email Address", color = AppColors.TextTertiary) },
                colors = textFieldColors,
                shape = RoundedCornerShape(AppDimensions.RadiusM),
                singleLine = true
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.ButtonHeightMedium),
                placeholder = { Text("Password", color = AppColors.TextTertiary) },
                colors = textFieldColors,
                shape = RoundedCornerShape(AppDimensions.RadiusM),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.SpacingXXXL))

        ContinueButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimensions.ButtonHeightMedium),
            onClick = {
                if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    isLoading = true
                    scope.launch {
                        val result = AuthRepository.createUserWithEmailAndPassword(email, password)
                        isLoading = false
                        result.onSuccess {
                            onContinueClick(firstName, lastName, email, password)
                        }.onFailure { exception ->
                            onError(exception.message ?: "Account creation failed")
                        }
                    }
                } else {
                    onError("Please fill in all fields")
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

        Spacer(modifier = Modifier.height(AppDimensions.SpacingL))

        Text(
            text = "Already have an account? Sign in",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier
                .clickable(onClick = onBackClick)
        )

        Spacer(modifier = Modifier.height(AppDimensions.SpacingM))

        Text(
            text = "Forgot Password ? Reset",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.clickable(onClick = onForgotPasswordClick)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateAccountScreen() {
    MaterialTheme {
        Box(modifier = Modifier.size(390.dp, 844.dp)) {
            CreateAccountScreen()
        }
    }
}

