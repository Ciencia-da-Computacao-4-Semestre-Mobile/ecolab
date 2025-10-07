package com.example.ecolab.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecolab.feature.library.Article
import com.example.ecolab.feature.library.LibraryViewModel
import com.example.ecolab.ui.theme.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: LibraryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Artigos", "Documentos", "Guias")

    val filteredArticles = uiState.articles.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Busque por tema ou palavra-chave") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Busca") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Palette.divider,
                focusedBorderColor = Palette.primary
            )
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Palette.surface,
            contentColor = Palette.primary,
            divider = { HorizontalDivider(color = Palette.divider) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    selectedContentColor = Palette.primary,
                    unselectedContentColor = Palette.textMuted
                )
            }
        }

        if (selectedTabIndex == 0) { // Only show content for "Artigos"
            ChipGroup()
            if (filteredArticles.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum artigo encontrado.", color = Palette.textMuted)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredArticles) { article ->
                        ArticleCard(article)
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("${tabs[selectedTabIndex]} em breve.", color = Palette.textMuted)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipGroup() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(onClick = { /* TODO */ }, label = { Text("Reciclagem") })
        AssistChip(onClick = { /* TODO */ }, label = { Text("Compostagem") })
        AssistChip(onClick = { /* TODO */ }, label = { Text("Água") })
    }
}

@Composable
private fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(article.title, style = MaterialTheme.typography.titleMedium, color = Palette.text)
            Spacer(Modifier.height(4.dp))
            Text(article.summary, style = MaterialTheme.typography.bodyMedium, color = Palette.textMuted)
            Spacer(Modifier.height(8.dp))
            AssistChip(onClick = { /*TODO*/ }, label = { Text("Salvar") })
        }
    }
}
