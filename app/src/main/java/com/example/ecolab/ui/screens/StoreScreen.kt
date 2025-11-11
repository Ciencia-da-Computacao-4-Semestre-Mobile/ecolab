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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    onNavigateBack: () -> Unit = {},
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Loja", "Inventário")
    
    // Gradient background como nos quizzes
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Palette.primary.copy(alpha = 0.15f),
            Palette.background,
            Palette.background
        )
    )

    Scaffold(
        containerColor = Palette.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            // Top Bar personalizada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Palette.text,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Loja Eco",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Palette.text
                )
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
                        items = uiState.items.filter { !it.isPurchased },
                        userPoints = userPoints,
                        onPurchase = { item ->
                            viewModel.purchaseItem(item)
                        },
                        onEquip = { item ->
                            viewModel.equipItem(item)
                        }
                    )
                    1 -> InventoryPage(
                        purchasedItems = uiState.items.filter { it.isPurchased },
                        equippedItems = uiState.items.filter { it.isEquipped },
                        onEquip = { item ->
                            viewModel.equipItem(item)
                        }
                    )
                }
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
    var selectedCategory by remember { mutableStateOf<StoreCategory?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Seção de estatísticas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Palette.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pontos disponíveis
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = userPoints.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Palette.primary
                    )
                    Text(
                        text = "Pontos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Palette.textMuted
                    )
                }
                
                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = Palette.textMuted.copy(alpha = 0.3f)
                )
                
                // Itens disponíveis
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = items.size.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Palette.success
                    )
                    Text(
                        text = "Disponíveis",
                        style = MaterialTheme.typography.bodySmall,
                        color = Palette.textMuted
                    )
                }
            }
        }
        
        // Filtros por categoria
        Text(
            text = "Categorias",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val categories = listOf(
            null to "Todos" to Icons.Default.Apps,
            StoreCategory.AVATAR to "Avatares" to Icons.Default.Person,
            StoreCategory.BADGE to "Selos" to Icons.Default.Verified,
            StoreCategory.THEME to "Temas" to Icons.Default.Palette,
            StoreCategory.EFFECT to "Efeitos" to Icons.Default.AutoAwesome
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (categoryPair, icon) ->
                val (category, name) = categoryPair
                val isSelected = selectedCategory == category
                
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Palette.primary else Palette.surface,
                    border = BorderStroke(
                        1.dp, 
                        if (isSelected) Palette.primary else Palette.textMuted.copy(alpha = 0.3f)
                    ),
                    onClick = { selectedCategory = category }
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
                            tint = if (isSelected) Color.White else Palette.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else Palette.text,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        
        // Itens filtrados
        val filteredItems = if (selectedCategory == null) {
            items
        } else {
            items.filter { it.category == selectedCategory }
        }
        
        if (filteredItems.isEmpty()) {
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
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Palette.textMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhum item disponível",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Palette.textMuted,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Volte mais tarde para novidades!",
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
                items(filteredItems) { item ->
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
}

@Composable
fun StoreCategoriesSection(
    onCategorySelected: (StoreCategory?) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<StoreCategory?>(null) }
    
    val categories = listOf(
        null to "Todos" to Icons.Default.Apps,
        StoreCategory.AVATAR to "Avatares" to Icons.Default.Person,
        StoreCategory.BADGE to "Selos" to Icons.Default.Verified,
        StoreCategory.THEME to "Temas" to Icons.Default.Palette,
        StoreCategory.EFFECT to "Efeitos" to Icons.Default.AutoAwesome
    )
    
    Column {
        Text(
            text = "Categorias",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (categoryPair, icon) ->
                val (category, name) = categoryPair
                val isSelected = selectedCategory == category
                
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Palette.primary else Palette.surface,
                    border = BorderStroke(
                        1.dp, 
                        if (isSelected) Palette.primary else Palette.textMuted.copy(alpha = 0.3f)
                    ),
                    onClick = { 
                        selectedCategory = category
                        onCategorySelected(category)
                    }
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
                            tint = if (isSelected) Color.White else Palette.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else Palette.text,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryPage(
    purchasedItems: List<com.example.ecolab.model.StoreItem>,
    equippedItems: List<com.example.ecolab.model.StoreItem>,
    onEquip: (com.example.ecolab.model.StoreItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<StoreCategory?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Seção de estatísticas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Palette.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total de itens
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = purchasedItems.size.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Palette.primary
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = Palette.textMuted
                    )
                }
                
                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = Palette.textMuted.copy(alpha = 0.3f)
                )
                
                // Itens equipados
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = equippedItems.size.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Palette.success
                    )
                    Text(
                        text = "Equipados",
                        style = MaterialTheme.typography.bodySmall,
                        color = Palette.textMuted
                    )
                }
            }
        }
        
        // Filtros por categoria
        Text(
            text = "Categorias",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val categories = listOf(
            null to "Todos" to Icons.Default.Apps,
            StoreCategory.AVATAR to "Avatares" to Icons.Default.Person,
            StoreCategory.BADGE to "Selos" to Icons.Default.Verified,
            StoreCategory.THEME to "Temas" to Icons.Default.Palette,
            StoreCategory.EFFECT to "Efeitos" to Icons.Default.AutoAwesome
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (categoryPair, icon) ->
                val (category, name) = categoryPair
                val isSelected = selectedCategory == category
                
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Palette.primary else Palette.surface,
                    border = BorderStroke(
                        1.dp, 
                        if (isSelected) Palette.primary else Palette.textMuted.copy(alpha = 0.3f)
                    ),
                    onClick = { selectedCategory = category }
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
                            tint = if (isSelected) Color.White else Palette.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else Palette.text,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        
        // Itens filtrados
        val filteredItems = if (selectedCategory == null) {
            purchasedItems
        } else {
            purchasedItems.filter { it.category == selectedCategory }
        }
        
        if (filteredItems.isEmpty()) {
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
                        text = if (purchasedItems.isEmpty()) {
                            "Seu inventário está vazio"
                        } else {
                            "Nenhum item nesta categoria"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = Palette.textMuted,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (purchasedItems.isEmpty()) {
                            "Visite a loja para comprar itens!"
                        } else {
                            "Tente outra categoria!"
                        },
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
                items(filteredItems) { item ->
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
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Ícone do item
                            Text(
                                text = item.iconRes,
                                fontSize = 32.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // Nome do item
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Palette.text,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            
                            // Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.isEquipped) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Equipado",
                                        tint = Palette.success,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Equipado",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Palette.success,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Button(
                                        onClick = { onEquip(item) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Palette.primary,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Equipar",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}