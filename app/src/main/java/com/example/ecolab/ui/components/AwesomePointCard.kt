package com.example.ecolab.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.ui.theme.Palette

@Composable
fun AwesomePointCard(
    point: CollectionPoint,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    categoryColor: Color = Palette.primary,
    isSelected: Boolean = false
) {
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 4.dp,
        label = "card_elevation"
    )

    val gradient = Brush.verticalGradient(
        colors = listOf(
            categoryColor.copy(alpha = 0.9f),
            categoryColor.copy(alpha = 0.7f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone com fundo circular
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Local",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Informações do ponto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = point.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )

                if (point.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = point.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 2
                    )
                }

                // Categoria
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = point.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Botão de favorito
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.clickable { onFavorite() }
            ) {
                Icon(
                    imageVector = if (point.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (point.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                    tint = if (point.isFavorite) Color.Red else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(8.dp).size(24.dp)
                )
            }
        }
    }
}