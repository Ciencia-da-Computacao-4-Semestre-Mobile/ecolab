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
                    title = "Guia de reciclagem de papel", author = "Eureciclo", description = "Tudo sobre como reciclar papel.",
                    url = "https://blog.eureciclo.com.br/reciclagem-de-papel/", category = "papel"
                ),
                GuideItem(
                    title = "Guia de reciclagem de plástico", author = "Trevoreciclagem", description = "Evite o descarte incorreto de plásticos.",
                    url = "https://trevoreciclagem.com.br/reciclagem-do-plastico/", category = "plastico"
                ),
                GuideItem(
                    title = "Guia de reciclagem de vidro", author = "circulavidro", description = "Dicas e cuidados com o vidro.",
                    url = "https://circulavidro.com/wp-content/uploads/2024/05/Guia-Tecnico-de-Reciclagem-do-Vidro.pdf", category = "vidro"
                ),
                GuideItem(
                    title = "Guia de reciclagem de metal", author = "mrsucatas", description = "Metal: um recurso valioso.",
                    url = "https://mrsucatas.com.br/sucata-mista-um-guia-completo-para-reciclagem/", category = "metal"
                ),
                GuideItem(
                    title = "Guia de reciclagem de madeira", author = "tomra", description = "O ciclo de vida da madeira.",
                    url = "https://www.tomra.com/pt-br/waste-metal-recycling/media-center/download/waste-wood-recycling", category = "madeira"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo contaminado", author = "ufsc", description = "lixo perigoso",
                    url = "https://gestaoderesiduos.ufsc.br/files/2016/02/Butantan_guia_pratico.pdf", category = "perigoso"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo hospitalar", author = "ecourbis", description = "lixo hospitalar",
                    url = "https://drive.prefeitura.sp.gov.br/cidade/secretarias/upload/servicos/Manual_de_Residuos%20(ECOURBIS).pdf", category = "hospitalar"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo radioativo", author = "govbr", description = "lixo radioativo",
                    url = "https://www.gov.br/pt-br/servicos/receber-tratar-acondicionar-e-armazenar-rejeitos-radiotativos", category = "radioativo"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo organico", author = "trevoreciclagem", description = "lixo organico",
                    url = "https://trevoreciclagem.com.br/residuos-organicos/", category = "organico"
                ),
                GuideItem(
                    title = "Guia de reciclagem de lixo não reciclavel", author = "naturallimp", description = "lixo não reciclavel",
                    url = "https://www.naturallimp.com.br/blog/quais-sao-os-materiais-reciclaveis-e-nao-reciclaveis", category = "naoreciclavel"
                ),
                GuideItem(
                    title = "Artigo de reciclagem de lixo não reciclavel", author = "unila", description = "artigo",
                    url = "https://portal.unila.edu.br/semana-unilera/lista-de-lixo-reciclavel-e-nao-reciclavel.pdf", category = "artigo"
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