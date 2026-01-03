plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "nodomain.pacjo.wear.watchface.pointy_damascus"
    compileSdk = 36

    defaultConfig {
        applicationId = "nodomain.pacjo.wear.watchface.pointy_damascus"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "1.0"

        manifestPlaceholders["watchFaceServiceClassName"] = "$applicationId.PointyDamascusWatchFaceService"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":wear:jetpack:watchfaces:base_digital"))
    implementation(project(":wear:jetpack:feature:editor"))
    implementation(project(":wear:jetpack:feature:overlay"))
    implementation(project(":wear:jetpack:feature:cell_grid"))

    implementation(libs.openrndr.orx.noise) {
        exclude(group = "org.openrndr", module = "openrndr-draw")     // exclude unneeded dependency
    }
}