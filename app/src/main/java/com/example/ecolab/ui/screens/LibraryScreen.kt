package com.example.ecolab.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolab.R
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecolab.feature.library.EcoNewsViewModel
import com.example.ecolab.feature.library.NewsState
import kotlinx.coroutines.launch
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.example.ecolab.ui.components.AnimatedParticles
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query
import coil.request.ImageRequest

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onGuideClick: (url: String) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Notícias", "Artigos", "Tutoriais")
    val context = LocalContext.current
    val openUrl: (String) -> Unit = { url ->
        if (url.isNotBlank()) {
            val safeUrl = if (url.startsWith("http")) url else "https://$url"
            val uri = android.net.Uri.parse(safeUrl)
            try {
                val color = Palette.primary
                val builder = CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                    .setToolbarColor(color.toArgb())
                val customTabsIntent = builder.build()
                customTabsIntent.intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY)
                customTabsIntent.launchUrl(context, uri)
            } catch (_: Exception) {
                try {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (_: Exception) { }
            }
        }
    }

    // Background gradient igual ao StoreScreen
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Palette.primary.copy(alpha = 0.15f),
            Palette.background,
            Palette.background
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Biblioteca",
                        fontWeight = FontWeight.Bold,
                        color = Palette.text
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Palette.background)
        ) {
            AnimatedParticles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            selectedContentColor = Color.White,
                            unselectedContentColor = Palette.primary
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(48.dp)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (isSelected) Palette.primary else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = when (index) {
                                            0 -> Icons.Default.Article
                                            1 -> Icons.AutoMirrored.Filled.MenuBook
                                            else -> Icons.Default.School
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Palette.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = title,
                                        color = if (isSelected) Color.White else Palette.primary,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
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
                    0 -> NewsSection(openUrl)
                    1 -> ArticlesSection(openUrl)
                    2 -> TutorialsSection(openUrl)
                }
            }
            }
        }
    }
}

