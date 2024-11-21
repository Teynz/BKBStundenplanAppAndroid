plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.compose.compiler)

}

android {
    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("mozilla/public-suffix-list.txt")
    }
    namespace = "bkb.stundenplan.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "bkb.stundenplan.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 7
        versionName = "@string/app_Version"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    //implementation(libs.androidx.runner) removed 18.11.24
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    // androidTestImplementation(libs.androidx.espresso.core) removed 18.11.24 typically used for ui testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.webkit)
    implementation(libs.skrapeit)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // LiveData
    //implementation(libs.androidx.lifecycle.livedata.ktx) removed 18.11.24
    // Lifecycle utilities for Compose
    implementation(libs.lifecycle.runtime.compose)

    // Saved state module for ViewModel
    //implementation(libs.androidx.lifecycle.viewmodel.savedstate) removed 18.11.24

    // Annotation processor
    //kapt(libs.androidx.lifecycle.compiler) removed 18.11.24
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    //implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")


}