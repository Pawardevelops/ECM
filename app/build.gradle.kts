plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mec"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mec"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.4.0")) // Firebase BOM for version management
    implementation("com.google.firebase:firebase-auth") // Use BOM for auth
    implementation("com.google.firebase:firebase-firestore") // Use BOM for Firestore
    implementation("com.google.firebase:firebase-analytics") // If needed

    // Google Play services
    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation(libs.firebase.database) // For Google Sign-In, if needed

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
