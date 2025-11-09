package com.example.ecolab.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.model.StoreCategory
// StoreItemCard está definido neste mesmo arquivo
import com.example.ecolab.ui.theme.Palette
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreScreen(
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Loja", "Aparência")
    
    // Animação do header
    val infiniteTransition = rememberInfiniteTransition(label = "Header Animation")
    val headerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Header Offset"
    )
    
    // Gradient background como nos quizzes
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Palette.primary.copy(alpha = 0.15f),
            Palette.background,
            Palette.background
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Header animado com pontos do usuário
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Palette.primary.copy(alpha = 0.3f),
                            Palette.secondary.copy(alpha = 0.2f),
                            Palette.primary.copy(alpha = 0.3f)
                        ),
                        startX = headerOffset * -100f,
                        endX = headerOffset * 100f + 1000f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ícone de moeda/pontos
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Pontos",
                        tint = Palette.achievementsIcon,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = userPoints.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Palette.text
                    )
                }
                
                Text(
                    text = "Seus Pontos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Palette.textMuted
                )
            }
        }

        // Navegação por tabs estilo Quiz
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(280.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                color = Palette.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = pagerState.currentPage == index
                        
                        // Animação de seleção
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "Tab Scale"
                        )
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .scale(scale)
                                .clip(RoundedCornerShape(25.dp))
                                .background(
                                    if (isSelected) Palette.primary else Color.Transparent
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (index == 0) Icons.Default.Store else Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Palette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.White else Palette.primary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }

        // Conteúdo das páginas
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> StoreItemsPage(
                    items = uiState.items,
                    userPoints = userPoints,
                    onPurchase = { item ->
                        viewModel.purchaseItem(item)
                    },
                    onEquip = { item ->
                        viewModel.equipItem(item)
                    }
                )
                1 -> AppearancePage(
                    equippedItems = uiState.items.filter { it.isEquipped }
                )
            }
        }
    }
}

@Composable
fun StoreItemsPage(
    items: List<com.example.ecolab.model.StoreItem>,
    userPoints: Int,
    onPurchase: (com.example.ecolab.model.StoreItem) -> Unit,
    onEquip: (com.example.ecolab.model.StoreItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Categorias
        StoreCategoriesSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Grade de itens
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                StoreItemCard(
                    item = item,
                    userPoints = userPoints,
                    onPurchase = { onPurchase(item) },
                    onEquip = { onEquip(item) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StoreCategoriesSection() {
    val categories = listOf(
        StoreCategory.AVATAR to "Avatares" to Icons.Default.Person,
        StoreCategory.BADGE to "Selos" to Icons.Default.Verified,
        StoreCategory.THEME to "Temas" to Icons.Default.Palette,
        StoreCategory.EFFECT to "Efeitos" to Icons.Default.AutoAwesome
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categories.forEach { (categoryPair, icon) ->
            val (category, name) = categoryPair
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = Palette.surface,
                border = BorderStroke(1.dp, Palette.primary.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = Palette.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Palette.text,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AppearancePage(
    equippedItems: List<com.example.ecolab.model.StoreItem>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Itens Equipados",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (equippedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Palette.surface,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = Palette.textMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhum item equipado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Palette.textMuted,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Visite a loja para equipar itens!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Palette.textMuted.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(equippedItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Palette.surface
                        ),
                        border = BorderStroke(1.dp, Palette.primary.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = item.iconRes,
                                fontSize = 32.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Palette.text,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}