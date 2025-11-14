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
            LibraryItemList(items = state.news, onItemClick = onItemClick)
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

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("Artigo")
            .get()
            .addOnSuccessListener { result ->
                val mapped = result.documents.mapIndexed { idx, doc ->
                    LibraryItem(
                        id = (doc.id.hashCode() + idx),
                        title = doc.getString("title") ?: (doc.getString("titulo") ?: ""),
                        description = doc.getString("description") ?: (doc.getString("descricao") ?: ""),
                        category = doc.getString("category") ?: (doc.getString("categoria") ?: "Artigo"),
                        readTime = doc.getString("readTime") ?: "8 min",
                        imageRes = R.drawable.ic_launcher_foreground,
                        date = doc.getString("date") ?: "",
                        url = doc.getString("url")
                    )
                }
                itemsState = mapped
                loading = false
            }
            .addOnFailureListener { ex ->
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
                        FirebaseFirestore.getInstance().collection("Artigo").get()
                            .addOnSuccessListener { result ->
                                val mapped = result.documents.mapIndexed { idx, doc ->
                                    LibraryItem(
                                        id = (doc.id.hashCode() + idx),
                                        title = doc.getString("title") ?: (doc.getString("titulo") ?: ""),
                                        description = doc.getString("description") ?: (doc.getString("descricao") ?: ""),
                                        category = doc.getString("category") ?: (doc.getString("categoria") ?: "Artigo"),
                                        readTime = doc.getString("readTime") ?: "8 min",
                                        imageRes = R.drawable.ic_launcher_foreground,
                                        date = doc.getString("date") ?: "",
                                        url = doc.getString("url")
                                    )
                                }
                                itemsState = mapped
                                loading = false
                            }
                            .addOnFailureListener { e ->
                                error = e.message
                                loading = false
                            }
                    }) {
                        Text("Tentar novamente")
                    }
                }
            }
        }
        else -> {
            LibraryItemList(items = itemsState, onItemClick = onItemClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TutorialsSection(onItemClick: (String) -> Unit) {
    val tutorialItems = listOf(
        LibraryItem(10, "Guia prático de reciclagem em casa", "Como separar, lavar e destinar corretamente", "Reciclagem", "12 min", R.drawable.ic_launcher_foreground, "10 Jan 2025", "https://blog.eureciclo.com.br/como-reciclar-em-casa/"),
        LibraryItem(11, "Montando composteira doméstica", "Passo a passo de compostagem caseira", "Compostagem", "15 min", R.drawable.ic_launcher_foreground, "09 Jan 2025", "https://www.gov.br/ibama/pt-br/assuntos/educacao-ambiental/compostagem"),
        LibraryItem(12, "Economia de água: 20 dicas úteis", "Reduza consumo sem perder conforto", "Água", "8 min", R.drawable.ic_launcher_foreground, "08 Jan 2025", "https://www.sabesp.com.br/conteudo/consumo-consciente"),
        LibraryItem(13, "Energia solar residencial", "Introdução, custos e benefícios", "Energia", "10 min", R.drawable.ic_launcher_foreground, "07 Jan 2025", "https://pt.wikipedia.org/wiki/Energia_solar_fotovoltaica"),
        LibraryItem(14, "Horta urbana em pequenos espaços", "Cultive temperos e hortaliças", "Horta", "14 min", R.drawable.ic_launcher_foreground, "06 Jan 2025", "https://www.gov.br/agricultura/pt-br/assuntos/inovacao/como-fazer-horta-em-casa"),
        LibraryItem(15, "Upcycling: transformando resíduos", "Ideias criativas para reuso", "Faça Você Mesmo", "18 min", R.drawable.ic_launcher_foreground, "05 Jan 2025", "https://www.ecycle.com.br/upcycling/"),
        LibraryItem(16, "Separação correta de resíduos", "Classificação e coleta seletiva", "Reciclagem", "9 min", R.drawable.ic_launcher_foreground, "04 Jan 2025", "https://www.naturallimp.com.br/blog/quais-sao-os-materiais-reciclaveis-e-nao-reciclaveis"),
        LibraryItem(17, "Logística reversa na prática", "Como devolver produtos ao ciclo", "Logística Reversa", "10 min", R.drawable.ic_launcher_foreground, "03 Jan 2025", "https://www.ecycle.com.br/logistica-reversa/"),
        LibraryItem(18, "Coleta de óleo de cozinha", "Armazenamento e destinação correta", "Óleo de Cozinha", "7 min", R.drawable.ic_launcher_foreground, "02 Jan 2025", "https://www.ecycle.com.br/oleo-de-cozinha/"),
        LibraryItem(19, "Ecopontos municipais", "Onde levar volumosos e recicláveis", "Ecopontos", "6 min", R.drawable.ic_launcher_foreground, "01 Jan 2025", "https://www.prefeitura.sp.gov.br/cidade/secretarias/subprefeituras/ecopontos/"),
        LibraryItem(20, "Reuso de água cinza", "Aproveitamento seguro em casa", "Água Cinza", "9 min", R.drawable.ic_launcher_foreground, "31 Dez 2024", "https://www.ecycle.com.br/agua-cinza/")
    )

    var selected by remember { mutableStateOf<LibraryItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

    LibraryItemList(items = tutorialItems) { url ->
        selected = tutorialItems.find { it.url == url }
    }

    selected?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { selected = null },
            sheetState = sheetState,
            containerColor = Palette.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Palette.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.title,
                            tint = Palette.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Palette.text)
                        Text(item.category, style = MaterialTheme.typography.bodySmall, color = Palette.secondary)
                    }
                }

                Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Palette.text)

                val steps = when (item.category.lowercase()) {
                    "reciclagem" -> listOf(
                        Pair(Icons.Default.CheckCircle, "Separe recicláveis por material"),
                        Pair(Icons.Default.Build, "Lave e seque embalagens"),
                        Pair(Icons.Default.Eco, "Destine ao ecoponto/coop")
                    )
                    "compostagem" -> listOf(
                        Pair(Icons.Default.Eco, "Separe orgânicos"),
                        Pair(Icons.Default.Build, "Monte a composteira"),
                        Pair(Icons.Default.CheckCircle, "Adicione matéria seca e revolva")
                    )
                    "água" -> listOf(
                        Pair(Icons.Default.CheckCircle, "Instale arejadores e conserte vazamentos"),
                        Pair(Icons.Default.Build, "Reaproveite água de lavagem"),
                        Pair(Icons.Default.Eco, "Use coleta de água da chuva")
                    )
                    "logística reversa" -> listOf(
                        Pair(Icons.Default.CheckCircle, "Identifique produtos com responsabilidade"),
                        Pair(Icons.Default.Build, "Procure pontos de entrega"),
                        Pair(Icons.Default.Eco, "Acompanhe destino homologado")
                    )
                    "óleo de cozinha" -> listOf(
                        Pair(Icons.Default.CheckCircle, "Armazene óleo usado em garrafa"),
                        Pair(Icons.Default.Build, "Não despeje no ralo"),
                        Pair(Icons.Default.Eco, "Leve ao ponto de coleta")
                    )
                    "ecopontos" -> listOf(
                        Pair(Icons.Default.CheckCircle, "Separe volumosos e entulho"),
                        Pair(Icons.Default.Build, "Verifique horários e regras"),
                        Pair(Icons.Default.Eco, "Leve ao ecoponto mais próximo")
                    )
                    "água cinza" -> listOf(
                        Pair(Icons.Default.CheckCircle, "Separe saída da máquina/chuveiro"),
                        Pair(Icons.Default.Build, "Filtre antes do reuso"),
                        Pair(Icons.Default.Eco, "Use em irrigação não comestível")
                    )
                    else -> listOf(
                        Pair(Icons.Default.CheckCircle, "Siga o passo a passo do tutorial"),
                        Pair(Icons.Default.Eco, "Adapte ao seu contexto")
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    steps.forEachIndexed { index, (icon, text) ->
                        AnimatedVisibility(visible = true, enter = fadeIn(animationSpec = tween(300 + index * 100))) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(icon, contentDescription = null, tint = Palette.primary)
                                Text(text, color = Palette.text)
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { onItemClick(item.url ?: "") }, colors = ButtonDefaults.buttonColors(containerColor = Palette.primary)) {
                        Text("Abrir Tutorial")
                    }
                    OutlinedButton(onClick = { selected = null }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Palette.primary)) {
                        Text("Fechar")
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryItemList(
    items: List<LibraryItem>,
    onItemClick: (String) -> Unit
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
                LibraryItemCard(item = item, onItemClick = onItemClick)
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
    onItemClick: (String) -> Unit
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagem do item
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Palette.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    tint = Palette.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Categoria e tempo de leitura
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            text = item.readTime,
                            fontSize = 12.sp,
                            color = Palette.textMuted,
                            fontWeight = FontWeight.Medium
                        )
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
                        text = item.date,
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
}

// Modelo de dados
data class LibraryItem(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val readTime: String,
    val imageRes: Int,
    val date: String,
    val url: String?
)