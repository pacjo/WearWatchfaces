plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
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
        debug {
            applicationIdSuffix = ".debug"
        }
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
        create("simpleDigital") {
            applicationIdSuffix = ".simpleDigital"
        }
        create("digitalInformer") {
            applicationIdSuffix = ".digitalInformer"
        }
        create("alreadyLate") {
            applicationIdSuffix = ".alreadyLate"
        }
        create("simpleSnoopy") {
            applicationIdSuffix = ".simpleSnoopy"
        }
    }

    // to reduce code duplications we have additional source directories for specific features
    // like complications or backgrounds. Most of the code needed to implement them is added
    // to a productFlavour by specifying those directories in `java.srcDirs`
    sourceSets {
        val complicationsDir = "src/opt/complications/java"
        val handsDir = "src/opt/hands/java"
        val backgroundsDir = "src/opt/backgrounds/java"

        getByName("simpleDigital") {
            setRoot("src/simpleDigital/src")

            java.srcDirs(
                complicationsDir
            )
        }
        getByName("digitalInformer") {
            setRoot("src/digitalInformer/src")

            java.srcDirs(
                complicationsDir
            )
        }
        getByName("alreadyLate") {
            setRoot("src/alreadyLate/src")

            java.srcDirs(
                complicationsDir,
                handsDir,
                backgroundsDir
            )
        }
        getByName("simpleSnoopy") {
            setRoot("src/simpleSnoopy/src")

            java.srcDirs(
                handsDir,
                backgroundsDir
            )
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
    val watchfaceVersion = "1.3.0-alpha04"

    // main watchface
    implementation("androidx.wear.watchface:watchface:$watchfaceVersion")

    // complications
    implementation("androidx.wear.watchface:watchface-complications-data-source:$watchfaceVersion")
    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:$watchfaceVersion")
    implementation("androidx.wear.watchface:watchface-complications-rendering:$watchfaceVersion")

    // watchface style and complication editor
    implementation("androidx.wear.watchface:watchface-editor:$watchfaceVersion")
    implementation("androidx.wear.watchface:watchface-client:$watchfaceVersion")

    // Jetpack Compose
    implementation("androidx.wear.compose:compose-foundation:1.4.0")
    implementation("androidx.wear.compose:compose-material:1.4.0")
    implementation("androidx.wear.compose:compose-navigation:1.4.0")

    // others
    implementation("androidx.vectordrawable:vectordrawable:1.2.0")
    implementation(kotlin("reflect"))
}