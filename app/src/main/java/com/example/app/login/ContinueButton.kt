package com.example.app.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun ContinueButton(
    modifier: Modifier = Modifier,
    label: String = "Continue",
    minWidth: Dp = 342.dp,
    minHeight: Dp = 47.dp,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.0.dp))
            .background(
                if (enabled) Color(red = 0.25f, green = 0.25f, blue = 0.25f, alpha = 1.0f)
                else Color(red = 0.7f, green = 0.7f, blue = 0.7f, alpha = 1.0f)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = LocalTextStyle.current.copy(
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.0.sp
            )
        )
    }
}
