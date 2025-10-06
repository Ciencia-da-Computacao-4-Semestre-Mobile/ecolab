package com.example.ecolab.feature.ranking;

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
public final class RankingViewModel_Factory implements Factory<RankingViewModel> {
  @Override
  public RankingViewModel get() {
    return newInstance();
  }

  public static RankingViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RankingViewModel newInstance() {
    return new RankingViewModel();
  }

  private static final class InstanceHolder {
    private static final RankingViewModel_Factory INSTANCE = new RankingViewModel_Factory();
  }
}
