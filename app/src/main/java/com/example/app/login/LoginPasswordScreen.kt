

package com.example.app.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.app.auth.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPasswordScreen(
    email: String = "",
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) } // State to control animation
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val authError by AuthRepository.errorMessage.collectAsState()

    // Trigger animation when the composable is first displayed
    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { -it })
    ) {
        ConstraintLayout(
            modifier = Modifier
                .clip(RoundedCornerShape(32.0.dp))
                .background(Color(1.0f, 1.0f, 1.0f, 1.0f))
                .fillMaxSize()
        ) {
            val (backButton, title, passwordField, continueButton, forgotPassword) = createRefs()

            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.constrainAs(backButton) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0.15f, 0.15f, 0.15f, 1.0f)
                )
            }

            // Title
            Text(
                "Sign in",
                Modifier.wrapContentHeight(Alignment.Top).constrainAs(title) {
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

            // Password Field
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .constrainAs(passwordField) {
                        start.linkTo(parent.start, 23.dp)
                        top.linkTo(title.bottom, 32.dp)
                        width = Dimension.value(342.0.dp)
                    }
                    .clip(RoundedCornerShape(4.0.dp))
                    .background(Color(0.96f, 0.96f, 0.96f, 1.0f))
                    .size(342.0.dp, 56.0.dp),
                placeholder = { Text("Password") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0.96f, 0.96f, 0.96f, 1.0f),
                    unfocusedContainerColor = Color(0.96f, 0.96f, 0.96f, 1.0f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Continue button — sign in with Firebase Auth
            ContinueButton(
                modifier = Modifier
                    .constrainAs(continueButton) {
                        start.linkTo(parent.start, 23.dp)
                        top.linkTo(passwordField.bottom, 16.dp)
                    }
                    .size(342.0.dp, 47.0.dp),
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            val result = AuthRepository.signInWithEmailAndPassword(email, password)
                            isLoading = false
                            result.onSuccess {
                                // Auth state will be updated automatically, LaunchedEffect will handle navigation
                                // No need to call onContinueClick() manually
                            }.onFailure { exception ->
                                onError(exception.message ?: "Sign in failed")
                            }
                        }
                    } else {
                        onError("Please enter both email and password")
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

            // Forgot Password Link
            Text(
                text = "Forgot Password?",
                modifier = Modifier
                    .constrainAs(forgotPassword) {
                        start.linkTo(parent.start, 23.dp)
                        top.linkTo(continueButton.bottom, 16.dp)
                    }
                    .wrapContentHeight(Alignment.Top)
                    .size(190.0.dp, 15.0.dp)
                    .clickable(onClick = onForgotPasswordClick),
                style = LocalTextStyle.current.copy(
                    color = Color(0.0f, 0.0f, 0.0f, 1.0f),
                    textAlign = TextAlign.Left,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginPasswordScreen() {
    Box(modifier = Modifier.size(390.dp, 844.dp)) {
        LoginPasswordScreen()
    }
}
