plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.bundles.ktor.server)
    implementation(libs.sqldelight.runtime)
    implementation(libs.sqldelight.jdbc.driver)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.logback.classic)
    implementation(libs.koin.annotations)
    implementation(libs.napier)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}

detekt {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    baseline = file("${rootProject.projectDir}/detekt-baseline.xml")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("detekt")
}

// Quality gates
tasks.named("build") {
    dependsOn("detekt", "test")
}
