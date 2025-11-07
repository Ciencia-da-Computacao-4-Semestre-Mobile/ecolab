package com.example.ecolab.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SelectionItem(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)