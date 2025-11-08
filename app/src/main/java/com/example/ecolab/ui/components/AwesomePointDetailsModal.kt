package com.example.ecolab.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.ui.theme.Palette

/**
 * Gera informações sobre materiais aceitos baseado na categoria do ponto de coleta
 */
fun generateMaterialsInfo(category: String, existingMaterials: String?): String {
    // Se já houver materiais específicos, use-os
    if (!existingMaterials.isNullOrEmpty()) {
        return existingMaterials
    }
    
    // Caso contrário, gere baseado na categoria
    return when (category.lowercase()) {
        "ecoponto" -> """• Papel: jornais, revistas, papéis de escritório, caixas de papelão
• Plástico: garrafas PET, embalagens plásticas limpas, sacolas plásticas
• Vidro: garrafas de vidro, potes de vidro (sem tampa)
• Metal: latas de alumínio, latas de aço, tampas metálicas
• Embalagens longa vida (Tetra Pak)"""
        
        "ponto de entrega", "pev", "ponto de entrega voluntária" -> """• Eletrônicos: celulares, computadores, baterias, carregadores
• Lâmpadas: fluorescentes, LED, incandescentes
• Pilhas e baterias
• Óleo de cozinha usado
• Remédios vencidos
• Pneus usados"""
        
        "cooperativa" -> """• Papel e papelão em grandes quantidades
• Plásticos recicláveis
• Metais ferrosos e não-ferrosos
• Vidros
• Material de construção reciclável"""
        
        "pátio de compostagem", "compostagem" -> """• Resíduos orgânicos: restos de alimentos, cascas de frutas e legumes, borra de café, folhas secas, aparas de grama"""
        
        else -> """• Papel e papelão
• Plásticos
• Vidros
• Metais
• Consulte o local para materiais específicos"""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AwesomePointDetailsModal(
    point: CollectionPoint,
    onDismiss: () -> Unit,
    onFavorite: () -> Unit,
    onRouteClick: () -> Unit,
    categoryColor: Color = Palette.primary
) {
    val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val elevation by animateDpAsState(
            targetValue = if (isPressed) 8.dp else 4.dp,
            label = "elevation"
        )

    val gradient = Brush.verticalGradient(
        colors = listOf(
            categoryColor.copy(alpha = 0.95f),
            categoryColor.copy(alpha = 0.75f)
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Palette.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetDefaults.DragHandle(
                    color = Palette.textMuted.copy(alpha = 0.4f),
                    width = 32.dp,
                    height = 4.dp
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header com gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(gradient)
                    .padding(24.dp)
            ) {
                Column {
                    // Nome e favorito
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
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
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = point.name,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                ),
                                color = Color.White
                            )
                            
                            // TODO: Adicionar distância quando disponível no modelo
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { onFavorite() }
                        ) {
                            Icon(
                                imageVector = if (point.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (point.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                                tint = if (point.isFavorite) Color.Red else Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(12.dp).size(28.dp)
                            )
                        }
                    }
                    
                    // Categoria
                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = point.category,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Descrição
            if (point.description.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Palette.surface,
                        contentColor = Palette.text
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Descrição",
                                tint = categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Descrição",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Palette.text
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = point.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Palette.textMuted,
                            lineHeight = 20.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Horários de funcionamento
            val openingHours = point.openingHours
            if (!openingHours.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Palette.surface,
                        contentColor = Palette.text
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Horários",
                                tint = categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Horários de Funcionamento",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Palette.text
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = openingHours,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Palette.textMuted
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Materiais aceitos - sempre mostra informações baseadas na categoria
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Palette.surface,
                    contentColor = Palette.text
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Recycling,
                            contentDescription = "Materiais",
                            tint = categoryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Materiais Aceitos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Palette.text
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = generateMaterialsInfo(point.category, point.materials),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Palette.textMuted
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botão de rota
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation, RoundedCornerShape(16.dp), clip = false)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onRouteClick() },
                shape = RoundedCornerShape(16.dp),
                color = Palette.primary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Traçar rota",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Traçar Rota",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}