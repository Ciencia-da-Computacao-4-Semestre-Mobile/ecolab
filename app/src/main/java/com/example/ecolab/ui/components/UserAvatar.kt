package com.example.ecolab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ecolab.ui.theme.Palette

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    displayName: String?,
    email: String?,
    photoUrl: Any?
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiaryContainer),
        contentAlignment = Alignment.Center
    ) {
        when (photoUrl) {
            is String -> {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            is Int -> {
                Image(
                    painter = painterResource(id = photoUrl),
                    contentDescription = "Avatar de Perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                val initial = displayName?.firstOrNull()?.uppercase() ?: email?.firstOrNull()?.uppercase() ?: "U"
                Text(
                    text = initial,
                    fontSize = 56.sp,
                    color = Palette.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
