package com.example.app.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.app.auth.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Login(
    onContinue: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val authError by AuthRepository.errorMessage.collectAsState()

    ConstraintLayout(
        modifier = Modifier
            .background(AppColors.Background)
            .fillMaxSize()
    ) {
        val (signInRef, frameRef, signUpMethodsRef) = createRefs()

        Text(
            "Sign in",
            Modifier.wrapContentHeight(Alignment.Top).constrainAs(signInRef) {
                start.linkTo(parent.start, AppDimensions.SpacingXXXL)
                top.linkTo(parent.top, 100.0.dp)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            },
            style = MaterialTheme.typography.headlineLarge.copy(
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Left
            )
        )

        Column(
            Modifier.constrainAs(frameRef) {
                centerHorizontallyTo(parent)
                top.linkTo(signInRef.bottom, AppDimensions.SpacingXXXL)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }.padding(horizontal = AppDimensions.SpacingXXXL),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingL),
            horizontalAlignment = Alignment.Start
        ) {
            // Email field
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.ButtonHeightMedium),
                placeholder = { Text("Email Address", color = AppColors.TextTertiary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM),
                singleLine = true
            )

            // Password field with visibility toggle
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.ButtonHeightMedium),
                placeholder = { Text("Password", color = AppColors.TextTertiary) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = AppColors.TextSecondary
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedContainerColor = AppColors.SurfaceVariant,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary
                ),
                shape = RoundedCornerShape(AppDimensions.RadiusM),
                singleLine = true
            )

            // Sign in button
            ContinueButton(
                modifier = Modifier.fillMaxWidth().height(AppDimensions.ButtonHeightMedium),
                enabled = email.isNotBlank() && email.contains("@") && password.isNotBlank() && !isLoading,
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            val result = AuthRepository.signInWithEmailAndPassword(email, password)
                            isLoading = false
                            result.onSuccess {
                                onContinue()
                            }.onFailure { exception ->
                                onError(exception.message ?: "Sign in failed")
                            }
                        }
                    } else {
                        onError("Please enter both email and password")
                    }
                }
            )

            // Forgot Password Link
            Text(
                text = "Forgot Password?",
                modifier = Modifier
                    .clickable(onClick = onForgotPasswordClick),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AppColors.Primary,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))

            // Create account link
            Text(
                text = "Don't have an account? Create One",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCreateAccountClick),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            )
        }

        Text(
            text = "Or continue with",
            modifier = Modifier.constrainAs(createRef()) {
                centerHorizontallyTo(parent)
                top.linkTo(frameRef.bottom, AppDimensions.SpacingXL)
            },
            style = MaterialTheme.typography.bodySmall.copy(
                color = AppColors.TextTertiary
            )
        )

        // Show error message if any
        authError?.let { error ->
            LaunchedEffect(error) {
                onError(error)
                AuthRepository.clearError()
            }
        }

        Column(
            Modifier.constrainAs(signUpMethodsRef) {
                centerHorizontallyTo(parent)
                top.linkTo(frameRef.bottom, 80.0.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }.padding(horizontal = AppDimensions.SpacingXXXL),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SocialButton(
                text = "Continue With Apple",
                onClick = { /* TODO */ }
            )

            SocialButton(
                text = "Continue With Google",
                onClick = { /* TODO */ }
            )

            SocialButton(
                text = "Continue With Facebook",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun SocialButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(AppDimensions.ButtonHeightMedium)
            .clip(RoundedCornerShape(AppDimensions.RadiusM))
            .background(AppColors.SurfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    Box(modifier = Modifier.size(390.dp, 844.dp)) {
        Login()
    }
}
