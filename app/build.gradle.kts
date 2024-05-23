plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    //alias(libs.plugins.androidLibrary)
    kotlin("kapt")
    //alias(libs.plugins.daggerHilt)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.baseproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.baseproject"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField ("String", "BASE_URL", "\"paste_your_base_url_here/\"")
        buildConfigField ("String", "APPLICATION_ID", "\"com.example.baseproject\"")
    }

    buildFeatures {
        buildConfig = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //Hilt
    implementation(libs.dagger.hilt)
    kapt(libs.hilt.compiler)

    //Retrofit
    implementation (libs.retrofit)
    implementation(libs.gson.converter)
    implementation (libs.converter.scalar)
    //coroutines
    implementation (libs.coroutine.core)
    implementation (libs.coroutine.android)
    // ViewModel
    implementation (libs.viewmodel)
    // Lifecycles only (without ViewModel or LiveData)
    implementation(libs.viewmodel.runtime)
    // Saved state module for ViewModel
//        implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.2")
    // LiveData
    //Interceptor
    implementation (libs.interceptor)
    implementation (libs.live.data)
}