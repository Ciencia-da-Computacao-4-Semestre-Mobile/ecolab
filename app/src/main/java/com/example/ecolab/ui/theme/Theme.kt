package com.example.ecolab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    secondary = SecondaryGreen,
    onSecondary = White,
    tertiary = AccentTeal,
    onTertiary = White,
    background = BackgroundLight,
    onBackground = OnSurfacePrimary,
    surface = SurfaceLight,
    onSurface = OnSurfacePrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = OnSurfaceSecondary,
    outline = OnSurfaceSecondary,
    error = ErrorRed,
    onError = White
)

@Composable
fun EcoLabTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
