package com.example.ecolab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Enum exclusivo da UI para evitar colisão com o modelo
enum class UiRarity {
    COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
}

// Modelo de dados da UI para itens da loja
data class UiStoreItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val icon: String = "🌱",
    val rarity: UiRarity = UiRarity.COMMON,
    val iconRes: Int? = null,
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false
)

// Modelo de dados da UI para itens do inventário
data class UiInventoryItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val icon: String = "📦"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Loja", "Inventário")
    var userCoins by remember { mutableStateOf(1000) }
    var storeItems by remember { mutableStateOf(getInitialStoreItems()) }
    var inventory by remember { mutableStateOf(listOf<UiInventoryItem>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ecolab Store") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header com moedas
            CoinsHeader(coins = userCoins)

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Conteúdo baseado na tab selecionada
            when (selectedTab) {
                0 -> StoreItemsPage(
                    storeItems = storeItems,
                    userCoins = userCoins,
                    onPurchase = { item: UiStoreItem ->
                        if (userCoins >= item.price && !item.isPurchased) {
                            userCoins -= item.price

                            // Atualiza o item como comprado
                            storeItems = storeItems.map {
                                if (it.id == item.id) it.copy(isPurchased = true)
                                else it
                            }

                            // Adiciona ao inventário
                            val existingItem = inventory.find { it.id == item.id }
                            inventory = if (existingItem != null) {
                                inventory.map {
                                    if (it.id == item.id) it.copy(quantity = it.quantity + 1)
                                    else it
                                }
                            } else {
                                inventory + UiInventoryItem(
                                    id = item.id,
                                    name = item.name,
                                    quantity = 1,
                                    icon = item.icon
                                )
                            }
                        }
                    },
                    onEquip = { item: UiStoreItem ->
                        if (item.isPurchased) {
                            storeItems = storeItems.map {
                                if (it.id == item.id) it.copy(isEquipped = !it.isEquipped)
                                else if (it.category == item.category) it.copy(isEquipped = false)
                                else it
                            }
                        }
                    }
                )
                1 -> InventoryPage(
                    inventory = inventory,
                    onUseItem = { item: UiInventoryItem ->
                        inventory = inventory.map {
                            if (it.id == item.id && it.quantity > 0) {
                                it.copy(quantity = it.quantity - 1)
                            } else it
                        }.filter { it.quantity > 0 }
                    }
                )
            }
        }
    }
}

fun getInitialStoreItems(): List<UiStoreItem> {
    return listOf(
        UiStoreItem(1, "Semente de Tomate", "Plante e colha tomates frescos", 50, "Sementes", "🍅", UiRarity.COMMON),
        UiStoreItem(2, "Semente de Alface", "Cultive alfaces orgânicas", 30, "Sementes", "🥬", UiRarity.COMMON),
        UiStoreItem(3, "Fertilizante Orgânico", "Melhora o crescimento das plantas", 100, "Fertilizantes", "🌿", UiRarity.UNCOMMON),
        UiStoreItem(4, "Kit de Irrigação", "Sistema de irrigação automático", 200, "Ferramentas", "💧", UiRarity.RARE),
        UiStoreItem(5, "Composteira", "Transforme resíduos em adubo", 150, "Ferramentas", "♻️", UiRarity.UNCOMMON),
        UiStoreItem(6, "Semente de Cenoura", "Cenouras crocantes e saborosas", 40, "Sementes", "🥕", UiRarity.COMMON),
        UiStoreItem(7, "Pesticida Natural", "Proteja suas plantas naturalmente", 80, "Fertilizantes", "🐛", UiRarity.UNCOMMON),
        UiStoreItem(8, "Vasos Biodegradáveis", "Pack com 5 vasos ecológicos", 60, "Ferramentas", "🪴", UiRarity.COMMON),
        UiStoreItem(9, "Semente Rara de Orquídea", "Flor exótica e valiosa", 300, "Sementes", "🌸", UiRarity.EPIC)
    )
}

@Composable
fun CoinsHeader(coins: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "Moedas",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$coins EcoCoins",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StoreItemsPage(
    storeItems: List<UiStoreItem>,
    userCoins: Int,
    onPurchase: (UiStoreItem) -> Unit,
    onEquip: (UiStoreItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(storeItems) { item ->
            StoreItemCard(
                item = item,
                userCoins = userCoins,
                onPurchase = { onPurchase(item) },
                onEquip = { onEquip(item) }
            )
        }
    }
}

@Composable
fun StoreItemCard(
    item: UiStoreItem,
    userCoins: Int,
    onPurchase: () -> Unit,
    onEquip: () -> Unit
) {
    val canAfford = userCoins >= item.price && !item.isPurchased

    val rarityColor = when (item.rarity) {
        UiRarity.COMMON -> Color(0xFF9E9E9E)
        UiRarity.UNCOMMON -> Color(0xFF4CAF50)
        UiRarity.RARE -> Color(0xFF2196F3)
        UiRarity.EPIC -> Color(0xFF9C27B0)
        UiRarity.LEGENDARY -> Color(0xFFFFC107)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canAfford || item.isPurchased) {
                if (canAfford) onPurchase()
                else if (item.isPurchased) onEquip()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPurchased)
                MaterialTheme.colorScheme.tertiaryContainer
            else if (canAfford)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone do item
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        rarityColor.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.icon,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Informações do item
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isEquipped) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Equipado",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• ${item.rarity.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = rarityColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Botões
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (!item.isPurchased) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.price}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onPurchase,
                        enabled = canAfford,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(if (canAfford) "Comprar" else "Sem moedas")
                    }
                } else {
                    Button(
                        onClick = onEquip,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isEquipped)
                                Color(0xFF4CAF50)
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (item.isEquipped)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (item.isEquipped) "Equipado" else "Equipar")
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryPage(
    inventory: List<UiInventoryItem>,
    onUseItem: (UiInventoryItem) -> Unit
) {
    if (inventory.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Seu inventário está vazio",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Compre itens na loja para começar!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(inventory) { item ->
                InventoryItemCard(
                    item = item,
                    onUse = { onUseItem(item) }
                )
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    item: UiInventoryItem,
    onUse: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone do item
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.icon,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nome e quantidade
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Quantidade: ${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botão usar
            Button(
                onClick = onUse,
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Usar")
            }
        }
    }
}