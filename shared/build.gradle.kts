plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("app.cash.sqldelight")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
}

kotlin {
    jvmToolchain(21)

    androidLibrary {
        namespace = "com.example.rcc.shared"
        compileSdk = 36
        minSdk = 24
    }

    jvm("backend")
    // iOS targets временно отключены: SQLDelight 2.2.1 несовместим с Kotlin 2.3.10
    // TODO: Включить когда выйдет совместимая версия SQLDelight
    // iosArm64()
    // iosSimulatorArm64()

    // Strict explicit API mode for all targets
    explicitApi()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.runtime)
            implementation(libs.koin.core)
            implementation(libs.napier)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.websockets)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.assertions)
            implementation(libs.mockk)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
            implementation(libs.ktor.client.okhttp)
        }

        getByName("backendMain").dependencies {
            implementation(libs.sqldelight.jdbc.driver)
        }

        // iOS source set временно отключён
        // iosMain.dependencies {
        //     implementation(libs.sqldelight.native.driver)
        //     implementation(libs.ktor.client.darwin)
        // }
    }
}

sqldelight {
    databases {
        create("RemoteCloudCodeDb") {
            packageName.set("com.example.rcc.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight"))
        }
    }
}

detekt {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    baseline = file("${rootProject.projectDir}/detekt-baseline.xml")
}

// Linting tasks
tasks.named("build") {
    dependsOn("lintKotlin", "detekt")
}
