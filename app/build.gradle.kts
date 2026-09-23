import java.io.File
import java.net.HttpURLConnection
import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.athar.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.athar.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.0-beta.2"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    // Prayer times — Batoulapps Adhan (MIT). Credited in README.
    implementation("com.batoulapps.adhan:adhan:1.2.1")
    // One-tap location (fresh fix + enable-location dialog) — Google Play Services (free SDK).
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // AndroidSvg — renders Mushaf SVG pages directly to Canvas (no WebView needed)
    implementation("com.caverock:androidsvg-aar:1.4")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// One-time task: downloads the full Uthmani Quran (114 chapters) from the
// Quran.com API (Quran Foundation, Madani mushaf — free, no key) into
// src/main/assets/quran so the reader works fully offline.
// Run once on a machine with internet:  ./gradlew downloadQuran
// Chapters already on disk are skipped. Safe to re-run any time.
tasks.register("downloadQuran") {
    group = "athar"
    description = "Downloads full Quran chapters into src/main/assets/quran (run once)."
    doLast {
        val outDir = file("src/main/assets/quran")
        outDir.mkdirs()
        var ok = 0
        val failed = mutableListOf<Int>()
        for (n in 1..114) {
            val out = File(outDir, "ch$n.json")
            if (out.exists() && out.length() > 0) {
                ok++
                continue
            }
            try {
                val url = URL(
                    "https://api.quran.com/api/v4/quran/verses/uthmani?chapter_number=$n&per_page=300"
                )
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 25000
                conn.readTimeout = 25000
                conn.setRequestProperty("Accept", "application/json")
                if (conn.responseCode == 200) {
                    out.writeText(conn.inputStream.bufferedReader().readText())
                    ok++
                    println("quran: saved chapter $n")
                } else {
                    failed.add(n)
                    println("quran: FAILED chapter $n HTTP ${conn.responseCode}")
                }
                conn.disconnect()
            } catch (e: Exception) {
                failed.add(n)
                println("quran: FAILED chapter $n (${e.message})")
            }
        }
        println("quran: done — $ok/114 chapters on disk" + if (failed.isEmpty()) "" else ", retry: ${failed.joinToString()}")
    }
}