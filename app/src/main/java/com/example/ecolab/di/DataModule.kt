package com.example.ecolab.di

import android.content.Context
import com.example.ecolab.core.data.repository.PointsRepositoryImpl
import com.example.ecolab.core.domain.repository.PointsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePointsRepository(
        @ApplicationContext context: Context,
        json: Json
    ): PointsRepository = PointsRepositoryImpl(context, json)

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }
}
