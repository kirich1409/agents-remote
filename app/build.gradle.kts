plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
}

kotlin {
    jvmToolchain(21)

    androidTarget()
    jvm("desktop")
    iosArm64()
    iosSimulatorArm64()

    // Strict explicit API mode for all targets
    explicitApi()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))

                implementation(libs.bundles.compose)
                implementation(libs.decompose)
                implementation(libs.decompose.compose)
                implementation(libs.bundles.mvikotlin)
                implementation(libs.essenty.lifecycle)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.napier)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.sqldelight.runtime)
                implementation(libs.bundles.ktor.client)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.android.driver)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.activity.compose)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
    }
}

detekt {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    baseline = file("${rootProject.projectDir}/detekt-baseline.xml")
}

android {
    namespace = "com.example.rcc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rcc"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

// Quality gates
tasks.named("build") {
    dependsOn("lintKotlin", "detekt")
}
