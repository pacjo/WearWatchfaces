plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "nodomain.pacjo.wear.watchface.feature.base"
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
}

dependencies {
    api(libs.androidx.watchface.client)
    api(project(":wear:jetpack:feature:rendering"))

    api(platform(libs.koin.bom))
    api(libs.koin.android)
    api(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
}