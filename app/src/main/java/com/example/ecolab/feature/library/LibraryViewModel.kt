package com.example.ecolab.feature.library // Adapte o nome do pacote

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// 1. Novo Modelo de Dados para os Guias
data class GuideItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val description: String,
    val url: String, // A URL que será carregada no WebView
    val category: String // Ajuda a determinar cor/ícone
)

// Estrutura de Estado para a UI
data class LibraryUiState(
    val guides: List<GuideItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// 2. ViewModel
class LibraryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        loadGuides()
    }

    // Simulação do carregamento de dados do banco de dados/servidor
    private fun loadGuides() {

        viewModelScope.launch {
            // Em uma aplicação real, você faria uma chamada de API ou acesso ao DB aqui.
            val loadedGuides = listOf(
                GuideItem(
                    title = "Guia de reciclagem de papel", author = "EcoTeam", description = "Tudo sobre como reciclar papel.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_papel", category = "papel"
                ),
                GuideItem(
                    title = "Guia de reciclagem de plástico", author = "GreenCorp", description = "Evite o descarte incorreto de plásticos.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_pl%C3%A1stico", category = "plastico"
                ),
                GuideItem(
                    title = "Guia de reciclagem de vidro", author = "Recicla Brasil", description = "Dicas e cuidados com o vidro.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_vidro", category = "vidro"
                ),
                GuideItem(
                    title = "Guia de reciclagem de metal", author = "EcoTeam", description = "Metal: um recurso valioso.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_metal", category = "metal"
                ),
                GuideItem(
                    title = "Guia de reciclagem de madeira", author = "WoodSave", description = "O ciclo de vida da madeira.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_madeira", category = "madeira"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo contaminado", author = "WoodSave", description = "O ciclo de vida da madeira.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_madeira", category = "perigoso"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo hospitalar", author = "WoodSave", description = "O ciclo de vida da madeira.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_madeira", category = "hospitalar"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo radioativo", author = "WoodSave", description = "O ciclo de vida da madeira.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_madeira", category = "radioativo"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo organico", author = "WoodSave", description = "O ciclo de vida da madeira.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_madeira", category = "organico"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo não reciclavel", author = "WoodSave", description = "O ciclo de vida da madeira.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_madeira", category = "naoreciclavel"
                ),
                GuideItem(
                    title = "Artigo de reciclagem de lixo não reciclavel", author = "WoodSave", description = "O ciclo de vida da madeira.",
                    url = "https://pt.wikipedia.org/wiki/Reciclagem_de_madeira", category = "artigo"
                ),
            )
            _uiState.value = _uiState.value.copy(guides = loadedGuides, isLoading = false)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    // Função que aplica a cor e ícone baseados na categoria (para consistência do UI)
    fun getGuideVisuals(guide: GuideItem): Pair<Color, ImageVector> {
        return when (guide.category.lowercase()) {
            "papel" -> Pair(Color(0xFF1E88E5), Icons.Default.Inventory2) // Azul
            "plastico" -> Pair(Color(0xFFE53935), Icons.Default.SportsBasketball) // Vermelho
            "vidro" -> Pair(Color(0xFF43A047), Icons.Default.WineBar) // Verde
            "metal" -> Pair(Color(0xFFFDD835), Icons.Default.Extension) // Amarelo
            "madeira" -> Pair(Color(0xFF424242), Icons.Default.Texture)// Preto/Cinza
            "perigoso" -> Pair(Color(0xFFFF5722), Icons.Default.Texture)
            "hospitalar" -> Pair(Color(0xFFBDBDBD), Icons.Default.Texture)
            "radioativo" -> Pair(Color(0xFF673AB7), Icons.Default.Texture)
            "organico" -> Pair(Color(0xFF964B00), Icons.Default.Texture)
            "naoreciclavel" -> Pair(Color(0xFF575757), Icons.Default.Texture)
            else -> Pair(Color(0xFF00BCD4), Icons.Default.MenuBook) // artigo
        }
    }
}