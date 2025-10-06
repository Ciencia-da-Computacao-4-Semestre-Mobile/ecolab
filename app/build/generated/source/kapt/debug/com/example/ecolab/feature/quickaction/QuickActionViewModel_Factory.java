package com.example.ecolab.feature.quickaction;

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
public final class QuickActionViewModel_Factory implements Factory<QuickActionViewModel> {
  @Override
  public QuickActionViewModel get() {
    return newInstance();
  }

  public static QuickActionViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static QuickActionViewModel newInstance() {
    return new QuickActionViewModel();
  }

  private static final class InstanceHolder {
    private static final QuickActionViewModel_Factory INSTANCE = new QuickActionViewModel_Factory();
  }
}