@Composable
private fun NewsSection(onItemClick: (String) -> Unit) {
    val viewModel: EcoNewsViewModel = viewModel()
    val newsState by viewModel.newsState.collectAsState()
    
    // Carrega notícias automaticamente quando a seção é exibida
    LaunchedEffect(Unit) {
        viewModel.loadEnvironmentalNews()
    }
    
    when (val state = newsState) {
        is NewsState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = Palette.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Carregando notícias ambientais...",
                        color = Palette.textMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        is NewsState.Success -> {
            LibraryItemList(items = state.news, onItemClick = onItemClick, showPostedTime = true)
        }
        is NewsState.Empty -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Palette.textMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Sem novas notícias",
                        color = Palette.text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { viewModel.refreshNews() },
                        colors = ButtonDefaults.buttonColors(containerColor = Palette.primary)
                    ) {
                        Text("Atualizar")
                    }
                }
            }
        }
        is NewsState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Palette.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Erro ao carregar notícias",
                        color = Palette.text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verifique sua conexão e tente novamente",
                        color = Palette.textMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { viewModel.loadEnvironmentalNews() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Palette.primary
                        )
                    ) {
                        Text("Tentar Novamente")
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticlesSection(onItemClick: (String) -> Unit) {
    var itemsState by remember { mutableStateOf<List<LibraryItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            itemsState = fetchLibraryItems("artigo", "Artigo", altCollection = "Artigo")
            loading = false
        } catch (ex: Exception) {
            android.util.Log.e("LibraryScreen", "Falha ao carregar artigos", ex)
            error = ex.message
            loading = false
        }
    }

    when {
        loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Palette.primary)
            }
        }
        error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = Palette.error)
                    Text("Erro ao carregar artigos", color = Palette.text)
                    OutlinedButton(onClick = {
                        loading = true
                        error = null
                        scope.launch {
                            try {
                                itemsState = fetchLibraryItems("artigo", "Artigo", altCollection = "Artigo")
                                loading = false
                            } catch (e: Exception) {
                                android.util.Log.e("LibraryScreen", "Falha ao carregar artigos (retry)", e)
                                error = e.message
                                loading = false
                            }
                        }
                    }) {
                        Text("Tentar novamente")
                    }
                }
            }
        }
        else -> {
            LibraryItemList(items = itemsState, onItemClick = onItemClick, showPostedTime = false)
        }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TutorialsSection(onItemClick: (String) -> Unit) {
    var itemsState by remember { mutableStateOf<List<LibraryItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val fallback = listOf(
        LibraryItem(10, "Guia prático de reciclagem em casa", "Como separar, lavar e destinar corretamente", "Reciclagem", "12 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.ecycle.com.br/wp-content/uploads/2021/05/reciclagem.jpg", date = "10 Jan 2025", url = "https://www.ecycle.com.br/reciclagem/"),
        LibraryItem(11, "Montando composteira doméstica", "Passo a passo de compostagem caseira", "Compostagem", "15 min", R.drawable.ic_launcher_foreground, imageUrl = "https://upload.wikimedia.org/wikipedia/commons/9/9b/Compost_bin.jpg", date = "09 Jan 2025", url = "https://pt.wikipedia.org/wiki/Compostagem"),
        LibraryItem(12, "Economia de água: dicas úteis", "Reduza consumo sem perder conforto", "Água", "8 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.ecycle.com.br/wp-content/uploads/2020/07/agua.jpg", date = "08 Jan 2025", url = "https://www.ecycle.com.br/economia-de-agua/"),
        LibraryItem(13, "Energia solar residencial", "Introdução, custos e benefícios", "Energia", "10 min", R.drawable.ic_launcher_foreground, imageUrl = "https://upload.wikimedia.org/wikipedia/commons/c/c4/Solar_panels.jpg", date = "07 Jan 2025", url = "https://pt.wikipedia.org/wiki/Energia_solar_fotovoltaica"),
        LibraryItem(14, "Horta urbana em pequenos espaços", "Cultive temperos e hortaliças", "Horta", "14 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.gov.br/agro/pt-br/assuntos/sustentabilidade/imagens/horta.jpg", date = "06 Jan 2025", url = "https://www.gov.br/agro/pt-br/assuntos/sustentabilidade/horta-em-casa"),
        LibraryItem(15, "Upcycling: transformando resíduos", "Ideias criativas para reuso", "Faça Você Mesmo", "18 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.ecycle.com.br/wp-content/uploads/2021/05/upcycling.jpg", date = "05 Jan 2025", url = "https://www.ecycle.com.br/upcycling/"),
        LibraryItem(16, "Separação correta de resíduos", "Classificação e coleta seletiva", "Reciclagem", "9 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.ecycle.com.br/wp-content/uploads/2021/05/coleta-seletiva.jpg", date = "04 Jan 2025", url = "https://www.ecycle.com.br/coleta-seletiva/"),
        LibraryItem(17, "Logística reversa na prática", "Como devolver produtos ao ciclo", "Logística Reversa", "10 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.ecycle.com.br/wp-content/uploads/2021/05/logistica-reversa.jpg", date = "03 Jan 2025", url = "https://www.ecycle.com.br/logistica-reversa/"),
        LibraryItem(18, "Coleta de óleo de cozinha", "Armazenamento e destinação correta", "Óleo de Cozinha", "7 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.ecycle.com.br/wp-content/uploads/2021/05/oleo-de-cozinha.jpg", date = "02 Jan 2025", url = "https://www.ecycle.com.br/oleo-de-cozinha/"),
        LibraryItem(19, "Ecopontos municipais", "Onde levar volumosos e recicláveis", "Ecopontos", "6 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.prefeitura.sp.gov.br/cidade/secretarias/subprefeituras/ecopontos/galeria/ecoponto.jpg", date = "01 Jan 2025", url = "https://www.prefeitura.sp.gov.br/cidade/secretarias/subprefeituras/ecopontos/"),
        LibraryItem(20, "Reuso de água cinza", "Aproveitamento seguro em casa", "Água Cinza", "9 min", R.drawable.ic_launcher_foreground, imageUrl = "https://www.ecycle.com.br/wp-content/uploads/2020/07/agua-cinza.jpg", date = "31 Dez 2024", url = "https://www.ecycle.com.br/agua-cinza/")
    )

    LaunchedEffect(Unit) {
        try {
            val mapped = fetchLibraryItems("tutoria", "Tutoria", altCollection = "Tutoria")
            itemsState = if (mapped.isNotEmpty()) mapped else fallback
            loading = false
        } catch (ex: Exception) {
            itemsState = fallback
            android.util.Log.e("LibraryScreen", "Falha ao carregar tutoriais, usando fallback", ex)
            error = null
            loading = false
        }
    }

    when {
        loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Palette.primary)
            }
        }
        error != null -> {
            LibraryItemList(items = itemsState, onItemClick = onItemClick, showPostedTime = false)
        }
        else -> {
            LibraryItemList(items = itemsState, onItemClick = onItemClick, showPostedTime = false)
        }
}
}

