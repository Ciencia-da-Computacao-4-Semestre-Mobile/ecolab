package com.example.ecolab.feature.library

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Article(val id: Int, val title: String, val summary: String)
data class LibraryUiState(val articles: List<Article> = emptyList())

class LibraryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        LibraryUiState(
            articles = listOf(
                Article(1, "Como separar o lixo corretamente", "Um guia prático para o dia a dia de reciclagem."),
                Article(2, "O impacto do plástico nos oceanos", "Entenda a dimensão do problema e as ações."),
                Article(3, "Compostagem caseira: passo a passo", "Transforme seu lixo orgânico em adubo de compostagem.")
            )
        )
    )
    val uiState = _uiState.asStateFlow()
}
