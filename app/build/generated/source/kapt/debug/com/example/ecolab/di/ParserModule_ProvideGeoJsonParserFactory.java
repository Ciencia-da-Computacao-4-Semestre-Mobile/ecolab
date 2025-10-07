package com.example.ecolab.di;

import android.content.Context;
import com.example.ecolab.core.data.prepopulation.GeoJsonParser;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ParserModule_ProvideGeoJsonParserFactory implements Factory<GeoJsonParser> {
  private final Provider<Context> contextProvider;

  public ParserModule_ProvideGeoJsonParserFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public GeoJsonParser get() {
    return provideGeoJsonParser(contextProvider.get());
  }

  public static ParserModule_ProvideGeoJsonParserFactory create(Provider<Context> contextProvider) {
    return new ParserModule_ProvideGeoJsonParserFactory(contextProvider);
  }

  public static GeoJsonParser provideGeoJsonParser(Context context) {
    return Preconditions.checkNotNullFromProvides(ParserModule.INSTANCE.provideGeoJsonParser(context));
  }
}
