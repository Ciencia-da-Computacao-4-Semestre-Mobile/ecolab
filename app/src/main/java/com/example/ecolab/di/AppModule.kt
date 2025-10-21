package com.example.ecolab.di

import android.content.Context
import android.location.Geocoder
import com.example.ecolab.core.data.prepopulation.GeoJsonParser
import com.example.ecolab.core.data.repository.AuthRepositoryImpl
import com.example.ecolab.core.data.repository.FirebasePointsRepository
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun providePointsRepository(impl: FirebasePointsRepository): PointsRepository = impl

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder = Geocoder(context)

    @Provides
    @Singleton
    fun provideGeoJsonParser(@ApplicationContext context: Context): GeoJsonParser {
        return GeoJsonParser(context)
    }
}
