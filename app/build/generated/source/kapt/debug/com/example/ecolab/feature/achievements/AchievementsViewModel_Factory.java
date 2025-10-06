package com.example.ecolab.feature.achievements;

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
public final class AchievementsViewModel_Factory implements Factory<AchievementsViewModel> {
  @Override
  public AchievementsViewModel get() {
    return newInstance();
  }

  public static AchievementsViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AchievementsViewModel newInstance() {
    return new AchievementsViewModel();
  }

  private static final class InstanceHolder {
    private static final AchievementsViewModel_Factory INSTANCE = new AchievementsViewModel_Factory();
  }
}
