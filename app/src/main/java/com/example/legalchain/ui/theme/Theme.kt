package com.example.legalchain.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Primary brand green (used for buttons)
private val BrandGreen = Color(0xFF004D40)
private val BrandGreenLight = Color(0xFF00695C)
private val BrandAmber = Color(0xFFFFC107)

// Light color scheme (normal app)
private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    secondary = Color(0xFF00695C),
    background = Color(0xFFF7F8F9),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    error = Color(0xFFB00020)
)

// Dark color scheme (deep dark green background — Option B)
private val DarkColors = darkColorScheme(
    primary = BrandGreen,
    onPrimary = Color.Black,
    secondary = BrandGreenLight,
    background = Color(0xFF002B24), // very dark green background
    surface = Color(0xFF083028), // deep forest card background
    onSurface = Color(0xFFEFFAF6),
    error = Color(0xFFEF9A9A)
)

@Composable
fun LegalChainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // dynamic color not needed here but kept for compatibility
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
