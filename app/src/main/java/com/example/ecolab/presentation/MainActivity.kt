package com.example.ecolab.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.MaterialTheme
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.ecolab.presentation.navigation.AppNavigation
import com.example.ecolab.presentation.theme.EcoLabTheme
import dagger.hilt.android.AndroidEntryPoint
import android.app.Activity


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { App() }
    }
}

@Composable
private fun App() {
    EcoLabTheme {
        val navController = rememberNavController()
        // Harmoniza a status bar com a cor primária do tema
        val view = LocalView.current
        val window = (view.context as Activity).window
        val statusBarColor = MaterialTheme.colorScheme.primary
        val lightIcons = statusBarColor.luminance() > 0.5f
        val navBarColor = MaterialTheme.colorScheme.surface
        val lightNavIcons = navBarColor.luminance() > 0.5f
        SideEffect {
            window.statusBarColor = statusBarColor.toArgb()
            window.navigationBarColor = navBarColor.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = lightIcons
            controller.isAppearanceLightNavigationBars = lightNavIcons
        }
        AppNavigation(navController)
    }
}