package com.example.ecolab.core.data.repository;

import com.example.ecolab.core.data.prepopulation.GeoJsonParser;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AssetPointsRepository_Factory implements Factory<AssetPointsRepository> {
  private final Provider<GeoJsonParser> geoJsonParserProvider;

  public AssetPointsRepository_Factory(Provider<GeoJsonParser> geoJsonParserProvider) {
    this.geoJsonParserProvider = geoJsonParserProvider;
  }

  @Override
  public AssetPointsRepository get() {
    return newInstance(geoJsonParserProvider.get());
  }

  public static AssetPointsRepository_Factory create(
      Provider<GeoJsonParser> geoJsonParserProvider) {
    return new AssetPointsRepository_Factory(geoJsonParserProvider);
  }

  public static AssetPointsRepository newInstance(GeoJsonParser geoJsonParser) {
    return new AssetPointsRepository(geoJsonParser);
  }
}
