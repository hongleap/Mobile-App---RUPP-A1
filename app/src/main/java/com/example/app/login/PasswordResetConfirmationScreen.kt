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

@Composable
fun PasswordResetConfirmationScreen(
    onBackClick: () -> Unit = {},
    onReturnToLoginClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.Start)
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

        Spacer(modifier = Modifier.height(120.dp))

        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0x1A262626)),
            tint = Color(0xFFFFB74D)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "We Sent you an Email to reset your password.",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF262626)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ContinueButton(
            modifier = Modifier.size(width = 200.dp, height = 47.dp),
            label = "Return to Login",
            minWidth = 200.dp,
            minHeight = 47.dp,
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

