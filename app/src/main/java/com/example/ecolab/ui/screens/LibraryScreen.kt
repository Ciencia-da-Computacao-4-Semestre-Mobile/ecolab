package com.example.ecolab.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecolab.feature.library.GuideItem
import com.example.ecolab.feature.library.LibraryViewModel
// Remova o import desnecessário de Palette se não estiver a usar mais
// import com.example.ecolab.ui.theme.Palette


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    // Recebe a ação de navegação para a URL (que abrirá o WebView)
    onGuideClick: (url: String) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    // Coleta o estado do ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Filtra a lista com base no termo de pesquisa
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
                    text = "Bem Vindo a Biblioteca ",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Campo de pesquisa
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) }, // Atualiza o estado da busca no ViewModel
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pesquisar...") },
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
                        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        items(filteredGuides, key = { it.id }) { guide ->
                            // Passa o guide, a função para obter cor/ícone e a ação de clique.
                            val (color, icon) = viewModel.getGuideVisuals(guide)
                            GuideCard(
                                guide = guide,
                                cardColor = color,
                                cardIcon = icon,
                                onClick = { onGuideClick(guide.url) } // Passa a URL para a função de navegação
                            )
                        }
                    }
                }
            }
        },
    )
}

// 3. Componente GuideCard Atualizado
@Composable
fun GuideCard(
    guide: GuideItem,
    cardColor: Color,
    cardIcon: ImageVector,
    onClick: () -> Unit // Ação de clique do botão
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        onClick = onClick // Você pode querer que o cartão inteiro seja clicável
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Seção de Ícone e Texto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Ícone
                Icon(
                    imageVector = cardIcon, // Ícone dinâmico
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Título
                Text(
                    text = guide.title, // Título dinâmico
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }

            // Botão "Ver Conteúdo"
            Button(
                onClick = onClick, // Usa a função passada, que abrirá o WebView
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Ver Conteúdo", fontSize = 12.sp)
            }
        }
    }
}