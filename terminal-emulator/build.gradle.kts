plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux.terminal"
    compileSdk = 34

    defaultConfig {
        minSdk = 27
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation("androidx.annotation:annotation:1.7.0")
    testImplementation("junit:junit:4.13.2")
}

