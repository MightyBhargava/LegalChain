plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // REQUIRED for Kotlin 2.0+ (this removes your error)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

android {
    namespace = "com.example.legalchain"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.legalchain"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        // Compose compiler extension compatible with UI 1.4.x (safe)
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose UI (stable + compatible)
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.ui:ui-graphics:1.5.4")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")

    // Material 3
    implementation("androidx.compose.material3:material3:1.2.1")

    // Material Icons Extended (ONLY this one)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Optional: Accompanist Navigation Animation
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")
    implementation("androidx.datastore:datastore-preferences:1.1.0")
    // Testing & debug
    implementation("androidx.datastore:datastore-preferences:1.1.0")
    implementation("androidx.compose.material:material-icons-extended")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
}
