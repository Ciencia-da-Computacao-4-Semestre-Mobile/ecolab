package com.example.ecolab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()
    
    private val _userPoints = MutableStateFlow(1250) // Pontos do usuário (exemplo)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()
    
    init {
        loadStoreItems()
    }
    
    private fun loadStoreItems() {
        val items = listOf(
            // Avatares Natureza
            StoreItem("avatar_nature_1", "Guardião da Floresta", "Avatar com tema de floresta tropical", 150, StoreCategory.AVATAR, Rarity.COMMON, "🌳"),
            StoreItem("avatar_nature_2", "Espírito do Verde", "Avatar com aura natural", 300, StoreCategory.AVATAR, Rarity.RARE, "🌿"),
            StoreItem("avatar_nature_3", "Avatar Ancião", "Avatar com sabedoria da natureza", 500, StoreCategory.AVATAR, Rarity.EPIC, "🍃"),
            
            // Avatares Tecnologia
            StoreItem("avatar_tech_1", "Eco-Tech", "Avatar com tema tecnológico sustentável", 200, StoreCategory.AVATAR, Rarity.COMMON, "⚡"),
            StoreItem("avatar_tech_2", "Ciborgue Verde", "Avatar meio humano, meio máquina", 400, StoreCategory.AVATAR, Rarity.RARE, "🔋"),
            StoreItem("avatar_tech_3", "IA Ambiental", "Avatar inteligência artificial ecológica", 750, StoreCategory.AVATAR, Rarity.LEGENDARY, "🤖"),
            
            // Avatares Animais
            StoreItem("avatar_animal_1", "Lobo da Reciclagem", "Avatar lobo com tema sustentável", 250, StoreCategory.AVATAR, Rarity.COMMON, "🐺"),
            StoreItem("avatar_animal_2", "Águia Verde", "Avatar águia com visão ecológica", 450, StoreCategory.AVATAR, Rarity.RARE, "🦅"),
            StoreItem("avatar_animal_3", "Dragão da Terra", "Avatar dragão guardião do planeta", 1000, StoreCategory.AVATAR, Rarity.LEGENDARY, "🐉"),
            
            // Selos/Badges
            StoreItem("badge_recycler", "Mestre Reciclador", "Selo para recicladores dedicados", 100, StoreCategory.BADGE, Rarity.COMMON, "♻️"),
            StoreItem("badge_water", "Guardião da Água", "Selo para protetores dos recursos hídricos", 150, StoreCategory.BADGE, Rarity.COMMON, "💧"),
            StoreItem("badge_energy", "Energizador Verde", "Selo para economizadores de energia", 150, StoreCategory.BADGE, Rarity.COMMON, "⚡"),
            StoreItem("badge_animal", "Amigo dos Animais", "Selo para defensores da fauna", 200, StoreCategory.BADGE, Rarity.RARE, "🐾"),
            StoreItem("badge_earth", "Guardião da Terra", "Selo supremo do meio ambiente", 500, StoreCategory.BADGE, Rarity.EPIC, "🌍"),
            
            // Temas
            StoreItem("theme_ocean", "Tema Oceano", "Tema visual inspirado no oceano", 300, StoreCategory.THEME, Rarity.RARE, "🌊"),
            StoreItem("theme_forest", "Tema Floresta", "Tema visual inspirado na floresta", 300, StoreCategory.THEME, Rarity.RARE, "🌲"),
            StoreItem("theme_sunset", "Tema Pôr do Sol", "Tema visual com cores do pôr do sol", 400, StoreCategory.THEME, Rarity.EPIC, "🌅"),
            
            // Efeitos
            StoreItem("effect_particles", "Partículas Douradas", "Efeitos visuais dourados", 250, StoreCategory.EFFECT, Rarity.RARE, "✨"),
            StoreItem("effect_leaves", "Folhas Dançantes", "Efeitos de folhas caindo", 350, StoreCategory.EFFECT, Rarity.EPIC, "🍂"),
            StoreItem("effect_stars", "Chuva de Estrelas", "Efeitos de estrelas brilhantes", 500, StoreCategory.EFFECT, Rarity.LEGENDARY, "⭐")
        )
        
        _uiState.value = StoreUiState(items = items)
    }
    
    fun purchaseItem(item: StoreItem) {
        viewModelScope.launch {
            if (_userPoints.value >= item.price && !item.isPurchased) {
                _userPoints.value -= item.price
                
                val updatedItems = _uiState.value.items.map { 
                    if (it.id == item.id) it.copy(isPurchased = true) else it 
                }
                _uiState.value = _uiState.value.copy(items = updatedItems)
            }
        }
    }
    
    fun equipItem(item: StoreItem) {
        viewModelScope.launch {
            if (item.isPurchased) {
                val updatedItems = _uiState.value.items.map { 
                    when {
                        it.id == item.id -> it.copy(isEquipped = true)
                        it.category == item.category -> it.copy(isEquipped = false)
                        else -> it
                    }
                }
                _uiState.value = _uiState.value.copy(items = updatedItems)
            }
        }
    }
    
    fun filterByCategory(category: StoreCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }
}

data class StoreUiState(
    val items: List<StoreItem> = emptyList(),
    val selectedCategory: StoreCategory? = null,
    val isLoading: Boolean = false
)