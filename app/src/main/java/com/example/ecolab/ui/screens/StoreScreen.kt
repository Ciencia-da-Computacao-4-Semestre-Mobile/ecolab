package com.example.ecolab.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolab.R
import android.content.Context
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

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
    val icon: String = "📦",
    val iconRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Avatares", "Selos", "Inventário")
    var userCoins by remember { mutableStateOf(1000) }
    val context = LocalContext.current
    var avatarItems by remember { mutableStateOf(getAvatarStoreItems(context)) }
    var sealItems by remember { mutableStateOf(getSealStoreItems()) }
    var inventory by remember { mutableStateOf(listOf<UiInventoryItem>()) }

    Scaffold(
        topBar = {
            StoreTopBar(onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(com.example.ecolab.ui.theme.Palette.background)
        ) {
            com.example.ecolab.ui.components.AnimatedParticles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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
                        storeItems = avatarItems,
                        userCoins = userCoins,
                        onPurchase = { item: UiStoreItem, price: Int ->
                            if (userCoins >= price && !item.isPurchased) {
                                userCoins -= price

                                avatarItems = avatarItems.map {
                                    if (it.id == item.id) it.copy(isPurchased = true)
                                    else it
                                }

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
                                        icon = item.icon,
                                        iconRes = item.iconRes
                                    )
                                }
                            }
                        },
                        onEquip = { item: UiStoreItem ->
                            if (item.isPurchased) {
                                avatarItems = avatarItems.map {
                                    if (it.id == item.id) it.copy(isEquipped = !it.isEquipped)
                                    else if (it.category == item.category) it.copy(isEquipped = false)
                                    else it
                                }

                                val equipped = avatarItems.find { it.isEquipped }
                                val prefs = context.getSharedPreferences(
                                    "ecolab_prefs",
                                    android.content.Context.MODE_PRIVATE
                                )
                                if (equipped?.iconRes != null) {
                                    prefs.edit()
                                        .putInt("equipped_avatar_res_id", equipped.iconRes!!)
                                        .apply()
                                } else {
                                    prefs.edit().remove("equipped_avatar_res_id").apply()
                                }
                            }
                        }
                    )

                    1 -> StoreItemsPage(
                        storeItems = sealItems,
                        userCoins = userCoins,
                        onPurchase = { item: UiStoreItem, price: Int ->
                            if (userCoins >= price && !item.isPurchased) {
                                userCoins -= price

                                sealItems = sealItems.map {
                                    if (it.id == item.id) it.copy(isPurchased = true)
                                    else it
                                }

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
                                        icon = item.icon,
                                        iconRes = item.iconRes
                                    )
                                }
                            }
                        },
                        onEquip = { item: UiStoreItem ->
                            if (item.isPurchased) {
                                sealItems = sealItems.map {
                                    if (it.id == item.id) it.copy(isEquipped = !it.isEquipped)
                                    else if (it.category == item.category) it.copy(isEquipped = false)
                                    else it
                                }

                                val equippedSeal = sealItems.find { it.isEquipped }
                                val prefs = context.getSharedPreferences(
                                    "ecolab_prefs",
                                    android.content.Context.MODE_PRIVATE
                                )
                                if (equippedSeal?.iconRes != null) {
                                    prefs.edit()
                                        .putInt("equipped_seal_res_id", equippedSeal.iconRes!!)
                                        .apply()
                                    prefs.edit().remove("equipped_seal_emoji").apply()
                                } else {
                                    prefs.edit()
                                        .putString("equipped_seal_emoji", equippedSeal?.icon ?: "")
                                        .apply()
                                    prefs.edit().remove("equipped_seal_res_id").apply()
                                }

                                equippedSeal?.let {
                                    applySealEffectOnEquip(
                                        context,
                                        it
                                    )
                                }
                    }
                }
            )

                    2 -> InventoryPage(
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

}

@Composable
private fun StoreTopBar(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = com.example.ecolab.ui.theme.Palette.text
                )
            }
        }
    }
}

