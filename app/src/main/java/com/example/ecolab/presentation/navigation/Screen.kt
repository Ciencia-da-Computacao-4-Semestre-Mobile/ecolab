package com.example.ecolab.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Map : Screen("map")
    data object Education : Screen("education")
    data object Conquests : Screen("conquests")
    data object Profile : Screen("profile")
}