package com.example.ecolab.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolab.ui.screens.SelectionItem

@Composable
fun AwesomeThemeCard(
    item: SelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val elevation by animateDpAsState(targetValue = if (isSelected) 12.dp else 4.dp, label = "")
    val gradient = Brush.verticalGradient(
        colors = listOf(
            item.color.copy(alpha = 0.8f),
            item.color.copy(alpha = 0.6f)
        )
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(elevation, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = item.icon, contentDescription = item.name, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                item.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
                color = Color.White
            )
        }
    }
}

@Composable
fun AwesomeGameModeCard(
    item: SelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateDpAsState(
        targetValue = if (isSelected) 1.05f.dp else 1f.dp,
        animationSpec = tween(300),
        label = ""
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) item.color else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier.size(36.dp))
            Column {
                Text(item.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(item.description.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}