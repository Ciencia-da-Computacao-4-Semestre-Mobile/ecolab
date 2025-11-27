package com.example.ecolab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.compose.LocalImageLoader
import android.graphics.Bitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip

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
    val isEquipped: Boolean = false,
    val discountPercent: Int = 0,
    val quizBoostPercent: Int = 0
)

// Modelo de dados da UI para itens do inventário
data class UiInventoryItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val icon: String = "📦",
    val iconRes: Int? = null,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun StoreScreen(
    onNavigateBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Avatares", "Selos", "Inventário")
    var ecoPoints by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val imageLoader = LocalImageLoader.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    var avatarItems by remember { mutableStateOf(getAvatarStoreItems(context)) }
    var sealItems by remember { mutableStateOf(getSealStoreItemsV2()) }
    var inventory by remember { mutableStateOf(listOf<UiInventoryItem>()) }
    var lastSaveError by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    suspend fun saveState() {
        try {
            kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val purchasedAvatars = avatarItems.filter { it.isPurchased }.map { it.id }
                    val purchasedSeals = sealItems.filter { it.isPurchased }.map { it.id }
                    val equippedAvatar = avatarItems.find { it.isEquipped }?.id
                    val equippedSeal = sealItems.find { it.isEquipped }?.id
                    val equippedAvatarResId = avatarItems.find { it.isEquipped }?.iconRes
                    val equippedSealEmoji = sealItems.find { it.isEquipped && it.iconRes == null }?.icon
                    val data = hashMapOf(
                        "purchasedAvatars" to purchasedAvatars,
                        "purchasedSeals" to purchasedSeals,
                        "equippedAvatar" to equippedAvatar,
                        "equippedSeal" to equippedSeal,
                        
                        "equippedAvatarResId" to equippedAvatarResId,
                        "equippedSealEmoji" to equippedSealEmoji
                    )
                    firestore.collection("users").document(uid).collection("store").document("state").set(data).await()
                    firestore.collection("users").document(uid).update(
                        mapOf(
                            "purchasedAvatars" to purchasedAvatars,
                            "purchasedSeals" to purchasedSeals
                        )
                    ).await()
                    val purchasedItemNames = (avatarItems.filter { it.isPurchased && it.iconRes != null }.map {
                        runCatching { context.resources.getResourceEntryName(it.iconRes!!) }.getOrNull()
                    } + sealItems.filter { it.isPurchased && it.iconRes != null }.map {
                        runCatching { context.resources.getResourceEntryName(it.iconRes!!) }.getOrNull()
                    }).filterNotNull()
                    if (purchasedItemNames.isNotEmpty()) {
                        firestore.collection("users").document(uid)
                            .update("purchasedItems", com.google.firebase.firestore.FieldValue.arrayUnion(*purchasedItemNames.toTypedArray()))
                            .await()
                    }
                    writeInventoryCache(context, inventory)
                }
            }
        } catch (e: Exception) {
            lastSaveError = e.message
            android.util.Log.e("StoreSave", "saveState error", e)
        }
    }

    LaunchedEffect(Unit) {
        try {
            val cacheInv = readInventoryCache(context)
            if (cacheInv.isNotEmpty()) {
                inventory = cacheInv
                val cachedIdsAv = cacheInv.filter { it.category == "Avatares" }.map { it.id }.toSet()
                val cachedIdsSe = cacheInv.filter { it.category == "Selos" }.map { it.id }.toSet()
                avatarItems = avatarItems.map { it.copy(isPurchased = cachedIdsAv.contains(it.id)) }
                sealItems = sealItems.map { it.copy(isPurchased = cachedIdsSe.contains(it.id)) }
            }
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val userDoc = firestore.collection("users").document(uid).get().await()
                ecoPoints = userDoc.getLong("totalPoints")?.toInt() ?: 0
                val stateDoc = firestore.collection("users").document(uid).collection("store").document("state").get().await()
                if (stateDoc.exists()) {
                    val purchasedAvatarsState = (stateDoc.get("purchasedAvatars") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
                    val purchasedSealsState = (stateDoc.get("purchasedSeals") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
                    val purchasedAvatarsUser = (userDoc.get("purchasedAvatars") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
                    val purchasedSealsUser = (userDoc.get("purchasedSeals") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
                    val purchasedItemStrings = (userDoc.get("purchasedItems") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    val purchasedFromStringsAv = purchasedItemStrings.filter { it.startsWith("avatar_") }
                        .mapNotNull { name ->
                            runCatching {
                                val f = com.example.ecolab.R.drawable::class.java.getDeclaredField(name)
                                f.getInt(null)
                            }.getOrNull()
                        }
                    val purchasedFromStringsSe = purchasedItemStrings.filter { it.startsWith("seal_") }
                        .mapNotNull { name ->
                            runCatching {
                                val f = com.example.ecolab.R.drawable::class.java.getDeclaredField(name)
                                f.getInt(null)
                            }.getOrNull()
                        }
                    val purchasedAvatars = (purchasedAvatarsState + purchasedAvatarsUser + purchasedFromStringsAv)
                        .map { normalizeAvatarId(context, it) }
                        .distinct()
                    val purchasedSeals = (purchasedSealsState + purchasedSealsUser + purchasedFromStringsSe)
                        .map { normalizeSealId(context, it) }
                        .distinct()
                    val equippedAvatar = stateDoc.getLong("equippedAvatar")?.toInt()
                    val equippedSeal = stateDoc.getLong("equippedSeal")?.toInt()
                    val equippedAvatarResId = stateDoc.getLong("equippedAvatarResId")?.toInt()
                    val equippedSealEmoji = stateDoc.getString("equippedSealEmoji")
                    avatarItems = avatarItems.map { it.copy(isPurchased = purchasedAvatars.contains(it.id), isEquipped = equippedAvatar == it.id) }
                    sealItems = sealItems.map { it.copy(isPurchased = purchasedSeals.contains(it.id), isEquipped = equippedSeal == it.id) }
                    val invFromAvatars = avatarItems.filter { it.isPurchased }
                        .map { UiInventoryItem(it.id, it.name, 1, it.icon, it.iconRes, "Avatares") }
                    val invFromSeals = sealItems.filter { it.isPurchased }
                        .map { UiInventoryItem(it.id, it.name, 1, it.icon, it.iconRes, "Selos") }
                    inventory = (invFromAvatars + invFromSeals)
                    writeInventoryCache(context, inventory)

                    val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
                    equippedAvatarResId?.let { resId ->
                        if (resId != 0 && runCatching { context.resources.getResourceName(resId) }.isSuccess) {
                            prefs.edit().putInt("equipped_avatar_res_id", resId).apply()
                        }
                    }
                    if (!equippedSealEmoji.isNullOrEmpty()) {
                        prefs.edit().putString("equipped_seal_emoji", equippedSealEmoji).apply()
                        prefs.edit().remove("equipped_seal_res_id").apply()
                    } else {
                        val sealRes = sealItems.find { it.isEquipped }?.iconRes ?: 0
                        if (sealRes != 0 && runCatching { context.resources.getResourceName(sealRes) }.isSuccess) {
                            prefs.edit().putInt("equipped_seal_res_id", sealRes).apply()
                            prefs.edit().remove("equipped_seal_emoji").apply()
                        }
                    }
                    coroutineScope.launch { saveState() }
                } else {
                    val purchasedAvatarsUser = (userDoc.get("purchasedAvatars") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
                    val purchasedSealsUser = (userDoc.get("purchasedSeals") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
                    val purchasedItemStrings = (userDoc.get("purchasedItems") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    val purchasedFromStringsAv = purchasedItemStrings.filter { it.startsWith("avatar_") }
                        .mapNotNull { name ->
                            runCatching {
                                val f = com.example.ecolab.R.drawable::class.java.getDeclaredField(name)
                                f.getInt(null)
                            }.getOrNull()
                        }
                    val purchasedFromStringsSe = purchasedItemStrings.filter { it.startsWith("seal_") }
                        .mapNotNull { name ->
                            runCatching {
                                val f = com.example.ecolab.R.drawable::class.java.getDeclaredField(name)
                                f.getInt(null)
                            }.getOrNull()
                        }
                    val normalizedAv = purchasedAvatarsUser.map { normalizeAvatarId(context, it) }.toSet()
                    val normalizedSe = purchasedSealsUser.map { normalizeSealId(context, it) }.toSet()
                    val normalizedAvFromStrings = purchasedFromStringsAv.map { normalizeAvatarId(context, it) }.toSet()
                    val normalizedSeFromStrings = purchasedFromStringsSe.map { normalizeSealId(context, it) }.toSet()
                    avatarItems = avatarItems.map { it.copy(isPurchased = normalizedAv.contains(it.id) || normalizedAvFromStrings.contains(it.id)) }
                    sealItems = sealItems.map { it.copy(isPurchased = normalizedSe.contains(it.id) || normalizedSeFromStrings.contains(it.id)) }
                    val invFromAvatars = avatarItems.filter { it.isPurchased }.map { UiInventoryItem(it.id, it.name, 1, it.icon, it.iconRes, "Avatares") }
                    val invFromSeals = sealItems.filter { it.isPurchased }.map { UiInventoryItem(it.id, it.name, 1, it.icon, it.iconRes, "Selos") }
                    inventory = (invFromAvatars + invFromSeals)
                    coroutineScope.launch { saveState() }
                    writeInventoryCache(context, inventory)
                }
            }
        } catch (e: Exception) {
            lastSaveError = e.message
            android.util.Log.e("StoreLoad", "load error", e)
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Loja",
                        fontWeight = FontWeight.Bold,
                        color = com.example.ecolab.ui.theme.Palette.text
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = com.example.ecolab.ui.theme.Palette.text)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
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
                val equippedAvatarResId = avatarItems.find { it.isEquipped }?.iconRes
                val equippedSealResId = sealItems.find { it.isEquipped }?.iconRes
                val equippedSealEmoji = sealItems.find { it.isEquipped && it.iconRes == null }?.icon
                EcoPointsHeader(
                    points = ecoPoints,
                    equippedAvatarResId = equippedAvatarResId,
                    equippedSealResId = equippedSealResId,
                    equippedSealEmoji = equippedSealEmoji
                )
                if (lastSaveError != null) {
                    androidx.compose.material3.Text(
                        text = "Falha ao salvar/carregar: ${lastSaveError}",
                        color = Color.Red,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.Transparent,
                        indicator = {},
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = pagerState.currentPage == index
                            Tab(
                                selected = isSelected,
                                    onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } },
                                selectedContentColor = Color.White,
                                unselectedContentColor = com.example.ecolab.ui.theme.Palette.primary
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(if (isSelected) com.example.ecolab.ui.theme.Palette.primary else Color.Transparent)
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = when (index) {
                                            0 -> Icons.Default.Person
                                            1 -> Icons.Default.Star
                                            else -> Icons.Default.Inventory
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else com.example.ecolab.ui.theme.Palette.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = title,
                                        color = if (isSelected) Color.White else com.example.ecolab.ui.theme.Palette.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                }
            }

            LaunchedEffect(pagerState.currentPage, avatarItems, sealItems) {
                val size40 = with(density) { 40.dp.roundToPx() }
                val size48 = with(density) { 48.dp.roundToPx() }
                val pagesToPrefetch = listOf(pagerState.currentPage, (pagerState.currentPage + 1) % 3)
                pagesToPrefetch.forEach { p ->
                    val ids = when (p) {
                        0 -> avatarItems.mapNotNull { it.iconRes }
                        1 -> sealItems.mapNotNull { it.iconRes }
                        else -> emptyList()
                    }
                    ids.take(16).forEach { id ->
                        val req = ImageRequest.Builder(context)
                            .data(id)
                            .size(size48, size48)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .allowHardware(true)
                            .build()
                        imageLoader.enqueue(req)
                    }
                }
            }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> StoreItemsPage(
                            storeItems = avatarItems,
                            userCoins = ecoPoints,
                            onPurchase = { item: UiStoreItem, price: Int ->
                                if (ecoPoints >= price && !item.isPurchased) {
                                    ecoPoints -= price
                                    val uid = auth.currentUser?.uid
                                    if (uid != null) {
                                        firestore.collection("users").document(uid).update("totalPoints", ecoPoints)
                                    }

                                avatarItems = avatarItems.map {
                                    if (it.id == item.id) it.copy(isPurchased = true)
                                    else it
                                }

                                val exists = inventory.any { it.id == item.id }
                                if (!exists) {
                                    inventory = inventory + UiInventoryItem(
                                        id = item.id,
                                        name = item.name,
                                        quantity = 1,
                                        icon = item.icon,
                                        iconRes = item.iconRes,
                                        category = item.category
                                    )
                                }
                                coroutineScope.launch { saveState() }
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
                        val res = equipped?.iconRes ?: 0
                        val isValid = res != 0 && runCatching { context.resources.getResourceName(res) }.isSuccess
                        if (!isValid) {
                            prefs.edit().remove("equipped_avatar_res_id").apply()
                        }
                                coroutineScope.launch { saveState() }
                            }
                        }
                    )

                        1 -> StoreItemsPage(
                            storeItems = sealItems,
                            userCoins = ecoPoints,
                            onPurchase = { item: UiStoreItem, price: Int ->
                                if (ecoPoints >= price && !item.isPurchased) {
                                    ecoPoints -= price
                                    val uid = auth.currentUser?.uid
                                    if (uid != null) {
                                        firestore.collection("users").document(uid).update("totalPoints", ecoPoints)
                                    }

                                sealItems = sealItems.map {
                                    if (it.id == item.id) it.copy(isPurchased = true)
                                    else it
                                }

                                val exists = inventory.any { it.id == item.id }
                                if (!exists) {
                                    inventory = inventory + UiInventoryItem(
                                        id = item.id,
                                        name = item.name,
                                        quantity = 1,
                                        icon = item.icon,
                                        iconRes = item.iconRes,
                                        category = item.category
                                    )
                                }
                                coroutineScope.launch { saveState() }
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

                                val sealRes = equippedSeal?.iconRes ?: 0
                                val sealValid = sealRes != 0 && runCatching { context.resources.getResourceName(sealRes) }.isSuccess
                                if (!sealValid) {
                                    prefs.edit().remove("equipped_seal_res_id").apply()
                                }

                                
                                coroutineScope.launch { saveState() }
                    }
                }
            )

                        2 -> InventoryPage(
                            inventory = inventory,
                            equippedAvatarId = avatarItems.find { it.isEquipped }?.id,
                            equippedSealId = sealItems.find { it.isEquipped }?.id,
                            storeAvatarItems = avatarItems,
                            storeSealItems = sealItems,
                            onEquipItem = { item: UiInventoryItem ->
                                if (item.category == "Avatares") {
                                    avatarItems = avatarItems.map {
                                        if (it.id == item.id) it.copy(isEquipped = true)
                                        else it.copy(isEquipped = false)
                                    }
                                    val equipped = avatarItems.find { it.isEquipped }
                                    val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
                                    val res = equipped?.iconRes ?: 0
                                    val isValid = res != 0 && runCatching { context.resources.getResourceName(res) }.isSuccess
                                    if (isValid) {
                                        prefs.edit().putInt("equipped_avatar_res_id", res).apply()
                                    } else {
                                        prefs.edit().remove("equipped_avatar_res_id").apply()
                                    }
                                } else if (item.category == "Selos") {
                                    sealItems = sealItems.map {
                                        if (it.id == item.id) it.copy(isEquipped = true)
                                        else it.copy(isEquipped = false)
                                    }
                                    val equippedSeal = sealItems.find { it.isEquipped }
                                    val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
                                    if (equippedSeal?.iconRes != null && equippedSeal.iconRes != 0) {
                                        prefs.edit().putInt("equipped_seal_res_id", equippedSeal.iconRes!!).apply()
                                        prefs.edit().remove("equipped_seal_emoji").apply()
                                    } else {
                                        prefs.edit().putString("equipped_seal_emoji", equippedSeal?.icon ?: "").apply()
                                        prefs.edit().remove("equipped_seal_res_id").apply()
                                    }
                                    
                                }
                                coroutineScope.launch { saveState() }
                            },
                            onUnequipItem = { item: UiInventoryItem ->
                                if (item.category == "Avatares") {
                                    avatarItems = avatarItems.map { it.copy(isEquipped = false) }
                                    val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
                                    prefs.edit().remove("equipped_avatar_res_id").apply()
                                } else if (item.category == "Selos") {
                                    sealItems = sealItems.map { it.copy(isEquipped = false) }
                                    val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
                                    prefs.edit().remove("equipped_seal_res_id").apply()
                                    prefs.edit().remove("equipped_seal_emoji").apply()
                                    
                                }
                                coroutineScope.launch { saveState() }
                            }
                        )
                    }
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
private fun EcoPointsHeader(
    points: Int,
    equippedAvatarResId: Int?,
    equippedSealResId: Int?,
    equippedSealEmoji: String?
) {
    val context = LocalContext.current
    val resources = LocalContext.current.resources
    val equippedAvatarRes = equippedAvatarResId ?: 0
    val equippedSealRes = equippedSealResId ?: 0
    val isValidAvatarRes = equippedAvatarRes != 0 && runCatching { resources.getResourceName(equippedAvatarRes) }.isSuccess
    val isValidSealRes = equippedSealRes != 0 && runCatching { resources.getResourceName(equippedSealRes) }.isSuccess

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            com.example.ecolab.ui.theme.Palette.primary.copy(alpha = 0.9f),
                            com.example.ecolab.ui.theme.Palette.secondary
                        )
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    ) {
                        if (isValidAvatarRes && equippedAvatarRes != 0) {
                            AsyncImage(
                                model = run {
                                    val px = with(androidx.compose.ui.platform.LocalDensity.current) { 48.dp.roundToPx() }
                                    ImageRequest.Builder(context)
                                        .data(equippedAvatarRes)
                                        .size(px, px)
                                        .bitmapConfig(Bitmap.Config.RGB_565)
                                        .allowHardware(true)
                                        .crossfade(true)
                                        .build()
                                },
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = com.example.ecolab.ui.theme.Palette.primary,
                                modifier = Modifier.padding(8.dp).fillMaxSize()
                            )
                        }

                        if (equippedSealRes != 0 || !equippedSealEmoji.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isValidSealRes && equippedSealRes != 0) {
                                    AsyncImage(
                                        model = run {
                                            val px = with(androidx.compose.ui.platform.LocalDensity.current) { 16.dp.roundToPx() }
                                            ImageRequest.Builder(context)
                                                .data(equippedSealRes)
                                                .size(px, px)
                                                .bitmapConfig(Bitmap.Config.RGB_565)
                                                .allowHardware(true)
                                                .crossfade(true)
                                                .build()
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                    )
                                } else {
                                    Text(text = (equippedSealEmoji ?: ""), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EnergySavingsLeaf,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EcoPoints",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$points",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                
            }
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
            items(items = storeItems, key = { it.id }) { item ->
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
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = com.example.ecolab.ui.theme.Palette.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                    val res = item.iconRes
                    val isValidRes = res != null && res != 0 && runCatching { context.resources.getResourceName(res) }.isSuccess
                    if (isValidRes && res != null) {
                        AsyncImage(
                            model = run {
                                val px = with(androidx.compose.ui.platform.LocalDensity.current) { 40.dp.roundToPx() }
                                ImageRequest.Builder(context)
                                    .data(res)
                                    .size(px, px)
                                    .bitmapConfig(Bitmap.Config.RGB_565)
                                    .allowHardware(true)
                                    .crossfade(true)
                                    .build()
                            },
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = item.icon,
                            fontSize = 20.sp
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = com.example.ecolab.ui.theme.Palette.secondary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = item.category,
                                fontSize = 12.sp,
                                color = com.example.ecolab.ui.theme.Palette.secondary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = rarityColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = item.rarity.name,
                                fontSize = 12.sp,
                                color = rarityColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
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
                                imageVector = Icons.Default.EnergySavingsLeaf,
                                contentDescription = null,
                                tint = Color.White,
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
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ecolab.ui.theme.Palette.primary)
                        ) {
                            Text(if (canAfford) "Comprar" else "Sem EcoPoints")
                        }
                    } else {
                        Button(
                            onClick = onEquip,
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.isEquipped)
                                    com.example.ecolab.ui.theme.Palette.success
                                else
                                    com.example.ecolab.ui.theme.Palette.primary
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
        equippedAvatarId: Int?,
        equippedSealId: Int?,
        storeAvatarItems: List<UiStoreItem>,
        storeSealItems: List<UiStoreItem>,
        onEquipItem: (UiInventoryItem) -> Unit,
        onUnequipItem: (UiInventoryItem) -> Unit
    ) {
        val avatars = inventory.filter { it.category == "Avatares" }
        val seals = inventory.filter { it.category == "Selos" }

        if (avatars.isEmpty() && seals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = com.example.ecolab.ui.theme.Palette.textMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Seu inventário está vazio",
                        style = MaterialTheme.typography.titleMedium,
                        color = com.example.ecolab.ui.theme.Palette.text
                    )
                    Text(
                        text = "Compre itens na loja para começar!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.example.ecolab.ui.theme.Palette.textMuted
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val storeMap = remember(storeAvatarItems, storeSealItems) { (storeAvatarItems + storeSealItems).associateBy { it.id } }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (avatars.isNotEmpty()) {
                        item {
                            Text(
                                text = "Avatares",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = com.example.ecolab.ui.theme.Palette.text
                            )
                        }
                        items(items = avatars, key = { it.id }) { item ->
                            UiInventoryItemCard(
                                item = item,
                                equippedAvatarId = equippedAvatarId,
                                equippedSealId = equippedSealId,
                                storeItem = storeMap[item.id],
                                onEquip = { onEquipItem(item) },
                                onUnequip = { onUnequipItem(item) }
                            )
                        }
                    }
                    if (seals.isNotEmpty()) {
                        item {
                            Text(
                                text = "Selos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = com.example.ecolab.ui.theme.Palette.text
                            )
                        }
                        items(items = seals, key = { it.id }) { item ->
                            UiInventoryItemCard(
                                item = item,
                                equippedAvatarId = equippedAvatarId,
                                equippedSealId = equippedSealId,
                                storeItem = storeMap[item.id],
                                onEquip = { onEquipItem(item) },
                                onUnequip = { onUnequipItem(item) }
                            )
                        }
                    }
                }
            }
        }
    }

@Composable
private fun UiInventoryItemCard(
        item: UiInventoryItem,
        equippedAvatarId: Int?,
        equippedSealId: Int?,
        storeItem: UiStoreItem?,
        onEquip: () -> Unit,
        onUnequip: () -> Unit
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
                    val context = LocalContext.current
                    val res = item.iconRes
                    val isValidRes = res != null && res != 0 && runCatching { context.resources.getResourceName(res) }.isSuccess
                    if (isValidRes && res != null) {
                        AsyncImage(
                            model = run {
                                val px = with(androidx.compose.ui.platform.LocalDensity.current) { 48.dp.roundToPx() }
                                ImageRequest.Builder(context)
                                    .data(res)
                                    .size(px, px)
                                    .bitmapConfig(Bitmap.Config.RGB_565)
                                    .allowHardware(true)
                                    .crossfade(true)
                                    .build()
                            },
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = item.icon,
                            fontSize = 32.sp
                        )
                    }
                    }

                Spacer(modifier = Modifier.width(16.dp))

                // Nome e detalhes
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val desc = storeItem?.description.orEmpty()
                    if (desc.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val categoryText = storeItem?.category ?: item.category
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = com.example.ecolab.ui.theme.Palette.secondary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = categoryText,
                                fontSize = 12.sp,
                                color = com.example.ecolab.ui.theme.Palette.secondary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        val rarityColor = when (storeItem?.rarity ?: UiRarity.COMMON) {
                            UiRarity.COMMON -> Color(0xFF9E9E9E)
                            UiRarity.UNCOMMON -> Color(0xFF4CAF50)
                            UiRarity.RARE -> Color(0xFF2196F3)
                            UiRarity.EPIC -> Color(0xFF9C27B0)
                            UiRarity.LEGENDARY -> Color(0xFFFF9800)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = rarityColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = (storeItem?.rarity ?: UiRarity.COMMON).name,
                                fontSize = 12.sp,
                                color = rarityColor,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                if (item.category == "Avatares" || item.category == "Selos") {
                    val isEquipped = (item.category == "Avatares" && equippedAvatarId == item.id) ||
                        (item.category == "Selos" && equippedSealId == item.id)
                    OutlinedButton(
                        onClick = if (isEquipped) onUnequip else onEquip,
                        modifier = Modifier.height(36.dp).wrapContentWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isEquipped) Icons.Default.CheckCircle else Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isEquipped) "Desequipar" else "Equipar")
                    }
                } else {
                    Button(
                        onClick = { /* Itens consumíveis */ },
                        enabled = false,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Uso indisponível")
                    }
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
        val fields = com.example.ecolab.R.drawable::class.java.fields
            .filter { it.name.startsWith("avatar_") }
        val items = mutableListOf<UiStoreItem>()
        fields.forEach { field ->
            val resId = field.getInt(null)
            val namePart = field.name.removePrefix("avatar_")
            val num = namePart.toIntOrNull() ?: 0
            val rarity = rarityForNumber(if (num > 0) num else 10)
            val price = priceForNumber(if (num > 0) num else 10)
            items += UiStoreItem(
                id = resId,
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
        return item.price
    }

fun applySealEffectOnEquipV2(context: Context, item: UiStoreItem) {
        val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("equipped_seal_effect_discount_percent", item.discountPercent.coerceIn(0, 90)).apply()
        prefs.edit().putInt("equipped_seal_effect_quiz_boost_percent", item.quizBoostPercent.coerceIn(0, 100)).apply()
}

fun getSealStoreItemsV2(): List<UiStoreItem> {
        val items = mutableListOf<UiStoreItem>()
        val fields = com.example.ecolab.R.drawable::class.java.fields.filter { it.name.startsWith("seal_") }
        fields.forEachIndexed { index, field ->
            val resId = field.getInt(null)
            val namePart = field.name.removePrefix("seal_")
            val num = namePart.toIntOrNull() ?: (100 + index)
            val rarity = rarityForNumber(num)
            val price = priceForNumber(num)
            items += UiStoreItem(
                id = resId,
                name = "Selo #$namePart",
                description = "",
                price = price,
                category = "Selos",
                icon = "",
                rarity = rarity,
                iconRes = resId
            )
        }

        val more = listOf(
            UiStoreItem(101, "Reciclagem", "", 250, "Selos", "♻️", UiRarity.UNCOMMON, null, false, false, 0, 0),
            UiStoreItem(102, "Energia Solar", "", 400, "Selos", "☀️", UiRarity.RARE, null, false, false, 0, 0),
            UiStoreItem(103, "Eco Warrior", "", 800, "Selos", "🛡️", UiRarity.EPIC, null, false, false, 0, 0),
            UiStoreItem(104, "Água Limpa", "", 220, "Selos", "💧", UiRarity.COMMON, null, false, false, 0, 0),
            UiStoreItem(105, "Floresta Viva", "", 260, "Selos", "🌲", UiRarity.COMMON, null, false, false, 0, 0),
            UiStoreItem(106, "Eco Chef", "", 320, "Selos", "🥗", UiRarity.UNCOMMON, null, false, false, 0, 0),
            UiStoreItem(107, "Bike Lover", "", 340, "Selos", "🚲", UiRarity.UNCOMMON, null, false, false, 0, 0),
            UiStoreItem(108, "Sol & Vento", "", 480, "Selos", "🌬️", UiRarity.RARE, null, false, false, 0, 0),
            UiStoreItem(109, "Plástico Zero", "", 520, "Selos", "🧴", UiRarity.RARE, null, false, false, 0, 0),
            UiStoreItem(110, "Guardião Verde", "", 700, "Selos", "🛡️", UiRarity.EPIC, null, false, false, 0, 0),
            UiStoreItem(111, "Sábio Sustentável", "", 720, "Selos", "📚", UiRarity.EPIC, null, false, false, 0, 0),
            UiStoreItem(112, "Lenda Eco", "", 1200, "Selos", "🏆", UiRarity.LEGENDARY, null, false, false, 0, 0),
            UiStoreItem(113, "Compostagem Pro", "", 400, "Selos", "🪱", UiRarity.RARE, null, false, false, 0, 0),
            UiStoreItem(114, "Mar Limpo", "", 450, "Selos", "🌊", UiRarity.RARE, null, false, false, 0, 0),
            UiStoreItem(115, "Cidade Verde", "", 600, "Selos", "🏙️", UiRarity.EPIC, null, false, false, 0, 0),
            UiStoreItem(116, "Agricultura Orgânica", "", 650, "Selos", "🌾", UiRarity.EPIC, null, false, false, 0, 0),
            UiStoreItem(117, "Eco Tech", "", 550, "Selos", "💡", UiRarity.RARE, null, false, false, 0, 0),
            UiStoreItem(118, "Guerreiro da Natureza", "", 780, "Selos", "🗡️", UiRarity.EPIC, null, false, false, 0, 0),
            UiStoreItem(119, "Fauna & Flora", "", 820, "Selos", "🦋", UiRarity.EPIC, null, false, false, 0, 0),
            UiStoreItem(120, "Eco Mestre", "", 1500, "Selos", "👑", UiRarity.LEGENDARY, null, false, false, 0, 0)
        )
        items.addAll(more)
        return items.sortedBy { it.id }
}

private fun normalizeAvatarId(context: Context, id: Int): Int {
    val valid = id != 0 && runCatching { context.resources.getResourceName(id) }.isSuccess
    if (valid) return id
    val name = "avatar_${id}"
    return runCatching {
        val field = com.example.ecolab.R.drawable::class.java.getDeclaredField(name)
        field.getInt(null)
    }.getOrElse { id }
}

private fun normalizeSealId(context: Context, id: Int): Int {
    val valid = id != 0 && runCatching { context.resources.getResourceName(id) }.isSuccess
    if (valid) return id
    val name = "seal_${id}"
    return runCatching {
        val field = com.example.ecolab.R.drawable::class.java.getDeclaredField(name)
        field.getInt(null)
    }.getOrElse { id }
}

private fun writeInventoryCache(context: Context, inventory: List<UiInventoryItem>) {
    try {
        val arr = org.json.JSONArray()
        // Deduplica por id e força quantidade = 1
        val unique = inventory.distinctBy { it.id }
        unique.forEach {
            val obj = org.json.JSONObject()
            obj.put("id", it.id)
            obj.put("name", it.name)
            obj.put("quantity", 1)
            obj.put("icon", it.icon)
            obj.put("iconRes", it.iconRes ?: 0)
            obj.put("category", it.category)
            arr.put(obj)
        }
        val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("inventory_json", arr.toString()).apply()
    } catch (_: Exception) { }
}

private fun readInventoryCache(context: Context): List<UiInventoryItem> {
    return try {
        val prefs = context.getSharedPreferences("ecolab_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("inventory_json", null) ?: return emptyList()
        val arr = org.json.JSONArray(json)
        val out = mutableListOf<UiInventoryItem>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            out += UiInventoryItem(
                id = obj.optInt("id", 0),
                name = obj.optString("name", ""),
                quantity = 1,
                icon = obj.optString("icon", ""),
                iconRes = obj.optInt("iconRes", 0).let { if (it == 0) null else it },
                category = obj.optString("category", "")
            )
        }
        out.distinctBy { it.id }
    } catch (_: Exception) { emptyList() }
}

@Composable
private fun EquippedSection(
    equippedAvatar: UiStoreItem?,
    equippedSeal: UiStoreItem?,
    onUnequipAvatar: () -> Unit,
    onUnequipSeal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (equippedAvatar != null || equippedSeal != null) {
            Text(
                text = "Equipado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = com.example.ecolab.ui.theme.Palette.text,
                modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (equippedAvatar != null) {
                    Surface(shape = RoundedCornerShape(16.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(equippedAvatar.name, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(onClick = onUnequipAvatar, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                                Text("Desequipar")
                            }
                        }
                    }
                }

                if (equippedSeal != null) {
                    Surface(shape = RoundedCornerShape(16.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (equippedSeal.icon.isNotEmpty()) {
                                Text(text = equippedSeal.icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(equippedSeal.name, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(onClick = onUnequipSeal, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                                Text("Desequipar")
                            }
                        }
                    }
                }
            }
        }
    }
}