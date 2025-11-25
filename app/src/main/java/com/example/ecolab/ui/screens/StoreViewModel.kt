package com.example.ecolab.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.data.repository.UserRepository
import com.example.ecolab.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _userPoints = MutableStateFlow(1250) // Pontos do usuário (exemplo)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    init {
        observeStoreState()
    }

    private fun observeStoreState() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let { user ->
                userRepository.getUserFlow(user.uid).collectLatest { userData ->
                    val items = getHardcodedItems()
                    val purchasedIds = userData?.purchasedItems?.toSet() ?: emptySet()
                    val equippedItems = userData?.equippedItemsMap ?: emptyMap()

                    val updatedItems = items.map {
                        it.copy(
                            isPurchased = purchasedIds.contains(it.id),
                            isEquipped = equippedItems[it.category.name] == it.id
                        )
                    }
                    _uiState.update { it.copy(items = updatedItems, isLoading = false) }
                }
            }
        }
    }

    fun purchaseItem(item: StoreItem) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null && _userPoints.value >= item.price && !item.isPurchased) {
                _userPoints.value -= item.price
                userRepository.addPurchasedItem(currentUser.uid, item.id)
            }
        }
    }

    fun equipItem(item: StoreItem) {
        viewModelScope.launch {
            if (item.isPurchased) {
                val currentUser = authRepository.getCurrentUser()
                currentUser?.let { user ->
                    userRepository.updateEquippedItem(user.uid, item.category, item.id)
                    Log.d("StoreViewModel", "Equipped item ${item.id} for user ${user.uid}")
                }
            }
        }
    }

    fun filterByCategory(category: StoreCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    private fun getHardcodedItems(): List<StoreItem> {
        return listOf(
             // ===== AVATARES NATUREZA =====
            // Comuns (50-200 pontos)
            StoreItem("avatar_nature_1", "Guardião da Floresta", "Avatar com tema de floresta tropical", 150, StoreCategory.AVATAR, Rarity.COMMON, "🌳", drawableRes = com.example.ecolab.R.drawable.avatar_02),
            StoreItem("avatar_nature_2", "Espírito do Verde", "Avatar com aura natural", 180, StoreCategory.AVATAR, Rarity.COMMON, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_03),
            StoreItem("avatar_nature_3", "Filho da Terra", "Avatar com conexão profunda com a natureza", 120, StoreCategory.AVATAR, Rarity.COMMON, "🌱", drawableRes = com.example.ecolab.R.drawable.avatar_04),
            StoreItem("avatar_nature_4", "Aprendiz Verde", "Avatar iniciante na jornada ecológica", 80, StoreCategory.AVATAR, Rarity.COMMON, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_05),
            StoreItem("avatar_nature_5", "Herói da Mata", "Avatar protetor das florestas", 200, StoreCategory.AVATAR, Rarity.COMMON, "🌲", drawableRes = com.example.ecolab.R.drawable.avatar_06),

            // Raros (250-400 pontos)
            StoreItem("avatar_nature_6", "Avatar Ancião", "Avatar com sabedoria da natureza", 350, StoreCategory.AVATAR, Rarity.RARE, "🍃", drawableRes = com.example.ecolab.R.drawable.avatar_07),
            StoreItem("avatar_nature_7", "Druida Moderno", "Avatar com poderes naturais antigos", 400, StoreCategory.AVATAR, Rarity.RARE, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_08),
            StoreItem("avatar_nature_8", "Guardião das Águas", "Avatar protetor dos oceanos", 300, StoreCategory.AVATAR, Rarity.RARE, "🌊", drawableRes = com.example.ecolab.R.drawable.avatar_09),
            StoreItem("avatar_nature_9", "Senhor dos Ventos", "Avatar com domínio dos ventos limpos", 380, StoreCategory.AVATAR, Rarity.RARE, "💨", drawableRes = com.example.ecolab.R.drawable.avatar_10),
            StoreItem("avatar_nature_10", "Curandeiro da Terra", "Avatar com poder de curar a natureza", 320, StoreCategory.AVATAR, Rarity.RARE, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_11),

            // Épicos (500-700 pontos)
            StoreItem("avatar_nature_11", "Avatar Elemental", "Avatar mestre dos quatro elementos", 650, StoreCategory.AVATAR, Rarity.EPIC, "🔥🌊🌪️🌍", drawableRes = com.example.ecolab.R.drawable.avatar_12),
            StoreItem("avatar_nature_12", "Guardião Sagrado", "Avatar protetor de todos os ecossistemas", 700, StoreCategory.AVATAR, Rarity.EPIC, "🌟", drawableRes = com.example.ecolab.R.drawable.avatar_13),
            StoreItem("avatar_nature_13", "Espírito da Floresta", "Avatar uma com a floresta", 550, StoreCategory.AVATAR, Rarity.EPIC, "🌳✨", drawableRes = com.example.ecolab.R.drawable.avatar_14),
            StoreItem("avatar_nature_14", "Avatar da Harmonia", "Avatar que equilibra natureza e tecnologia", 600, StoreCategory.AVATAR, Rarity.EPIC, "🌿⚡", drawableRes = com.example.ecolab.R.drawable.avatar_15),

            // Lendários (800-1200 pontos)
            StoreItem("avatar_nature_15", "Avatar Supremo", "Avatar supremo da ecologia", 1200, StoreCategory.AVATAR, Rarity.LEGENDARY, "👑🌿"),
            StoreItem("avatar_nature_16", "Deus da Natureza", "Avatar divino com poder absoluto sobre a natureza", 1000, StoreCategory.AVATAR, Rarity.LEGENDARY, "🌿⚡"),

            // ===== AVATARES TECNOLOGIA SUSTENTÁVEL =====
            // Comuns
            StoreItem("avatar_tech_1", "Eco-Tech", "Avatar com tema tecnológico sustentável", 200, StoreCategory.AVATAR, Rarity.COMMON, "⚡"),
            StoreItem("avatar_tech_2", "Recicla-Bot", "Avatar robô reciclador", 180, StoreCategory.AVATAR, Rarity.COMMON, "🤖♻️"),
            StoreItem("avatar_tech_3", "Energia Solar", "Avatar alimentado por energia solar", 220, StoreCategory.AVATAR, Rarity.COMMON, "☀️⚡"),
            StoreItem("avatar_tech_4", "Eco-Hacker", "Avatar que hackeia sistemas para ajudar o meio ambiente", 250, StoreCategory.AVATAR, Rarity.COMMON, "💻🌿"),

            // Raros
            StoreItem("avatar_tech_5", "Ciborgue Verde", "Avatar meio humano, meio máquina", 400, StoreCategory.AVATAR, Rarity.RARE, "🔋"),
            StoreItem("avatar_tech_6", "IA Ambiental", "Avatar inteligência artificial ecológica", 750, StoreCategory.AVATAR, Rarity.RARE, "🤖"),
            StoreItem("avatar_tech_7", "Nano-Tecnólogo", "Avatar com nanotecnologia verde", 450, StoreCategory.AVATAR, Rarity.RARE, "🔬🌿"),
            StoreItem("avatar_tech_8", "Engenheiro Verde", "Avatar engenheiro de tecnologias sustentáveis", 380, StoreCategory.AVATAR, Rarity.RARE, "⚙️🌿"),

            // Épicos
            StoreItem("avatar_tech_9", "Mestre da Energia", "Avatar com controle total sobre energias limpas", 700, StoreCategory.AVATAR, Rarity.EPIC, "⚡🌟"),
            StoreItem("avatar_tech_10", "Tecnólogo Supremo", "Avatar com tecnologia de última geração ecológica", 800, StoreCategory.AVATAR, Rarity.EPIC, "🚀🌿"),

            // ===== AVATARES ANIMAIS =====
            // Comuns
            StoreItem("avatar_animal_1", "Lobo da Reciclagem", "Avatar lobo com tema sustentável", 250, StoreCategory.AVATAR, Rarity.COMMON, "🐺♻️"),
            StoreItem("avatar_animal_2", "Raposa Verde", "Avatar raposa inteligente e ecológica", 200, StoreCategory.AVATAR, Rarity.COMMON, "🦊🌿"),
            StoreItem("avatar_animal_3", "Urso Guardião", "Avatar urso protetor da floresta", 280, StoreCategory.AVATAR, Rarity.COMMON, "🐻🌲"),
            StoreItem("avatar_animal_4", "Esquilo Eco", "Avatar esquilo coletor de nozes sustentável", 150, StoreCategory.AVATAR, Rarity.COMMON, "🐿️🌰"),

            // Raros
            StoreItem("avatar_animal_5", "Águia Verde", "Avatar águia com visão ecológica", 450, StoreCategory.AVATAR, Rarity.RARE, "🦅🌿"),
            StoreItem("avatar_animal_6", "Tigre da Floresta", "Avatar tigre das florestas tropicais", 500, StoreCategory.AVATAR, Rarity.RARE, "🐅🌴"),
            StoreItem("avatar_animal_7", "Elefante Sábio", "Avatar elefante com sabedoria ambiental", 420, StoreCategory.AVATAR, Rarity.RARE, "🐘🌿"),
            StoreItem("avatar_animal_8", "Golfinho Oceânico", "Avatar golfinho protetor dos mares", 380, StoreCategory.AVATAR, Rarity.RARE, "🐬🌊"),

            // Épicos
            StoreItem("avatar_animal_9", "Fênix Verde", "Avatar fênix que renasce das cinzas da poluição", 750, StoreCategory.AVATAR, Rarity.EPIC, "🦅🔥🌿"),
            StoreItem("avatar_animal_10", "Quimera da Natureza", "Avatar quimera com poderes naturais", 900, StoreCategory.AVATAR, Rarity.EPIC, "🦁🐐🐍🌿"),

            // Lendários
            StoreItem("avatar_animal_11", "Dragão da Terra", "Avatar dragão guardião do planeta", 1000, StoreCategory.AVATAR, Rarity.LEGENDARY, "🐉🌍"),
            StoreItem("avatar_animal_12", "Fênix Suprema", "Avatar fênix supremo da renovação ecológica", 1100, StoreCategory.AVATAR, Rarity.LEGENDARY, "🦅✨"),

            // ===== SELOS / BADGES =====
            // Comuns (50-200 pontos)
            StoreItem("badge_recycler", "Mestre Reciclador", "Selo para recicladores dedicados", 100, StoreCategory.BADGE, Rarity.COMMON, "♻️"),
            StoreItem("badge_water", "Guardião da Água", "Selo para protetores dos recursos hídricos", 150, StoreCategory.BADGE, Rarity.COMMON, "💧"),
            StoreItem("badge_energy", "Energizador Verde", "Selo para economizadores de energia", 150, StoreCategory.BADGE, Rarity.COMMON, "⚡"),
            StoreItem("badge_plant", "Mestre Jardineiro", "Selo para quem planta e cuida do verde", 120, StoreCategory.BADGE, Rarity.COMMON, "🌱"),
            StoreItem("badge_cleaner", "Limpador de Praias", "Selo para limpadores de praias", 180, StoreCategory.BADGE, Rarity.COMMON, "🏖️"),
            StoreItem("badge_composter", "Compostador Master", "Selo para mestres do composto", 140, StoreCategory.BADGE, Rarity.COMMON, "🌱♻️"),
            StoreItem("badge_biker", "Ciclista Verde", "Selo para ciclistas urbanos", 160, StoreCategory.BADGE, Rarity.COMMON, "🚲🌿"),
            StoreItem("badge_walker", "Caminhante Eco", "Selo para quem caminha e economiza combustível", 100, StoreCategory.BADGE, Rarity.COMMON, "🚶‍♂️🌿"),
            StoreItem("badge_organic", "Comprador Orgânico", "Selo para quem compra produtos orgânicos", 200, StoreCategory.BADGE, Rarity.COMMON, "🥬"),
            StoreItem("badge_reuser", "Reutilizador Criativo", "Selo para quem reutiliza materiais", 130, StoreCategory.BADGE, Rarity.COMMON, "🔧♻️"),

            // Raros (250-400 pontos)
            StoreItem("badge_animal", "Amigo dos Animais", "Selo para defensores da fauna", 300, StoreCategory.BADGE, Rarity.RARE, "🐾"),
            StoreItem("badge_forest", "Protetor de Florestas", "Selo para quem protege as florestas", 350, StoreCategory.BADGE, Rarity.RARE, "🌲"),
            StoreItem("badge_ocean", "Guardião dos Oceanos", "Selo para protetores dos mares", 380, StoreCategory.BADGE, Rarity.RARE, "🌊"),
            StoreItem("badge_climate", "Combatente da Mudança Climática", "Selo para quem reduz emissões de CO2", 400, StoreCategory.BADGE, Rarity.RARE, "🌍🔥"),
            StoreItem("badge_solar", "Energia Solar", "Selo para usuários de energia solar", 320, StoreCategory.BADGE, Rarity.RARE, "☀️⚡"),
            StoreItem("badge_wind", "Energia Eólica", "Selo para apoiadores de energia eólica", 340, StoreCategory.BADGE, Rarity.RARE, "💨⚡"),
            StoreItem("badge_educator", "Educador Ambiental", "Selo para quem educa sobre o meio ambiente", 360, StoreCategory.BADGE, Rarity.RARE, "📚🌿"),
            StoreItem("badge_volunteer", "Voluntário Verde", "Selo para voluntários ambientais", 280, StoreCategory.BADGE, Rarity.RARE, "🤝🌿"),

            // Épicos (500-700 pontos)
            StoreItem("badge_earth", "Guardião da Terra", "Selo supremo do meio ambiente", 600, StoreCategory.BADGE, Rarity.EPIC, "🌍"),
            StoreItem("badge_eco_warrior", "Guerreiro Ecológico", "Selo para guerreiros do meio ambiente", 650, StoreCategory.BADGE, Rarity.EPIC, "⚔️🌿"),
            StoreItem("badge_zero_waste", "Zero Resíduos", "Selo para quem produz zero resíduos", 700, StoreCategory.BADGE, Rarity.EPIC, "0️⃣♻️"),
            StoreItem("badge_carbon_neutral", "Carbono Neutro", "Selo para quem compensa todas as emissões", 750, StoreCategory.BADGE, Rarity.EPIC, "🌱⚖️"),
            StoreItem("badge_biodiversity", "Protetor da Biodiversidade", "Selo para protetores de todas as formas de vida", 550, StoreCategory.BADGE, Rarity.EPIC, "🦋🌿"),

            // Lendários (800-1200 pontos)
            StoreItem("badge_eco_master", "Mestre Supremo da Ecologia", "Selo máximo de maestria ecológica", 1000, StoreCategory.BADGE, Rarity.LEGENDARY, "👑🌿"),
            StoreItem("badge_planet_savior", "Salvador do Planeta", "Selo para heróis ambientais", 1200, StoreCategory.BADGE, Rarity.LEGENDARY, "🌍🛡️"),

            // ===== TEMAS VISUAIS =====
            // Raros (300-400 pontos)
            StoreItem("theme_ocean", "Tema Oceano", "Tema visual inspirado no oceano", 300, StoreCategory.THEME, Rarity.RARE, "🌊"),
            StoreItem("theme_forest", "Tema Floresta", "Tema visual inspirado na floresta", 300, StoreCategory.THEME, Rarity.RARE, "🌲"),
            StoreItem("theme_sunset", "Tema Pôr do Sol", "Tema visual com cores do pôr do sol", 400, StoreCategory.THEME, Rarity.EPIC, "🌅"),
            StoreItem("theme_mountain", "Tema Montanhas", "Tema visual com montanhas majestosas", 350, StoreCategory.THEME, Rarity.RARE, "⛰️"),
            StoreItem("theme_garden", "Tema Jardim", "Tema visual com flores e jardins", 320, StoreCategory.THEME, Rarity.RARE, "🌸"),
            StoreItem("theme_rain", "Tema Chuva", "Tema visual com gotas de chuva refrescantes", 380, StoreCategory.THEME, Rarity.RARE, "🌧️"),
            StoreItem("theme_northern_lights", "Tema Aurora Boreal", "Tema visual com luzes do norte", 450, StoreCategory.THEME, Rarity.EPIC, "🌌"),
            StoreItem("theme_underwater", "Tema Subaquático", "Tema visual com vida marinha", 400, StoreCategory.THEME, Rarity.EPIC, "🐠"),
            StoreItem("theme_autumn", "Tema Outono", "Tema visual com folhas de outono", 330, StoreCategory.THEME, Rarity.RARE, "🍁"),
            StoreItem("theme_spring", "Tema Primavera", "Tema visual com flores da primavera", 310, StoreCategory.THEME, Rarity.RARE, "🌺"),
            StoreItem("theme_desert", "Tema Deserto", "Tema visual com dunas e cactos", 340, StoreCategory.THEME, Rarity.RARE, "🏜️"),
            StoreItem("theme_arctic", "Tema Ártico", "Tema visual com gelo e neve", 420, StoreCategory.THEME, Rarity.EPIC, "❄️"),
            StoreItem("theme_volcano", "Tema Vulcão", "Tema visual com lava e fogo", 500, StoreCategory.THEME, Rarity.EPIC, "🌋"),
            StoreItem("theme_space_green", "Tema Espaço Verde", "Tema visual com galáxias e estrelas verdes", 550, StoreCategory.THEME, Rarity.EPIC, "🌌🌿"),
            StoreItem("theme_crystal", "Tema Cristal", "Tema visual com cristais energéticos", 600, StoreCategory.THEME, Rarity.LEGENDARY, "💎"),
            StoreItem("theme_phoenix", "Tema Fênix", "Tema visual com fênix renovadora", 700, StoreCategory.THEME, Rarity.LEGENDARY, "🦅🔥"),

            // ===== EFEITOS VISUAIS =====
            // Raros (250-400 pontos)
            StoreItem("effect_particles", "Partículas Douradas", "Efeitos visuais dourados", 250, StoreCategory.EFFECT, Rarity.RARE, "✨"),
            StoreItem("effect_leaves", "Folhas Dançantes", "Efeitos de folhas caindo", 350, StoreCategory.EFFECT, Rarity.EPIC, "🍂"),
            StoreItem("effect_stars", "Chuva de Estrelas", "Efeitos de estrelas brilhantes", 500, StoreCategory.EFFECT, Rarity.LEGENDARY, "⭐"),
            StoreItem("effect_butterflies", "Mariposas Coloridas", "Efeitos de borboletas voando", 280, StoreCategory.EFFECT, Rarity.RARE, "🦋"),
            StoreItem("effect_fireflies", "Vaga-lumes Mágicos", "Efeitos de vaga-lumes brilhantes", 320, StoreCategory.EFFECT, Rarity.RARE, "✨"),
            StoreItem("effect_snow", "Neve Suave", "Efeitos de flocos de neve", 300, StoreCategory.EFFECT, Rarity.RARE, "❄️"),
            StoreItem("effect_rainbow", "Arco-íris", "Efeitos de arco-íris colorido", 450, StoreCategory.EFFECT, Rarity.EPIC, "🌈"),
            StoreItem("effect_lightning", "Relâmpagos Verdes", "Efeitos de relâmpagos ecológicos", 400, StoreCategory.EFFECT, Rarity.EPIC, "⚡🌿"),
            StoreItem("effect_bubbles", "Bolhas de Ar", "Efeitos de bolhas subindo", 260, StoreCategory.EFFECT, Rarity.RARE, "💧"),
            StoreItem("effect_sparkles", "Brilhos Mágicos", "Efeitos de brilhos mágicos", 380, StoreCategory.EFFECT, Rarity.EPIC, "✨"),
            StoreItem("effect_wind", "Vento Verde", "Efeitos de vento ecológico", 330, StoreCategory.EFFECT, Rarity.RARE, "💨🌿"),
            StoreItem("effect_flower_petals", "Pétalas de Flores", "Efeitos de pétalas caindo", 370, StoreCategory.EFFECT, Rarity.EPIC, "🌸"),
            StoreItem("effect_ocean_waves", "Ondas do Mar", "Efeitos de ondas oceânicas", 420, StoreCategory.EFFECT, Rarity.EPIC, "🌊"),
            StoreItem("effect_sakura", "Flor de Cerejeira", "Efeitos de flores de cerejeira", 480, StoreCategory.EFFECT, Rarity.EPIC, "🌸"),
            StoreItem("effect_northern_lights", "Aurora Boreal", "Efeitos de luzes do norte", 600, StoreCategory.EFFECT, Rarity.LEGENDARY, "🌌"),
            StoreItem("effect_phoenix_fire", "Fogo de Fênix", "Efeitos de fogo renovador", 700, StoreCategory.EFFECT, Rarity.LEGENDARY, "🔥✨"),
            StoreItem("effect_dragon_aura", "Aura de Dragão", "Efeitos de aura poderosa", 800, StoreCategory.EFFECT, Rarity.LEGENDARY, "🐉✨"),
            StoreItem("effect_crystal_glow", "Brilho de Cristal", "Efeitos de brilho cristalino", 650, StoreCategory.EFFECT, Rarity.LEGENDARY, "💎✨"),
            StoreItem("effect_nature_aura", "Aura da Natureza", "Efeitos de aura natural", 550, StoreCategory.EFFECT, Rarity.EPIC, "🌿✨"),

            // ===== ITENS ADICIONAIS ESPECIAIS =====
            // AVATARES - Edições Especiais
            StoreItem("avatar_amazon_guardian", "Guardião da Amazônia", "Avatar especial da floresta amazônica", 800, StoreCategory.AVATAR, Rarity.LEGENDARY, "🌳🦜"),
            StoreItem("avatar_arctic_defender", "Defensor do Ártico", "Avatar protetor do gelo polar", 750, StoreCategory.AVATAR, Rarity.LEGENDARY, "🐧❄️"),
            StoreItem("avatar_coral_savior", "Salvador dos Corais", "Avatar protetor dos recifes de coral", 720, StoreCategory.AVATAR, Rarity.LEGENDARY, "🐠🪸"),
            StoreItem("avatar_sustainable_farmer", "Fazendeiro Sustentável", "Avatar agricultor ecológico", 420, StoreCategory.AVATAR, Rarity.EPIC, "🚜🌾"),
            StoreItem("avatar_wind_rider", "Cavaleiro do Vento", "Avatar com domínio da energia eólica", 680, StoreCategory.AVATAR, Rarity.EPIC, "💨🐎"),
            StoreItem("avatar_solar_pharaoh", "Faraó Solar", "Avatar com poder do sol", 900, StoreCategory.AVATAR, Rarity.LEGENDARY, "☀️👑"),
            StoreItem("avatar_eco_ninja", "Ninja Ecológico", "Avatar silencioso e sustentável", 520, StoreCategory.AVATAR, Rarity.EPIC, "🥷🌿"),
            StoreItem("avatar_green_mage", "Mago Verde", "Avatar com magia da natureza", 760, StoreCategory.AVATAR, Rarity.LEGENDARY, "🧙‍♂️🌿"),
            StoreItem("avatar_earth_shaman", "Xamã da Terra", "Avatar espiritual da terra", 640, StoreCategory.AVATAR, Rarity.EPIC, "🗿🌿"),
            StoreItem("avatar_panda_master", "Mestre Panda", "Avatar zen e sustentável", 480, StoreCategory.AVATAR, Rarity.RARE, "🐼🧘"),

            // SELOS - Edições Limitadas
            StoreItem("badge_amazon_ally", "Aliado da Amazônia", "Selo especial da floresta amazônica", 600, StoreCategory.BADGE, Rarity.LEGENDARY, "🌳🦜"),
            StoreItem("badge_carbon_negative", "Carbono Negativo", "Selo para quem remove mais carbono do que emite", 800, StoreCategory.BADGE, Rarity.LEGENDARY, "➖🌱"),
            StoreItem("badge_circular_economy", "Economia Circular", "Selo para praticantes de economia circular", 650, StoreCategory.BADGE, Rarity.EPIC, "♻️🔄"),
            StoreItem("badge_renewable_energy", "Energia Renovável", "Selo para usuários de energia 100% renovável", 700, StoreCategory.BADGE, Rarity.EPIC, "🔋⚡"),
            StoreItem("badge_biodiversity_guardian", "Guardião da Biodiversidade", "Selo para protetores da biodiversidade global", 750, StoreCategory.BADGE, Rarity.EPIC, "🦋🌍"),
            StoreItem("badge_climate_activist", "Ativista Climático", "Selo para ativistas da mudança climática", 680, StoreCategory.BADGE, Rarity.EPIC, "🌍🔥"),
            StoreItem("badge_eco_innovator", "Inovador Eco", "Selo para inovadores em sustentabilidade", 620, StoreCategory.BADGE, Rarity.EPIC, "💡🌿"),
            StoreItem("badge_green_entrepreneur", "Empreendedor Verde", "Selo para empresários sustentáveis", 580, StoreCategory.BADGE, Rarity.RARE, "💼🌿"),
            StoreItem("badge_urban_gardener", "Jardineiro Urbano", "Selo para quem cultiva em espaços urbanos", 320, StoreCategory.BADGE, Rarity.RARE, "🏙️🌱"),
            StoreItem("badge_compost_hero", "Herói do Composto", "Selo para mestres do compostagem", 280, StoreCategory.BADGE, Rarity.RARE, "🌱♻️"),
            StoreItem("badge_water_saver", "Economizador de Água", "Selo para quem economiza água", 240, StoreCategory.BADGE, Rarity.RARE, "💧🚿"),
            StoreItem("badge_green_commuter", "Deslocamento Verde", "Selo para quem usa transporte sustentável", 360, StoreCategory.BADGE, Rarity.RARE, "🚲🚌"),
            StoreItem("badge_eco_shopper", "Comprador Consciente", "Selo para consumidores conscientes", 300, StoreCategory.BADGE, Rarity.RARE, "🛒🌿"),
            StoreItem("badge_waste_reducer", "Redutor de Resíduos", "Selo para quem minimiza resíduos", 420, StoreCategory.BADGE, Rarity.EPIC, "🗑️❌"),
            StoreItem("badge_green_teacher", "Professor Verde", "Selo para educadores ambientais", 520, StoreCategory.BADGE, Rarity.EPIC, "👨‍🏫🌿"),
            StoreItem("badge_eco_researcher", "Pesquisador Eco", "Selo para pesquisadores em sustentabilidade", 640, StoreCategory.BADGE, Rarity.EPIC, "🔬🌿"),
            StoreItem("badge_sustainable_designer", "Designer Sustentável", "Selo para designers ecológicos", 560, StoreCategory.BADGE, Rarity.EPIC, "🎨🌿"),
            StoreItem("badge_green_architect", "Arquiteto Verde", "Selo para arquitetos sustentáveis", 720, StoreCategory.BADGE, Rarity.LEGENDARY, "🏗️🌿"),
            StoreItem("badge_eco_lawyer", "Advogado Ambiental", "Selo para defensores legais do meio ambiente", 680, StoreCategory.BADGE, Rarity.LEGENDARY, "⚖️🌿"),

            // TEMAS - Temas Premium
            StoreItem("theme_eco_luxury", "Tema Luxo Eco", "Tema visual luxuoso e sustentável", 600, StoreCategory.THEME, Rarity.LEGENDARY, "💎🌿"),
            StoreItem("theme_nature_symphony", "Tema Sinfonia da Natureza", "Tema visual com harmonia natural", 550, StoreCategory.THEME, Rarity.LEGENDARY, "🎼🌿"),
            StoreItem("theme_sustainable_future", "Tema Futuro Sustentável", "Tema visual com tecnologia ecológica", 580, StoreCategory.THEME, Rarity.LEGENDARY, "🚀🌿"),
            StoreItem("theme_eco_paradise", "Tema Paraíso Ecológico", "Tema visual com natureza intocada", 520, StoreCategory.THEME, Rarity.EPIC, "🏝️🌿"),
            StoreItem("theme_green_metropolis", "Tema Metrópole Verde", "Tema visual com cidade sustentável", 480, StoreCategory.THEME, Rarity.EPIC, "🏙️🌿"),
            StoreItem("theme_renewable_world", "Tema Mundo Renovável", "Tema visual com energias renováveis", 460, StoreCategory.THEME, Rarity.EPIC, "🌍⚡"),
            StoreItem("theme_circular_economy", "Tema Economia Circular", "Tema visual com reciclagem infinita", 440, StoreCategory.THEME, Rarity.EPIC, "♻️🔄"),
            StoreItem("theme_biodiversity", "Tema Biodiversidade", "Tema visual com variedade de vida", 420, StoreCategory.THEME, Rarity.EPIC, "🦋🐠🌿"),
            StoreItem("theme_climate_action", "Tema Ação Climática", "Tema visual com combate à mudança climática", 500, StoreCategory.THEME, Rarity.EPIC, "🌍🔥❄️"),
            StoreItem("theme_eco_harmony", "Tema Harmonia Eco", "Tema visual com equilíbrio natural", 380, StoreCategory.THEME, Rarity.RARE, "☯️🌿"),
            StoreItem("theme_green_energy", "Tema Energia Verde", "Tema visual com energia limpa", 360, StoreCategory.THEME, Rarity.RARE, "⚡🌿"),
            StoreItem("theme_water_conservation", "Tema Conservação da Água", "Tema visual com preservação hídrica", 340, StoreCategory.THEME, Rarity.RARE, "💧🌿"),
            StoreItem("theme_zero_waste", "Tema Zero Resíduos", "Tema visual com desperdício zero", 320, StoreCategory.THEME, Rarity.RARE, "0️⃣♻️"),

            // EFEITOS - Efeitos Ultra
            StoreItem("effect_nova_explosion", "Explosão de Nova Verde", "Efeitos de explosão estelar ecológica", 650, StoreCategory.EFFECT, Rarity.LEGENDARY, "💥🌿"),
            StoreItem("effect_ecosystem", "Ecossistema Vivo", "Efeitos de ecossistema completo", 700, StoreCategory.EFFECT, Rarity.LEGENDARY, "🌍🦋🌿"),
            StoreItem("effect_quantum_green", "Quântico Verde", "Efeitos de partículas quânticas verdes", 750, StoreCategory.EFFECT, Rarity.LEGENDARY, "⚛️🌿"),
            StoreItem("effect_gaia_blessing", "Bênção de Gaia", "Efeitos da mãe natureza", 800, StoreCategory.EFFECT, Rarity.LEGENDARY, "🌍✨"),
            StoreItem("effect_renewable_spiral", "Espiral Renovável", "Efeitos de espiral de energia renovável", 620, StoreCategory.EFFECT, Rarity.EPIC, "🔄⚡"),
            StoreItem("effect_carbon_absorption", "Absorção de Carbono", "Efeitos de remoção de carbono", 580, StoreCategory.EFFECT, Rarity.EPIC, "🌱➖"),
            StoreItem("effect_green_revolution", "Revolução Verde", "Efeitos de transformação ecológica", 540, StoreCategory.EFFECT, Rarity.EPIC, "✊🌿"),
            StoreItem("effect_sustainable_transformation", "Transformação Sustentável", "Efeitos de mudança sustentável", 500, StoreCategory.EFFECT, Rarity.EPIC, "🔄🌿"),
            StoreItem("effect_eco_ripple", "Onda Eco", "Efeitos de onda de impacto ecológico", 460, StoreCategory.EFFECT, Rarity.RARE, "〰️🌿"),
            StoreItem("effect_green_pulse", "Pulso Verde", "Efeitos de pulsação ecológica", 420, StoreCategory.EFFECT, Rarity.RARE, "💚📈"),
            StoreItem("effect_nature_wave", "Onda da Natureza", "Efeitos de onda natural", 380, StoreCategory.EFFECT, Rarity.RARE, "🌊🌿"),
            StoreItem("effect_eco_burst", "Explosão Eco", "Efeitos de explosão de energia verde", 440, StoreCategory.EFFECT, Rarity.EPIC, "💥⚡"),
            StoreItem("effect_sustainable_swirl", "Redemoinho Sustentável", "Efeitos de redemoinho ecológico", 480, StoreCategory.EFFECT, Rarity.EPIC, "🌀🌿"),
            StoreItem("effect_green_energy_burst", "Explosão de Energia Verde", "Efeitos de explosão de energia renovável", 520, StoreCategory.EFFECT, Rarity.EPIC, "⚡💚"),
            StoreItem("effect_climate_healing", "Cura Climática", "Efeitos de cura do planeta", 680, StoreCategory.EFFECT, Rarity.LEGENDARY, "🌍💚"),
            StoreItem("effect_planetary_guardian", "Guardião Planetário", "Efeitos de proteção global", 720, StoreCategory.EFFECT, Rarity.LEGENDARY, "🌍🛡️"),
            StoreItem("effect_eco_champion", "Campeão Eco", "Efeitos de vitória ecológica", 660, StoreCategory.EFFECT, Rarity.LEGENDARY, "🏆🌿"),
            StoreItem("effect_green_phoenix", "Fênix Verde", "Efeitos de renascimento ecológico", 640, StoreCategory.EFFECT, Rarity.LEGENDARY, "🦅🔥🌿"),
            StoreItem("effect_nature_guardian", "Guardião da Natureza", "Efeitos de proteção natural", 560, StoreCategory.EFFECT, Rarity.EPIC, "🌳🛡️"),
            StoreItem("effect_sustainable_shield", "Escudo Sustentável", "Efeitos de proteção ecológica", 420, StoreCategory.EFFECT, Rarity.RARE, "🛡️🌿"),
            StoreItem("effect_eco_aura", "Aura Eco", "Efeitos de aura ecológica", 380, StoreCategory.EFFECT, Rarity.RARE, "✨🌿"),
            StoreItem("effect_green_vortex", "Vórtice Verde", "Efeitos de vórtice ecológico", 460, StoreCategory.EFFECT, Rarity.EPIC, "🌀💚"),
            StoreItem("effect_renewable_storm", "Tempestade Renovável", "Efeitos de tempestade de energia limpa", 580, StoreCategory.EFFECT, Rarity.EPIC, "⛈️⚡"),
            StoreItem("effect_eco_supernova", "Supernova Eco", "Efeitos de explosão estelar ecológica", 820, StoreCategory.EFFECT, Rarity.LEGENDARY, "💥⭐🌿")
        )
    }
}

data class StoreUiState(
    val items: List<StoreItem> = emptyList(),
    val selectedCategory: StoreCategory? = null,
    val isLoading: Boolean = true
)
