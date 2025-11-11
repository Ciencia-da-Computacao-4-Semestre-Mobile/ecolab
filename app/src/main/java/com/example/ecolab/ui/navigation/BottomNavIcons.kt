package com.example.ecolab.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

object BottomNavIcons {
    @Composable
    fun getIconForRoute(route: String, isSelected: Boolean): ImageVector {
        return when (route) {
            "home" -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
            "map" -> if (isSelected) Icons.Filled.Map else Icons.Outlined.Map
            "library" -> if (isSelected) Icons.AutoMirrored.Filled.MenuBook else Icons.Outlined.MenuBook
            "profile" -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
            else -> Icons.Filled.Home
        }
    }
}