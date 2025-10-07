package com.example.ecolab.di

import android.content.Context
import com.example.ecolab.core.data.prepopulation.GeoJsonParser
import com.example.ecolab.core.data.repository.AssetPointsRepository
import com.example.ecolab.core.domain.repository.PointsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPointsRepository(impl: AssetPointsRepository): PointsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ParserModule {

    @Provides
    @Singleton
    fun provideGeoJsonParser(@ApplicationContext context: Context): GeoJsonParser {
        return GeoJsonParser(context)
    }
}
