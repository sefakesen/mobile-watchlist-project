plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.mobiluygulamafinal"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.mobiluygulamafinal"
        minSdk = 24
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
// İnternetten veri çekmek için Volley
    implementation("com.android.volley:volley:1.2.1")

// İnternetteki resim linklerini ImageView'a yansıtmak için Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

// Firebase BOM (Tüm Firebase kütüphanelerinin sürümlerini uyumlu tutar)
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

// Firebase Authentication (Giriş / Kayıt işlemleri için)
    implementation("com.google.firebase:firebase-auth")

// Firebase Realtime Database (Listeleri kaydetmek ve okumak için)
    implementation("com.google.firebase:firebase-database")
}