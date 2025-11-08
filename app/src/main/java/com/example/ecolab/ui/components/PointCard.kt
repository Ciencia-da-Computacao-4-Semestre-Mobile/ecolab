package com.example.ecolab.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.ui.theme.Palette

@Composable
fun PointCard(
    point: CollectionPoint,
    onClick: () -> Unit,
    onFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.surface),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Ícone de local",
                tint = Palette.textMuted,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = point.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Palette.text
                )
                Text(
                    text = point.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Palette.textMuted
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (point.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (point.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                    tint = if (point.isFavorite) Color.Red else Palette.textMuted,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}