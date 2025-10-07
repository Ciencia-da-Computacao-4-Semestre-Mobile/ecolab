package com.example.ecolab.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Início", Icons.Default.Home)
    object Map : BottomNavItem("map", "Mapa", Icons.Default.Map)
    object Library : BottomNavItem("library", "Biblioteca", Icons.Default.MenuBook)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
}
