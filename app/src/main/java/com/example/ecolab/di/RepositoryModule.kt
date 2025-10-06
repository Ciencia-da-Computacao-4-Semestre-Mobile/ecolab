package com.example.ecolab.di

import com.example.ecolab.data.repository.MockPointsRepository
import com.example.ecolab.domain.PointsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the MockPointsRepository implementation to the PointsRepository interface.
     * This tells Hilt to use MockPointsRepository whenever PointsRepository is requested.
     */
    @Binds
    @Singleton
    abstract fun bindPointsRepository(repository: MockPointsRepository): PointsRepository
}
