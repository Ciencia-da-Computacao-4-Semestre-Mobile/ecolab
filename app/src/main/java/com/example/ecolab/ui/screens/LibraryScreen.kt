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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolab.R
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onGuideClick: (url: String) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Notícias", "Artigos", "Tutoriais")

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            // Tabs animadas estilo StoreScreen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(300.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Palette.surface.copy(alpha = 0.8f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = pagerState.currentPage == index

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
                                        imageVector = when (index) {
                                            0 -> Icons.Default.Article
                                            1 -> Icons.AutoMirrored.Filled.MenuBook
                                            else -> Icons.Default.School
                                        },
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
                    0 -> NewsSection(onGuideClick)
                    1 -> ArticlesSection(onGuideClick)
                    2 -> TutorialsSection(onGuideClick)
                }
            }
        }
    }
}

@Composable
private fun NewsSection(onItemClick: (String) -> Unit) {
    val newsItems = listOf(
        LibraryItem(
            id = 1,
            title = "Novo Recorde de Reciclagem em 2024",
            description = "Brasil atinge 98% de reciclagem de latinhas de alumínio",
            category = "Sustentabilidade",
            readTime = "3 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "15 Dez 2024"
        ),
        LibraryItem(
            id = 2,
            title = "Energia Solar em Alta",
            description = "Crescimento de 45% na instalação de painéis solares residenciais",
            category = "Energia",
            readTime = "5 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "14 Dez 2024"
        ),
        LibraryItem(
            id = 3,
            title = "Oceanos em Perigo",
            description = "Como a poluição plástica afeta a vida marinha",
            category = "Meio Ambiente",
            readTime = "7 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "13 Dez 2024"
        )
    )

    LibraryItemList(items = newsItems, onItemClick = onItemClick)
}

@Composable
private fun ArticlesSection(onItemClick: (String) -> Unit) {
    val articleItems = listOf(
        LibraryItem(
            id = 4,
            title = "Guia Completo: Reciclagem de Plásticos",
            description = "Aprenda a identificar e separar corretamente os diferentes tipos de plástico",
            category = "Reciclagem",
            readTime = "10 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "12 Dez 2024"
        ),
        LibraryItem(
            id = 5,
            title = "Compostagem Doméstica",
            description = "Transforme resíduos orgânicos em adubo natural para seu jardim",
            category = "Compostagem",
            readTime = "8 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "11 Dez 2024"
        ),
        LibraryItem(
            id = 6,
            title = "Economia Circular",
            description = "Entenda como o modelo de economia circular pode salvar o planeta",
            category = "Sustentabilidade",
            readTime = "12 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "10 Dez 2024"
        )
    )

    LibraryItemList(items = articleItems, onItemClick = onItemClick)
}

@Composable
private fun TutorialsSection(onItemClick: (String) -> Unit) {
    val tutorialItems = listOf(
        LibraryItem(
            id = 7,
            title = "Como Fazer uma Horta em Casa",
            description = "Passo a passo para criar sua própria horta orgânica",
            category = "Horta",
            readTime = "15 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "09 Dez 2024"
        ),
        LibraryItem(
            id = 8,
            title = "DIY: Separador de Lixos",
            description = "Construa seu próprio sistema de separação de resíduos",
            category = "Faça Você Mesmo",
            readTime = "20 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "08 Dez 2024"
        ),
        LibraryItem(
            id = 9,
            title = "Economizar Água no Dia a Dia",
            description = "Dicas práticas para reduzir o consumo de água em casa",
            category = "Economia",
            readTime = "6 min",
            imageRes = R.drawable.ic_launcher_foreground,
            date = "07 Dez 2024"
        )
    )

    LibraryItemList(items = tutorialItems, onItemClick = onItemClick)
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
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
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
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onItemClick(item.id.toString())
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
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
                        modifier = Modifier.size(20.dp)
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
    val date: String
)