@Composable
private fun LibraryItemList(
    items: List<LibraryItem>,
    onItemClick: (String) -> Unit,
    showPostedTime: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) +
                slideInVertically(animationSpec = tween(600)) { it / 3 }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                LibraryItemCard(item = item, onItemClick = onItemClick, showPostedTime = showPostedTime)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LibraryItemCard(
    item: LibraryItem,
    onItemClick: (String) -> Unit,
    showPostedTime: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val elevation by animateDpAsState(
        targetValue = 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onItemClick(item.url ?: "") },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = Palette.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Categoria e tempo de leitura
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.foundation.Image(
                        painter = coil.compose.rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    item.imageUrl ?: (item.url?.let { u ->
                                        try {
                                            val host = java.net.URI(u).host
                                            if (!host.isNullOrBlank()) {
                                                "https://www.google.com/s2/favicons?sz=64&domain_url=https://$host"
                                            } else {
                                                "https://www.google.com/s2/favicons?sz=64&domain_url=$u"
                                            }
                                        } catch (_: Exception) {
                                            "https://www.google.com/s2/favicons?sz=64&domain_url=$u"
                                        }
                                    } ?: "")
                                )
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.icone_artigo),
                            error = painterResource(R.drawable.icone_artigo)
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Palette.secondary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 12.sp,
                            color = Palette.secondary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                if (showPostedTime) {
                    val postedTime = formatPostedTimeOrNull(item.date)
                    if (!postedTime.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Palette.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = postedTime,
                                fontSize = 12.sp,
                                color = Palette.textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

                // Título
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Palette.text,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Descrição
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Palette.textMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Data
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDateDisplay(item.date),
                        fontSize = 12.sp,
                        color = Palette.textMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ler mais",
                        tint = Palette.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onItemClick(item.url ?: "") }
                    )
                }
            }
        }
    }

private fun formatPostedTimeOrNull(dateString: String): String? {
    val patternsWithTime = listOf(
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd HH:mm:ss",
        "dd MMM yyyy HH:mm",
        "dd/MM/yyyy HH:mm"
    )
    val patternsDateOnly = listOf(
        "dd MMM yyyy",
        "dd/MM/yyyy"
    )

    for (p in patternsWithTime) {
        try {
            val sdf = java.text.SimpleDateFormat(p, java.util.Locale.getDefault())
            sdf.timeZone = java.util.TimeZone.getDefault()
            val d = sdf.parse(dateString)
            if (d != null) {
                val out = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                return out.format(d)
            }
        } catch (_: Exception) { }
    }
    for (p in patternsDateOnly) {
        try {
            val sdf = java.text.SimpleDateFormat(p, java.util.Locale.getDefault())
            val d = sdf.parse(dateString)
            if (d != null) return null
        } catch (_: Exception) { }
    }
    return null
}

private fun formatDateDisplay(dateString: String): String {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd HH:mm:ss",
        "dd MMM yyyy HH:mm",
        "dd/MM/yyyy HH:mm",
        "dd MMM yyyy",
        "dd/MM/yyyy"
    )
    for (p in patterns) {
        try {
            val sdf = java.text.SimpleDateFormat(p, java.util.Locale("pt", "BR"))
            val d = sdf.parse(dateString)
            if (d != null) {
                val out = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("pt", "BR"))
                return out.format(d)
            }
        } catch (_: Exception) { }
    }
    return dateString
}

// Modelo de dados
data class LibraryItem(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val readTime: String,
    val imageRes: Int,
    val imageUrl: String? = null,
    val date: String,
    val url: String?
)
private suspend fun fetchLibraryItems(collection: String, defaultCategory: String, altCollection: String? = null): List<LibraryItem> {
    suspend fun load(col: String): List<LibraryItem> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection(col)
            .get()
            .await()
        return snapshot.documents.mapIndexed { idx, doc ->
            val url = doc.getString("url")
            val title = doc.getString("title") ?: (doc.getString("titulo") ?: "")
            val description = doc.getString("description") ?: (doc.getString("descricao") ?: "")
            val category = doc.getString("category") ?: (doc.getString("categoria") ?: defaultCategory)
            val readTime = doc.getString("readTime") ?: "8 min"
            val imageUrl = doc.getString("imageUrl") ?: (url?.let { u -> "https://www.google.com/s2/favicons?sz=64&domain_url=$u" })
            val date = doc.getString("date") ?: ""
            LibraryItem(
                id = (doc.id.hashCode() + idx),
                title = title,
                description = description,
                category = category,
                readTime = readTime,
                imageRes = R.drawable.ic_launcher_foreground,
                imageUrl = imageUrl,
                date = date,
                url = url
            )
        }
    }

    return try {
        val primary = load(collection)
        if (primary.isNotEmpty()) primary else if (altCollection != null) load(altCollection) else emptyList()
    } catch (_: Exception) {
        if (altCollection != null) {
            try { load(altCollection) } catch (_: Exception) { emptyList() }
        } else emptyList()
    }
}