package com.example.ecolab.feature.library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Data model as specified in the prompt
data class Article(
    val id: Long,
    val title: String,
    val summary: String,
    val content: String // Not used in summary view, but good for detail screen
)

data class LibraryUiState(
    val articles: List<Article> = emptyList()
)

@HiltViewModel
class LibraryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMockArticles()
    }

    private fun loadMockArticles() {
        _uiState.value = LibraryUiState(
            articles = listOf(
                Article(1, "Guia Completo da Reciclagem", "Aprenda a separar seu lixo corretamente e o impacto disso no planeta.", "..."),
                Article(2, "Compostagem Caseira", "Como transformar resíduos orgânicos em adubo rico para suas plantas.", "..."),
                Article(3, "Reduzindo o Desperdício de Água", "Dicas práticas para economizar água no dia a dia.", "...")
            )
        )
    }
}
