package com.example.ecolab.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class RankingRepository_Factory implements Factory<RankingRepository> {
  @Override
  public RankingRepository get() {
    return newInstance();
  }

  public static RankingRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RankingRepository newInstance() {
    return new RankingRepository();
  }

  private static final class InstanceHolder {
    private static final RankingRepository_Factory INSTANCE = new RankingRepository_Factory();
  }
}
