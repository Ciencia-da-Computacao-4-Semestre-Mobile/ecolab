package com.example.ecolab.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ecolab.presentation.components.BottomNavigationBar
import com.example.ecolab.presentation.ui.conquests.ConquestsScreen
import com.example.ecolab.presentation.ui.education.EducationScreen
import com.example.ecolab.presentation.ui.login.LoginScreen
import com.example.ecolab.presentation.ui.map.MapScreen
import com.example.ecolab.presentation.ui.profile.ProfileScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Login.route) {
                BottomNavigationBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoggedIn = { navController.navigate(Screen.Map.route) })
            }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.Education.route) { EducationScreen() }
            composable(Screen.Conquests.route) { ConquestsScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}