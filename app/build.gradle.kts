plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
}

kotlin {
    jvmToolchain(21)

    androidLibrary {
        namespace = "com.example.rcc.app"
        compileSdk = 36
        minSdk = 24
        androidResources {
            enable = true
        }
    }

    jvm("desktop")
    // iOS targets временно отключены (SQLDelight несовместим с Kotlin 2.3.10)
    // iosArm64()
    // iosSimulatorArm64()

    // Strict explicit API mode for all targets
    explicitApi()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))

            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
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

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
            implementation(libs.ktor.client.okhttp)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.sqldelight.sqlite.driver)
        }
    }
}

detekt {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    baseline = file("${rootProject.projectDir}/detekt-baseline.xml")
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
