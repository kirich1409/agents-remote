plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("app.cash.sqldelight")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    jvmToolchain(21)

    androidTarget()
    jvm("backend")
    iosArm64()
    iosSimulatorArm64()

    // Strict explicit API mode for all targets
    explicitApi()

    targets.configureEach {
        compilations.configureEach {
            if (name == "test") {
                compileTaskProvider.configure {
                    compilerOptions {
                        freeCompilerArgs.add("-Xno-explicit-api")
                    }
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
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
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.android.driver)
                implementation(libs.ktor.client.okhttp)
            }
        }

        val backendMain by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
        }

        val backendTest by getting {
            dependencies {
                implementation(libs.kotest.assertions)
                implementation(libs.mockk)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotest.assertions)
                implementation(libs.mockk)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.sqldelight.native.driver)
                implementation(libs.ktor.client.darwin)
            }
        }

        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
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

android {
    namespace = "com.example.rcc.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
}

// Linting tasks
tasks.named("build") {
    dependsOn("detekt")
}
