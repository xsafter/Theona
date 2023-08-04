// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false

    id("com.google.dagger.hilt.android") version "2.44" apply false

    id("com.autonomousapps.dependency-analysis") version "1.20.0" // Dependency Lint: ./gradlew buildHealth

    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false

}
