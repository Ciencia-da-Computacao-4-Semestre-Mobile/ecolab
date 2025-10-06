package com.example.ecolab.presentation.ui.map;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class MapViewModel_Factory implements Factory<MapViewModel> {
  private final Provider<Context> contextProvider;

  public MapViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MapViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static MapViewModel_Factory create(Provider<Context> contextProvider) {
    return new MapViewModel_Factory(contextProvider);
  }

  public static MapViewModel newInstance(Context context) {
    return new MapViewModel(context);
  }
}
