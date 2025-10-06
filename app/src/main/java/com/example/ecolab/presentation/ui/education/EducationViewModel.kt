package com.example.ecolab.presentation.ui.education

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EducationItem(val title: String, val summary: String)

data class EducationUiState(
    val items: List<EducationItem> = listOf(
        EducationItem("Dicas de reciclagem", "Separe plástico, papel, vidro e metal"),
        EducationItem("Economia de água", "Feche a torneira ao escovar os dentes"),
        EducationItem("Energia limpa", "Prefira lâmpadas LED e desligue aparelhos"),
    )
)

@HiltViewModel
class EducationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EducationUiState())
    val uiState: StateFlow<EducationUiState> = _uiState.asStateFlow()
}