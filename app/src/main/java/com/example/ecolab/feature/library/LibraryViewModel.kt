package com.example.ecolab.feature.library

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.ecolab.R


data class GuideItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val description: String,
    val url: String,
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

    private fun loadGuides() {

        viewModelScope.launch {
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
    fun getGuideVisuals(guide: GuideItem): Pair<Color, Int> {
        return when (guide.category.lowercase()) {
            "papel" -> Pair(Color(0xFF1E88E5), R.drawable.ic_recycling)
            "plastico" -> Pair(Color(0xFFE53935), R.drawable.ic_recycling)
            "vidro" -> Pair(Color(0xFF43A047), R.drawable.ic_recycling)
            "metal" -> Pair(Color(0xFFFDD835), R.drawable.ic_recycling)
            "madeira" -> Pair(Color(0xFF424242), R.drawable.ic_recycling)
            "perigoso" -> Pair(Color(0xFFFF5722), R.drawable.ic_notifications)
            "hospitalar" -> Pair(Color(0xFFBDBDBD), R.drawable.ic_help)
            "radioativo" -> Pair(Color(0xFF673AB7), R.drawable.ic_time)
            "organico" -> Pair(Color(0xFF964B00), R.drawable.ic_ecolab_logo)
            "naoreciclavel" -> Pair(Color(0xFF575757), R.drawable.ic_delete)
            else -> Pair(Color(0xFF00BCD4), R.drawable.ic_ecolab_logo)
        }
    }
}