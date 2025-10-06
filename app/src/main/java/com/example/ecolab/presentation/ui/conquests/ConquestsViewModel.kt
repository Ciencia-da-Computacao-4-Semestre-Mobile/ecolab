package com.example.ecolab.presentation.ui.conquests

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Conquest(val title: String, val achieved: Boolean)

data class ConquestsUiState(
    val items: List<Conquest> = listOf(
        Conquest("Reciclou 10 itens", true),
        Conquest("Economizou água por 7 dias", true),
        Conquest("Plantou uma árvore", false),
        Conquest("Participou de mutirão", false)
    )
)

@HiltViewModel
class ConquestsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ConquestsUiState())
    val uiState: StateFlow<ConquestsUiState> = _uiState.asStateFlow()
}