package com.example.app.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.Arrangement
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
            .clip(RoundedCornerShape(32.0.dp))
            .background(Color(1.0f, 1.0f, 1.0f, 1.0f))
            .fillMaxSize()
    ) {
        val (signInRef, frameRef, signUpMethodsRef) = createRefs()

        Text(
            "Sign in",
            Modifier.wrapContentHeight(Alignment.Top).constrainAs(signInRef) {
                start.linkTo(parent.start, 27.0.dp)
                top.linkTo(parent.top, 123.0.dp)
                width = Dimension.value(120.0.dp)
                height = Dimension.value(45.0.dp)
            },
            style = LocalTextStyle.current.copy(
                color = Color(0.15f, 0.15f, 0.15f, 1.0f),
                textAlign = TextAlign.Left,
                fontSize = 32.0.sp
            )
        )

        Column(
            Modifier.constrainAs(frameRef) {
                centerHorizontallyTo(parent)
                top.linkTo(parent.top, 190.0.dp)
                width = Dimension.value(342.0.dp)
                height = Dimension.value(230.0.dp)
            },
            verticalArrangement = Arrangement.spacedBy(16.0.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Email field
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .clip(RoundedCornerShape(4.0.dp))
                    .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    .size(342.0.dp, 56.0.dp),
                placeholder = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0.96f, 0.96f, 0.96f, 1.0f),
                    unfocusedContainerColor = Color(0.96f, 0.96f, 0.96f, 1.0f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Password field with visibility toggle
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .clip(RoundedCornerShape(4.0.dp))
                    .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    .size(342.0.dp, 56.0.dp),
                placeholder = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0.96f, 0.96f, 0.96f, 1.0f),
                    unfocusedContainerColor = Color(0.96f, 0.96f, 0.96f, 1.0f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Sign in button
            ContinueButton(
                modifier = Modifier.size(342.0.dp, 47.0.dp),
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
            androidx.compose.material3.Text(
                text = "Forgot Password?",
                modifier = Modifier
                    .wrapContentHeight(Alignment.Top)
                    .size(190.0.dp, 15.0.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Transparent)
                    .clickable(onClick = onForgotPasswordClick),
                style = LocalTextStyle.current.copy(
                    color = Color(0.0f, 0.0f, 0.0f, 1.0f),
                    textAlign = TextAlign.Left,
                    fontSize = 12.0.sp
                )
            )

            // Create account link
            androidx.compose.material3.Text(
                text = "Dont have an Account ? Create One",
                modifier = Modifier
                    .wrapContentHeight(Alignment.Top)
                    .size(190.0.dp, 15.0.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Transparent)
                    .clickable(onClick = onCreateAccountClick),
                style = LocalTextStyle.current.copy(
                    color = Color(0.0f, 0.0f, 0.0f, 1.0f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.0.sp
                )
            )
        }

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
                top.linkTo(parent.top, 413.0.dp)
                width = Dimension.value(342.0.dp)
                height = Dimension.value(171.0.dp)
            },
            verticalArrangement = Arrangement.spacedBy(12.0.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(100.0.dp))
                    .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    .size(344.0.dp, 49.0.dp)
            ) {
                Text(
                    "Continue With Apple",
                    Modifier
                        .align(Alignment.Center)
                        .wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.15f, 0.15f, 0.15f, 1.0f),
                        textAlign = TextAlign.Center,
                        fontSize = 16.0.sp
                    )
                )
            }

            Box(
                Modifier
                    .clip(RoundedCornerShape(100.0.dp))
                    .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    .size(344.0.dp, 49.0.dp)
            ) {
                Text(
                    "Continue With Google",
                    Modifier
                        .align(Alignment.Center)
                        .wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.15f, 0.15f, 0.15f, 1.0f),
                        textAlign = TextAlign.Center,
                        fontSize = 16.0.sp
                    )
                )
            }

            Box(
                Modifier
                    .clip(RoundedCornerShape(100.0.dp))
                    .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    .size(344.0.dp, 49.0.dp)
            ) {
                Text(
                    "Continue With Facebook",
                    Modifier
                        .align(Alignment.Center)
                        .wrapContentHeight(Alignment.Top),
                    style = LocalTextStyle.current.copy(
                        color = Color(0.15f, 0.15f, 0.15f, 1.0f),
                        textAlign = TextAlign.Center,
                        fontSize = 16.0.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    Box(modifier = Modifier.size(390.dp, 844.dp)) {
        Login()
    }
}