@Composable
private fun CoinsHeader(coins: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$coins",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val context = LocalContext.current
        val discount = getStoreDiscountPercent(context)
        if (discount > 0) {
            AssistChip(
                onClick = {},
                label = { Text(text = "Desconto ${discount}%") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

private fun getInitialStoreItems(): List<UiStoreItem> {
        return listOf(
            UiStoreItem(
                1,
                "Semente de Tomate",
                "Plante e colha tomates frescos",
                50,
                "Sementes",
                "🍅",
                UiRarity.COMMON
            ),
            UiStoreItem(
                2,
                "Semente de Alface",
                "Cultive alfaces orgânicas",
                30,
                "Sementes",
                "🥬",
                UiRarity.COMMON
            ),
            UiStoreItem(
                3,
                "Fertilizante Orgânico",
                "Melhora o crescimento das plantas",
                100,
                "Fertilizantes",
                "🌿",
                UiRarity.UNCOMMON
            ),
            UiStoreItem(
                4,
                "Kit de Irrigação",
                "Sistema de irrigação automático",
                200,
                "Ferramentas",
                "💧",
                UiRarity.RARE
            ),
            UiStoreItem(
                5,
                "Composteira",
                "Transforme resíduos em adubo",
                150,
                "Ferramentas",
                "♻️",
                UiRarity.UNCOMMON
            ),
            UiStoreItem(
                6,
                "Semente de Cenoura",
                "Cenouras crocantes e saborosas",
                40,
                "Sementes",
                "🥕",
                UiRarity.COMMON
            ),
            UiStoreItem(
                7,
                "Pesticida Natural",
                "Proteja suas plantas naturalmente",
                80,
                "Fertilizantes",
                "🐛",
                UiRarity.UNCOMMON
            ),
            UiStoreItem(
                8,
                "Vasos Biodegradáveis",
                "Pack com 5 vasos ecológicos",
                60,
                "Ferramentas",
                "🪴",
                UiRarity.COMMON
            ),
            UiStoreItem(
                9,
                "Semente Rara de Orquídea",
                "Flor exótica e valiosa",
                300,
                "Sementes",
                "🌸",
                UiRarity.EPIC
            )
        )
    }

    

@Composable
private fun StoreItemsPage(
        storeItems: List<UiStoreItem>,
        userCoins: Int,
        onPurchase: (UiStoreItem, Int) -> Unit,
        onEquip: (UiStoreItem) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(storeItems) { item ->
                UiStoreItemCard(
                    item = item,
                    userCoins = userCoins,
                    onPurchase = { price -> onPurchase(item, price) },
                    onEquip = { onEquip(item) }
                )
            }
        }
    }

@Composable
private fun UiStoreItemCard(
        item: UiStoreItem,
        userCoins: Int,
        onPurchase: (Int) -> Unit,
        onEquip: () -> Unit
    ) {
        val context = LocalContext.current
        val finalPrice = finalPriceFor(item, context)
        val canAfford = userCoins >= finalPrice && !item.isPurchased

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
                    if (canAfford) onPurchase(finalPrice)
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
            val glow = item.rarity == UiRarity.EPIC || item.rarity == UiRarity.LEGENDARY
            val infinite = rememberInfiniteTransition(label = "rarity_glow")
            val glowAlpha by infinite.animateFloat(
                initialValue = if (glow) 0.1f else 0f,
                targetValue = if (glow) 0.3f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow_alpha"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(rarityColor.copy(alpha = glowAlpha), RoundedCornerShape(16.dp)),
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
                    if (item.iconRes != null) {
                        val req = ImageRequest.Builder(LocalContext.current)
                            .data(item.iconRes)
                            .allowHardware(false)
                            .build()
                        Image(
                            painter = rememberAsyncImagePainter(model = req),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Text(
                            text = item.icon,
                            fontSize = 32.sp
                        )
                    }
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
                                text = "$finalPrice",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { onPurchase(finalPrice) },
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
private fun InventoryPage(
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
                    UiInventoryItemCard(
                        item = item,
                        onUse = { onUseItem(item) }
                    )
                }
            }
        }
    }

@Composable
private fun UiInventoryItemCard(
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
                    if (item.iconRes != null) {
                        val req = ImageRequest.Builder(LocalContext.current)
                            .data(item.iconRes)
                            .allowHardware(false)
                            .build()
                        Image(
                            painter = rememberAsyncImagePainter(model = req),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Text(
                            text = item.icon,
                            fontSize = 32.sp
                        )
                    }
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

private fun rarityForNumber(n: Int): UiRarity = when {
        n <= 10 -> UiRarity.COMMON
        n <= 20 -> UiRarity.UNCOMMON
        n <= 30 -> UiRarity.RARE
        n <= 35 -> UiRarity.EPIC
        else -> UiRarity.LEGENDARY
    }

private fun priceForNumber(n: Int): Int = when {
        n <= 10 -> 100 + n * 10
        n <= 20 -> 200 + n * 12
        n <= 30 -> 400 + n * 15
        n <= 35 -> 700 + n * 20
        else -> 1000 + n * 25
    }

fun getAvatarStoreItems(context: Context): List<UiStoreItem> {
        val fields = R.drawable::class.java.fields
            .filter { it.name.startsWith("avatar_") }
        val items = mutableListOf<UiStoreItem>()
        fields.forEach { field ->
            val resId = field.getInt(null)
            val namePart = field.name.removePrefix("avatar_")
            val num = namePart.toIntOrNull() ?: 0
            val rarity = rarityForNumber(if (num > 0) num else 10)
            val price = priceForNumber(if (num > 0) num else 10)
            items += UiStoreItem(
                id = if (num > 0) num else resId,
                name = "Avatar #${if (num > 0) num else namePart}",
                description = "Avatar ${rarity.name.lowercase()} número ${if (num > 0) num else namePart}",
                price = price,
                category = "Avatares",
                icon = "",
                rarity = rarity,
                iconRes = resId,
                isPurchased = false,
                isEquipped = false
            )
        }
        return items.sortedBy { it.id }
    }

fun getSealStoreItems(): List<UiStoreItem> {
        return listOf(
            UiStoreItem(
                id = 101,
                name = "Selo Reciclagem",
                description = "5% desconto na Loja",
                price = 250,
                category = "Selos",
                icon = "♻️",
                rarity = UiRarity.UNCOMMON
            ),
            UiStoreItem(
                id = 102,
                name = "Selo Energia Solar",
                description = "+5% pontos no Quiz",
                price = 400,
                category = "Selos",
                icon = "☀️",
                rarity = UiRarity.RARE
            ),
            UiStoreItem(
                id = 103,
                name = "Selo Eco Warrior",
                description = "10% desconto e +10% pontos",
                price = 800,
                category = "Selos",
                icon = "🛡️",
                rarity = UiRarity.EPIC
            )
        )
    }

private fun applySealEffectOnEquip(context: Context, item: UiStoreItem) {
        val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
        when (item.id) {
            101 -> {
                prefs.edit().putInt("equipped_seal_effect_discount_percent", 5).apply()
                prefs.edit().putInt("equipped_seal_effect_quiz_boost_percent", 0).apply()
            }

            102 -> {
                prefs.edit().putInt("equipped_seal_effect_discount_percent", 0).apply()
                prefs.edit().putInt("equipped_seal_effect_quiz_boost_percent", 5).apply()
            }

            103 -> {
                prefs.edit().putInt("equipped_seal_effect_discount_percent", 10).apply()
                prefs.edit().putInt("equipped_seal_effect_quiz_boost_percent", 10).apply()
            }

            else -> {
                prefs.edit().putInt("equipped_seal_effect_discount_percent", 0).apply()
                prefs.edit().putInt("equipped_seal_effect_quiz_boost_percent", 0).apply()
            }
        }
    }

private fun getStoreDiscountPercent(context: Context): Int {
        val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("equipped_seal_effect_discount_percent", 0)
    }

private fun finalPriceFor(item: UiStoreItem, context: Context): Int {
        val discount = getStoreDiscountPercent(context).coerceIn(0, 90)
        val discounted = (item.price * (100 - discount)) / 100
        return discounted
    }