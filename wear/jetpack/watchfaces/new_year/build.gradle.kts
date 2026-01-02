plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "nodomain.pacjo.wear.watchface.new_year"
    compileSdk = 36

    defaultConfig {
        applicationId = "nodomain.pacjo.wear.watchface.new_year"
        minSdk = 28
        targetSdk = 36
        versionCode = 10
        versionName = "1.0"

        manifestPlaceholders["watchFaceServiceClassName"] = "$applicationId.NewYearWatchFaceService"
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
    implementation(project(":wear:jetpack:watchfaces:base_digital"))
    implementation(project(":wear:jetpack:feature:overlay"))
    implementation(project(":wear:jetpack:feature:editor"))
}