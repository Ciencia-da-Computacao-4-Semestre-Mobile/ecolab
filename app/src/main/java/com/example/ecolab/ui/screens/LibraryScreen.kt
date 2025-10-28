package com.example.ecolab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecolab.feature.library.GuideItem
import com.example.ecolab.feature.library.LibraryViewModel
import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onGuideClick: (url: String) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredGuides = uiState.guides.filter {
        it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                it.description.contains(uiState.searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Bem Vindo a Biblioteca",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(), // 1. Ocupa toda a largura
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Campo de pesquisa
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pesquisar...") },
                    // Usamos Icon aqui, pois o ícone de busca é um ImageVector padrão do Compose
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Pesquisar") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFE0E0E0),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
            }
        },
        content = { paddingValues ->
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                filteredGuides.isEmpty() -> {
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text("Nenhum guia encontrado para \"${uiState.searchQuery}\".")
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(horizontal = 12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(12.dp)) }
                        items(filteredGuides, key = { it.id }) { guide ->
                            val (color, imageResId) = viewModel.getGuideVisuals(guide)
                            GuideCard(
                                guide = guide,
                                cardColor = color,
                                cardImageResId = imageResId,
                                onClick = { onGuideClick(guide.url) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }
                }
            }
        },
    )
}


@Composable
fun GuideCard(
    guide: GuideItem,
    cardColor: Color,
    cardImageResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Seção de Imagem e Texto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {

                Image(
                    painter = painterResource(id = cardImageResId), // Carrega a imagem usando o ID (Int)
                    contentDescription = null,
                    // Aplicamos a cor de destaque (Color) na imagem.
                    // Remova ColorFilter.tint se a sua imagem já for colorida.
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(65.dp)
                        .background(Color.Black.copy(alpha = 0.0f))
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                // Título
                Text(
                    text = guide.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }

            // Botão "Ver Conteúdo"
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 9.dp, vertical = 8.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Ver Conteúdo", fontSize = 12.sp)
            }
        }
    }
}