package com.example.ecolab.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class UserProgressRepository_Factory implements Factory<UserProgressRepository> {
  private final Provider<Context> contextProvider;

  public UserProgressRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UserProgressRepository get() {
    return newInstance(contextProvider.get());
  }

  public static UserProgressRepository_Factory create(Provider<Context> contextProvider) {
    return new UserProgressRepository_Factory(contextProvider);
  }

  public static UserProgressRepository newInstance(Context context) {
    return new UserProgressRepository(context);
  }
}
