plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "nodomain.pacjo.wear.watchface"
    compileSdk = 34

    defaultConfig {
        applicationId = "nodomain.pacjo.wear.watchface"
        minSdk = 26
        targetSdk = 34
        versionCode = 10
        versionName = "1.0"
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

    flavorDimensions += "default"
    productFlavors {
        create("simpledigital") {
            applicationIdSuffix = ".simpledigital"
        }
        create("digitalinformer") {
            applicationIdSuffix = ".digitalinformer"
        }
        create("alreadyLate") {
            applicationIdSuffix = ".alreadylate"
        }
    }

    sourceSets {
        getByName("simpledigital") {
            setRoot("src/simpledigital/src")
        }
        getByName("digitalinformer") {
            setRoot("src/digitalinformer/src")
        }
        getByName("alreadyLate") {
            setRoot("src/alreadylate/src")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // main watchface
    implementation("androidx.wear.watchface:watchface:1.2.1")

    // complications
    implementation("androidx.wear.watchface:watchface-complications-data-source:1.2.1")
    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.1")
    implementation("androidx.wear.watchface:watchface-complications-rendering:1.2.1")

    // watchface style and complication editor
    implementation("androidx.wear.watchface:watchface-editor:1.2.1")
    implementation("androidx.wear.watchface:watchface-client:1.2.1")

    // Jetpack Compose
    implementation("androidx.wear.compose:compose-foundation:1.3.0")
    implementation("androidx.wear.compose:compose-material:1.3.0")
    implementation("androidx.wear.compose:compose-navigation:1.3.0")

    implementation(kotlin("reflect"))

    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
}