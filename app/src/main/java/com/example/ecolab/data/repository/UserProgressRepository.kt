package com.example.ecolab.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.ecolab.data.model.Achievement
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_progress")

@Singleton
class UserProgressRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val plasticMissionCompleted = booleanPreferencesKey("achievement_plastic_pioneer")
        val glassMissionCompleted = booleanPreferencesKey("achievement_glass_guardian")
        val firstContribution = booleanPreferencesKey("achievement_first_contribution")
    }

    private val allAchievements = listOf(
        Achievement(
            id = Keys.plasticMissionCompleted.name,
            title = "Pioneiro do Plástico",
            description = "Faça seu primeiro descarte de plástico.",
        ),
        Achievement(
            id = Keys.glassMissionCompleted.name,
            title = "Guardião do Vidro",
            description = "Faça seu primeiro descarte de vidro.",
        ),
        Achievement(
            id = Keys.firstContribution.name,
            title = "Cidadão Consciente",
            description = "Adicione seu primeiro ponto de coleta no mapa.",
        )
    )

    val achievements: Flow<List<Achievement>> = context.dataStore.data
        .map { preferences ->
            allAchievements.map { achievement ->
                val isUnlocked = preferences[booleanPreferencesKey(achievement.id)] ?: false
                achievement.copy(isUnlocked = isUnlocked)
            }
        }

    suspend fun completeMission(wasteType: String) {
        val key = when (wasteType) {
            "Plástico" -> Keys.plasticMissionCompleted
            "Vidro" -> Keys.glassMissionCompleted
            else -> null
        }
        key?.let { aKey ->
            context.dataStore.edit { preferences ->
                preferences[aKey] = true
            }
        }
        // Always mark first contribution on any submission
        context.dataStore.edit {
            it[Keys.firstContribution] = true
        }
    }
}
