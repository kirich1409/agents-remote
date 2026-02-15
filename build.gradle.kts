plugins {
    kotlin("multiplatform") version "2.3.10" apply false
    kotlin("jvm") version "2.3.10" apply false
    kotlin("plugin.serialization") version "2.3.10" apply false
    kotlin("plugin.compose") version "2.3.10" apply false
    id("org.jetbrains.compose") version "1.10.1" apply false
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("com.google.devtools.ksp") version "2.3.5" apply false
    id("app.cash.sqldelight") version "2.2.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
    id("com.diffplug.spotless") version "8.2.1"
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", ".worktrees/**")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            ),
        )
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**", ".worktrees/**")
        ktlint()
    }
}

allprojects {
    // Global Kotlin compiler options for strict code quality
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        val isTestTask = name.contains("Test", ignoreCase = true)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            allWarningsAsErrors.set(true)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
            // Apply explicit API mode only to non-test compilations
            if (!isTestTask) {
                freeCompilerArgs.add("-Xexplicit-api=strict")
            }
        }
    }

    // Ensure tests include quality checks
    tasks.withType<Test> {
        finalizedBy(rootProject.tasks.named("codeQualityCheck"))
    }
}

tasks.register("detekt") {
    dependsOn(":shared:detekt")
    dependsOn(":backend:detekt")
    dependsOn(":app:detekt")
    description = "Run detekt static analysis on all modules"
}

// Root quality check aggregation
tasks.register("codeQualityCheck") {
    dependsOn("detekt")
    dependsOn("spotlessCheck")
    description = "Run all code quality checks"
}
