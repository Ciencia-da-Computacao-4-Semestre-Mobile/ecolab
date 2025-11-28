package com.example.ecolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ecolab.ui.navigation.AppNavHost
import com.example.ecolab.ui.theme.EcoLabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launchIntent = intent
        val scenario = if (launchIntent?.action == "com.google.intent.action.TEST_LOOP") {
            launchIntent.getIntExtra("scenario", 0)
        } else 0
        installSplashScreen()
        setContent {
            EcoLabTheme {
                AppNavHost()
            }
        }
    }
}
