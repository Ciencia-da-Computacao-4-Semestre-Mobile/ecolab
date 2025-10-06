package com.example.ecolab.presentation.ui.conquests;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ConquestsViewModel_Factory implements Factory<ConquestsViewModel> {
  @Override
  public ConquestsViewModel get() {
    return newInstance();
  }

  public static ConquestsViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ConquestsViewModel newInstance() {
    return new ConquestsViewModel();
  }

  private static final class InstanceHolder {
    private static final ConquestsViewModel_Factory INSTANCE = new ConquestsViewModel_Factory();
  }
}
