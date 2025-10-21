package com.example.ecolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ecolab.ui.navigation.AppNavHost
import com.example.ecolab.ui.theme.EcoLabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoLabTheme {
                AppNavHost()
            }
        }
    }
}
