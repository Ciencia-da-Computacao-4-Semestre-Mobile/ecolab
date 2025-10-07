plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.kapt) apply false // Added this line
    alias(libs.plugins.mapsplatform.secrets) apply false
    alias(libs.plugins.google.services) apply false
}
