package com.example.ecolab.feature.library

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import com.example.ecolab.R
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

// DTO para mapeamento do Firebase
data class GuideItemFirebase(
    var title: String = "",
    var author: String = "",
    var description: String = "",
    var url: String = "",
    var category: String = ""
) {
    fun toGuideItem(id: String): GuideItem {
        return GuideItem(
            id = id,
            title = this.title,
            author = this.author,
            description = this.description,
            url = this.url,
            category = this.category
        )
    }
}

// Data Class GuideItem
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
    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState

    // 1. Instância do Cloud Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val guidesRef = firestore.collection("Artigo")

    init {
        // Inicia o carregamento
        loadGuides()
    }

    private fun loadGuides() {
        // 1. Inicia o carregamento com os guias de FALLBACK como base
        val fallbackGuides = getFallbackGuides()

        // Atualiza o estado inicial: mostra o fallback imediatamente e indica que está carregando mais
        _uiState.value = _uiState.value.copy(
            guides = fallbackGuides,
            isLoading = true,
            errorMessage = null
        )

        // 2. Tenta carregar do Firestore
        guidesRef.get()
            .addOnSuccessListener { snapshot ->
                Log.d("FirestoreLoad", "Documentos recebidos na coleção 'Artigo': ${snapshot.size()}")

                val loadedGuidesFromFirebase = mutableListOf<GuideItem>()

                for (document in snapshot.documents) {
                    val guideKey = document.id

                    val guideFirebase = try {
                        document.toObject(GuideItemFirebase::class.java)
                    } catch (e: Exception) {
                        Log.e("FirestoreLoad", "ERRO DE MAPEEAMENTO no documento ${document.id}: ${e.message}", e)
                        null
                    }

                    guideFirebase?.let {
                        Log.i("FirestoreLoad", "Guia mapeado com sucesso: ${it.title}")
                        loadedGuidesFromFirebase.add(it.toGuideItem(guideKey))
                    }
                }

                // 3. Combina as listas: Fallback primeiro + Firestore depois
                // Filtramos para evitar duplicatas, caso o Firestore contenha algum item igual ao fallback.
                val uniqueFirebaseGuides = loadedGuidesFromFirebase.filter { firebaseGuide ->
                    // A lista de fallback já contém itens únicos (pelo título e URL)
                    !fallbackGuides.any { it.title == firebaseGuide.title && it.url == firebaseGuide.url }
                }

                val finalGuides = fallbackGuides + uniqueFirebaseGuides

                // 4. Atualiza o estado com a lista combinada e desliga o carregamento
                _uiState.value = _uiState.value.copy(
                    guides = finalGuides,
                    isLoading = false,
                    errorMessage = if (uniqueFirebaseGuides.isEmpty() && loadedGuidesFromFirebase.isNotEmpty()) {
                        "Todos os artigos do Firestore já existem na lista base."
                    } else if (loadedGuidesFromFirebase.isEmpty()) {
                        "O Firestore não retornou novos artigos, mas o fallback está ativo."
                    } else {
                        null
                    }
                )
            }
            .addOnFailureListener { exception ->
                // Se o Firestore falhar (por ex: conexão perdida após o início),
                // mantemos o fallback e apenas desligamos o isLoading e registramos o erro.
                Log.e("FirestoreLoad", "ERRO ao buscar no Firestore. Mantendo Fallback:", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível carregar artigos extras do servidor: ${exception.message}"
                )
            }
    }

    private fun getFallbackGuides(): List<GuideItem> {
        return listOf(
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
        )
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    // Função que aplica a cor e ícone baseados na categoria (para consistência do UI)
    fun getGuideVisuals(guide: GuideItem): Pair<Color, Int> {
        return when (guide.category.lowercase()) {
            "papel" -> Pair(Color(0xFF1E88E5), R.drawable.icone_papel)
            "plastico" -> Pair(Color(0xFFE53935), R.drawable.icone_plastico)
            "vidro" -> Pair(Color(0xFF43A047), R.drawable.icone_vidro)
            "metal" -> Pair(Color(0xFFFDD835), R.drawable.icone_metal)
            "madeira" -> Pair(Color(0xFF424242), R.drawable.icone_madeira)
            "perigoso" -> Pair(Color(0xFFFF5722), R.drawable.icone_reciclavel)
            "hospitalar" -> Pair(Color(0xFFBDBDBD), R.drawable.icone_medicamento)
            "radioativo" -> Pair(Color(0xFF673AB7), R.drawable.icone_radioativo)
            "organico" -> Pair(Color(0xFF964B00), R.drawable.icone_organico)
            "naoreciclavel" -> Pair(Color(0xFF575757), R.drawable.icone_naoreciclavel)
            else -> Pair(Color(0xFF00BCD4), R.drawable.icone_artigo) // artigo
        }
    }
}