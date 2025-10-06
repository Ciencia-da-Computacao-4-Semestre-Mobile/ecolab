package com.example.ecolab.presentation.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = EcoGreen,
    onPrimary = White,
    primaryContainer = EcoGreenHover,
    onPrimaryContainer = White,
    secondary = EcoGreenLight,
    onSecondary = White,
    tertiary = EcoAccent,
    onTertiary = White,
    surfaceVariant = NeutralLightVariant,
    onSurfaceVariant = NeutralMedium,
    background = NeutralLight,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    outline = Divider,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = EcoGreenLight,
    onPrimary = Black,
    primaryContainer = EcoGreen,
    onPrimaryContainer = White,
    secondary = EcoBlueLight,
    tertiary = EcoAccent,
    onSecondary = Black,
    surfaceVariant = NeutralDarkVariant,
    onSurfaceVariant = White,
    background = NeutralDark,
    onBackground = White,
    surface = NeutralDarkSurface,
    onSurface = White,
    error = ErrorRedLight
)

@Composable
fun EcoLabTheme(
    useDarkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EcoTypography,
        shapes = EcoShapes,
        content = content
    )
}