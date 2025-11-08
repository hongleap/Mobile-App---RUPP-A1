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
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
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
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val textFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                placeholder = { Text("Firstname") },
                colors = textFieldColors,
                singleLine = true
            )

            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                placeholder = { Text("Lastname") },
                colors = textFieldColors,
                singleLine = true
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                placeholder = { Text("Email Address") },
                colors = textFieldColors,
                singleLine = true
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                placeholder = { Text("Password") },
                colors = textFieldColors,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        ContinueButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Forgot Password ? Reset",
            style = LocalTextStyle.current.copy(
                color = Color(0xFF262626),
                textAlign = TextAlign.Start,
                fontSize = 12.sp
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

