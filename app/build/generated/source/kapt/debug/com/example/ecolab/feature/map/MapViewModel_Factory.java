package com.example.ecolab.feature.map;

import android.location.Geocoder;
import com.example.ecolab.core.domain.repository.PointsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MapViewModel_Factory implements Factory<MapViewModel> {
  private final Provider<PointsRepository> pointsRepositoryProvider;

  private final Provider<Geocoder> geocoderProvider;

  public MapViewModel_Factory(Provider<PointsRepository> pointsRepositoryProvider,
      Provider<Geocoder> geocoderProvider) {
    this.pointsRepositoryProvider = pointsRepositoryProvider;
    this.geocoderProvider = geocoderProvider;
  }

  @Override
  public MapViewModel get() {
    return newInstance(pointsRepositoryProvider.get(), geocoderProvider.get());
  }

  public static MapViewModel_Factory create(Provider<PointsRepository> pointsRepositoryProvider,
      Provider<Geocoder> geocoderProvider) {
    return new MapViewModel_Factory(pointsRepositoryProvider, geocoderProvider);
  }

  public static MapViewModel newInstance(PointsRepository pointsRepository, Geocoder geocoder) {
    return new MapViewModel(pointsRepository, geocoder);
  }
}
