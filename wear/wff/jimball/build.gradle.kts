plugins {
    alias(libs.plugins.android.application)
    // TODO: configure wff-validator
}

android {
    namespace = "nodomain.pacjo.wear.watchface.jimball"
    compileSdk = 36

    defaultConfig {
        applicationId = "nodomain.pacjo.wear.watchface.jimball"
        minSdk = 33
        targetSdk = 36
        versionCode = 10
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}