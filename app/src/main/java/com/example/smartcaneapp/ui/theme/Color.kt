package com.example.smartcaneapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val primaryContainer = Color(0x28474181)

val DarkColorScheme = darkColorScheme(

    primary =  Color(0xFFDCD7FD),
    onPrimary = Color(0xFF1C1C2A),
    primaryContainer = Color(0x28474181),
    onPrimaryContainer = Color(0xFFDDDFFF),

    secondary = Color(0xFF2D2D2D),
    onSecondary = Color(0xFFDDDFFF),
    secondaryContainer = Color(0x12FFFFFF),
    onSecondaryContainer = Color(0xFFDDDFFF),

    tertiary = Color(0xFF373737),
    onTertiary = Color(0xFF373737),
    tertiaryContainer = Color(0x390D001C),
    onTertiaryContainer = Color(0xFF464646),
    surface = Color(0xFF1C1C2A),
    onSurface = Color(0xFFDCD7FD),
    onError = Color(0xFF4CAF50),

)

val LightColorScheme = lightColorScheme(

    primary = Color(0xFF5A51A1), // Lighter version of dark mode primary
    onPrimary = Color(0xFFFFFFFF), // White text/icons on primary
    primaryContainer = Color(0xFFE0DBFD), // Light lavender, matching dark mode
    onPrimaryContainer = Color(0xFF1C1C2A), // Dark text/icons for contrast

    secondary = Color(0xFFB0B0B0), // Lighter neutral gray for secondary
    onSecondary = Color(0xFF1C1C2A), // Dark text/icons for contrast
    secondaryContainer = Color(0xFFE7E7F5), // Light gray background for secondary elements
    onSecondaryContainer = Color(0xFF2D2D2D), // Darker text/icons on container

    tertiary = Color(0xFF9C9C9C), // Lightened gray for accents
    onTertiary = Color(0xFF1C1C2A), // Darker text/icons on tertiary
    tertiaryContainer = Color(0xFFE5D9FF), // Soft pastel purple for highlights
    onTertiaryContainer = Color(0xFF464646), // Darker text/icons for contrast

    surface = Color(0xFFEBEBF5), // White background for a clean UI
    onSurface = Color(0xFF1C1C2A) // Dark text/icons on surface
)

/*

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2256FF),    // Deep Purple
    onPrimary = Color(0xFFFFFFFF),   // White
    secondary = Color(0xFFBFEAD2),   // Mint
    onSecondary = Color(0xFFFFFFFF), // White
    background = Color(0xFFFAFAFA),  // Light Lavender
    surface = Color(0xFFFFFFFF),     // White
    onSurface = Color(0xFF212121),   // Dark Gray
    tertiary = Color(0xFF3D4127),    // Vivid Blue
    error = Color(0xFFD32F2F),       // Dark Red
    onError = Color(0xFFFFFFFF),     // White
)
*/


