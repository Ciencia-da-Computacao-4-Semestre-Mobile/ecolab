package com.example.ecolab.di

import android.content.Context
import android.location.Geocoder
import com.example.ecolab.core.data.prepopulation.GeoJsonParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Apenas Utils - SEM repositórios ou Firebase
    @Provides @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder = Geocoder(context)

    @Provides @Singleton
    fun provideGeoJsonParser(@ApplicationContext context: Context) = GeoJsonParser(context)

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}