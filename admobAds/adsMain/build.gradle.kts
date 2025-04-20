plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "video.downloader.adsmain"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        multiDexEnabled = true
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.bundles.ads.related)
    implementation(project(":admobAds:appOpen"))
    implementation(project(":admobAds:bannerAds"))
    implementation(project(":admobAds:core"))
    implementation(project(":admobAds:interstitials"))
    implementation(project(":admobAds:nativeAds"))
    implementation(project(":admobAds:rewadedAd"))
    implementation(project(":admobAds:rewardedInterAds"))
}

afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.appsapiconsole"
                artifactId = "adsMainn"
                version = "2.1.6"
            }
        }
    }
}