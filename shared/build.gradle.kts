@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("app.cash.sqldelight")
    //id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}

android {
    namespace = "com.delerium.tremens.common"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

version = "1.2"

kotlin {
    @Suppress("OPT_IN_USAGE")
    targetHierarchy.default()
    androidTarget()
    ios()
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.sqlDelight.coroutinesExt)
                implementation(libs.multiplatformSettings.common)
                implementation(libs.kotlinx.dateTime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material)
                implementation(libs.compose.runtime)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.shared.commonTest)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.sqlDelight.android)

            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.bundles.shared.androidTest)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.sqlDelight.native)
            }
        }
    }

    cocoapods {
        framework {
            //isStatic = false // SwiftUI preview requires dynamic framework
            linkerOpts("-lsqlite3")
        }
        podfile = project.file("../iosApp/Podfile")
    }
}

//Mockative insanity
//dependencies {
//    configurations
//        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
//        .forEach {
//            add(it.name, libs.mockative.processor)
//        }
//}

sqldelight {
    databases.create("HabitDatabase") {
        packageName.set("tremens.database")
    }
}