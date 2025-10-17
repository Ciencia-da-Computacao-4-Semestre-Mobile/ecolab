package com.example.ecolab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = Palette.primary,
    secondary = Palette.secondary,
    background = Palette.background,
    surface = Palette.surface,
    onPrimary = Palette.surface,
    onSecondary = Palette.text,
    onBackground = Palette.text,
    onSurface = Palette.text,
    error = Palette.error,
    onError = Palette.surface
)

@Composable
fun EcoLabTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = true
        )
        systemUiController.setStatusBarColor(
            color = Palette.background,
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(
            color = Palette.surface,
            darkIcons = true
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
