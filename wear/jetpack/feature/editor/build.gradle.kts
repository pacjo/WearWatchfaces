plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "nodomain.pacjo.wear.watchface.feature.editor"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    useLibrary("wear-sdk")      // TODO: what is this?

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":wear:jetpack:watchfaces:base"))
    api(libs.bundles.watchface.editor)      // TODO: keep this here?

    // TODO: check
    api(libs.bundles.jetpack.compose)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.wear.compose.material)
    api(libs.androidx.wear.compose.foundation)
    api(libs.androidx.wear.tooling.preview)
    api(libs.androidx.activity.compose)
    api(libs.androidx.core.splashscreen)
}