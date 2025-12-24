package com.example.app.ui

import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
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
    // Primary Colors (Indigo-based)
    val Primary = Color(0xFF6366F1)
    val PrimaryLight = Color(0xFFEEF2FF)
    val PrimaryDark = Color(0xFF4F46E5)
    
    // Background Colors
    val Background = Color(0xFFF8FAFC)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1F5F9)
    val SurfaceDark = Color(0xFF0F172A)
    
    // Text Colors
    val TextPrimary = Color(0xFF1E293B)
    val TextSecondary = Color(0xFF64748B)
    val TextTertiary = Color(0xFF94A3B8)
    val TextOnPrimary = Color(0xFFFFFFFF)
    
    // Accent Colors
    val Accent = Color(0xFF10B981)
    val AccentLight = Color(0xFFECFDF5)
    val AccentRed = Color(0xFFEF4444)
    
    // Border Colors
    val Border = Color(0xFFE2E8F0)
    val BorderLight = Color(0xFFF1F5F9)
    
    // Status Colors
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)
    
    // Card Colors
    val CardBackground = Color(0xFFFFFFFF)
    val CardBackgroundVariant = Color(0xFFF8FAFC)
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
    val RadiusS = 10.dp
    val RadiusM = 14.dp
    val RadiusL = 20.dp
    val RadiusXL = 28.dp
    val RadiusCircle = 100.dp
    
    // Icon Sizes
    val IconXS = 16.dp
    val IconS = 18.dp
    val IconSmall = 18.dp
    val IconM = 24.dp
    val IconMedium = 24.dp
    val IconL = 32.dp
    val IconLarge = 32.dp
    val IconXLarge = 44.dp
    val IconXXL = 48.dp
    
    // Button Heights
    val ButtonHeightSmall = 44.dp
    val ButtonHeightMedium = 52.dp
    val ButtonHeightLarge = 60.dp
    
    // Card Elevation
    val ElevationNone = 0.dp
    val ElevationLow = 1.dp
    val ElevationMedium = 4.dp
    val ElevationHigh = 12.dp
    
    // Additional Radius
    val RadiusXS = 6.dp
    val RadiusXXL = 32.dp
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

val AppLightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.TextOnPrimary,
    primaryContainer = AppColors.PrimaryLight,
    onPrimaryContainer = AppColors.PrimaryDark,
    secondary = AppColors.Accent,
    onSecondary = AppColors.TextOnPrimary,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.TextSecondary,
    outline = AppColors.Border,
    error = AppColors.Error,
    onError = AppColors.TextOnPrimary
)

val AppDarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.TextOnPrimary,
    primaryContainer = AppColors.PrimaryDark,
    onPrimaryContainer = AppColors.PrimaryLight,
    secondary = AppColors.Accent,
    onSecondary = AppColors.TextOnPrimary,
    background = AppColors.SurfaceDark,
    onBackground = AppColors.TextOnPrimary,
    surface = AppColors.SurfaceDark,
    onSurface = AppColors.TextOnPrimary,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = AppColors.TextTertiary,
    outline = Color(0xFF334155),
    error = AppColors.Error,
    onError = AppColors.TextOnPrimary
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

