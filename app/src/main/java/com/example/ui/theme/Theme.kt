package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SignageDarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color(0xFF090D16),
    primaryContainer = CardSurface,
    onPrimaryContainer = TextPrimary,
    secondary = GoldAccent,
    onSecondary = Color(0xFF090D16),
    background = DarkBg,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF16233B),
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SignageDarkColorScheme,
        typography = Typography,
        content = content
    )
}
