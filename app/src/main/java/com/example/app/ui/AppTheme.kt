package com.example.app.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R

object AppColors {
    // Primary Colors
    val Primary = Color(0xFF2D2D2D)
    val PrimaryLight = Color(0xFF4A4A4A)
    val PrimaryDark = Color(0xFF1A1A1A)
    
    // Background Colors
    val Background = Color(0xFFF5F5F5)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF0F0F0)
    val SurfaceDark = Color(0xFF2D2D2D)
    
    // Text Colors
    val TextPrimary = Color(0xFF262626)
    val TextSecondary = Color(0xFF666666)
    val TextTertiary = Color(0xFF999999)
    val TextOnPrimary = Color(0xFFFFFFFF)
    
    // Accent Colors
    val Accent = Color(0xFFFF9800)
    val AccentGreen = Color(0xFF4CAF50)
    val AccentRed = Color(0xFFFF0000)
    
    // Border Colors
    val Border = Color(0xFFE0E0E0)
    val BorderLight = Color(0xFFF0F0F0)
    
    // Status Colors
    val Success = Color(0xFF4CAF50)
    val Error = Color(0xFFF44336)
    val Warning = Color(0xFFFF9800)
    val Info = Color(0xFF2196F3)
    
    // Card Colors
    val CardBackground = Color(0xFFFFFFFF)
    val CardBackgroundVariant = Color(0xFFF0F0F0)
}

object AppDimensions {
    // Spacing
    val SpacingXS = 4.dp
    val SpacingS = 8.dp
    val SpacingM = 12.dp
    val SpacingL = 16.dp
    val SpacingXL = 20.dp
    val SpacingXXL = 24.dp
    val SpacingXXXL = 32.dp
    
    // Border Radius
    val RadiusS = 8.dp
    val RadiusM = 12.dp
    val RadiusL = 16.dp
    val RadiusXL = 24.dp
    val RadiusCircle = 50.dp
    
    // Icon Sizes
    val IconSmall = 16.dp
    val IconMedium = 24.dp
    val IconLarge = 32.dp
    val IconXLarge = 40.dp
    
    // Button Heights
    val ButtonHeightSmall = 40.dp
    val ButtonHeightMedium = 48.dp
    val ButtonHeightLarge = 56.dp
    
    // Card Elevation
    val ElevationNone = 0.dp
    val ElevationLow = 2.dp
    val ElevationMedium = 4.dp
    val ElevationHigh = 8.dp
}

// Amaranth Font Family
val AmaranthFontFamily = FontFamily(
    Font(R.font.amaranth_regular, FontWeight.Normal),
    Font(R.font.amaranth_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.amaranth_bold, FontWeight.Bold),
    Font(R.font.amaranth_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

// Custom Typography using Amaranth font
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AmaranthFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

object AppTypographySizes {
    // Font Sizes
    val FontSizeXS = 10.sp
    val FontSizeS = 12.sp
    val FontSizeM = 14.sp
    val FontSizeL = 16.sp
    val FontSizeXL = 18.sp
    val FontSizeXXL = 20.sp
    val FontSizeXXXL = 24.sp
    val FontSizeHuge = 32.sp
}

