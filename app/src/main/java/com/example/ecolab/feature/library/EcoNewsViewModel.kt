package com.example.ecolab.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.news.EcoNewsCollector
import com.example.ecolab.data.news.EnvironmentalNews
import com.example.ecolab.ui.screens.LibraryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel simples para gerenciar as notícias ambientais na biblioteca
 * Carrega automaticamente quando o usuário entra na aba de notícias
 */
class EcoNewsViewModel : ViewModel() {
    
    private val _newsState = MutableStateFlow<NewsState>(NewsState.Loading)
    val newsState: StateFlow<NewsState> = _newsState.asStateFlow()
    
    private val _newsItems = MutableStateFlow<List<LibraryItem>>(emptyList())
    val newsItems: StateFlow<List<LibraryItem>> = _newsItems.asStateFlow()
    
    /**
     * Carrega as notícias ambientais automaticamente
     * Chamado quando o usuário entra na aba de notícias
     */
    fun loadEnvironmentalNews() {
        viewModelScope.launch {
            _newsState.value = NewsState.Loading
            
            try {
                val environmentalNews = EcoNewsCollector.collectEnvironmentalNews()
                val libraryItems = environmentalNews.map { it.toLibraryItem() }
                if (libraryItems.isNotEmpty()) {
                    _newsItems.value = libraryItems
                    _newsState.value = NewsState.Success(libraryItems)
                } else {
                    _newsItems.value = emptyList()
                    _newsState.value = NewsState.Empty
                }
            } catch (e: Exception) {
                _newsItems.value = emptyList()
                _newsState.value = NewsState.Error("Falha ao carregar notícias")
            }
        }
    }
    
    /**
     * Atualiza as notícias (pull to refresh)
     */
    fun refreshNews() {
        loadEnvironmentalNews()
    }
    
    /**
     * Notícias mockadas como fallback
     */
    private fun getMockEnvironmentalNews(): List<LibraryItem> = emptyList()
}

/**
 * Estados possíveis das notícias
 */
sealed class NewsState {
    object Loading : NewsState()
    data class Success(val news: List<LibraryItem>) : NewsState()
    data class Error(val message: String) : NewsState()
    object Empty : NewsState()
}