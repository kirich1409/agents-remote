plugins {
    kotlin("multiplatform") version "2.3.20-Beta2" apply false
    kotlin("plugin.serialization") version "2.3.20-Beta2" apply false
    id("org.jetbrains.compose") version "1.10.1" apply false
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("com.google.devtools.ksp") version "2.3.20-Beta2-1.0.17" apply false
    id("app.cash.sqldelight") version "2.0.2" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.6" apply false
    id("org.jmailen.gradle.kotlinter") version "4.1.1" apply false
}

allprojects {
    // Global Kotlin compiler options for strict code quality
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            allWarningsAsErrors.set(true)
            freeCompilerArgs.addAll(
                "-Xexplicit-api=strict",  // Require explicit API declarations
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }
}
