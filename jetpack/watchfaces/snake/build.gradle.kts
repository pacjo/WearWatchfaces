plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "nodomain.pacjo.wear.watchface.snake"
    compileSdk = 36

    defaultConfig {
        applicationId = "nodomain.pacjo.wear.watchface.snake"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "1.0"

        manifestPlaceholders["watchFaceServiceClassName"] = "$applicationId.SnakeWatchFaceService"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":jetpack:watchfaces:base_digital"))
    implementation(project(":jetpack:feature:background"))
    implementation(project(":jetpack:feature:cell_grid"))
}