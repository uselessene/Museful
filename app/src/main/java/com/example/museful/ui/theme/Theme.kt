package com.example.museful.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF803D4D),
    background = Color(0xFFF8E9E2),
    surface = Color(0xFFF1DED7),
    onBackground = Color(0xFF3B2D2D),
    onSurface = Color(0xFF3B2D2D),
    secondary = Color(0xFFF2D3CB),
    tertiary = Color(0xFFEBC8C2)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE8A5B3),
    background = Color(0xFF1E1A19),
    surface = Color(0xFF2E2523),
    onBackground = Color(0xFFE0D5D2),
    onSurface = Color(0xFFE0D5D2),
    secondary = Color(0xFF3E2F2C),
    tertiary = Color(0xFF9C5C68)
)

@Composable
fun MusefulTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}