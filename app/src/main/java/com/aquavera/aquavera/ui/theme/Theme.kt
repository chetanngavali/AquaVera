package com.aquavera.aquavera.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AquaVeraColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = DarkGreen,
    onSecondary = Color.White,
    background = BackgroundGray,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = LightGreen,
    onSurfaceVariant = DarkGreen
)

@Composable
fun AquaVeraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AquaVeraColorScheme,
        typography = Typography,
        content = content
    )
}
