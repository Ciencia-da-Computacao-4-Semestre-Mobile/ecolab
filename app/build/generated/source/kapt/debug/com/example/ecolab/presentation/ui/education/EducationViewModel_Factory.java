package com.example.ecolab.presentation.ui.education;

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
public final class EducationViewModel_Factory implements Factory<EducationViewModel> {
  @Override
  public EducationViewModel get() {
    return newInstance();
  }

  public static EducationViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EducationViewModel newInstance() {
    return new EducationViewModel();
  }

  private static final class InstanceHolder {
    private static final EducationViewModel_Factory INSTANCE = new EducationViewModel_Factory();
  }
}
