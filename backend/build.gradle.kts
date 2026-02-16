plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    alias(libs.plugins.koin.compiler)
    id("io.gitlab.arturbosch.detekt")
    alias(libs.plugins.kover)
}

application {
    mainClass.set("com.example.rcc.ApplicationKt")
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.bundles.ktor.server)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.sqldelight.runtime)
    implementation(libs.sqldelight.jdbc.driver)
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.koin.ktor)
    implementation(libs.logback.classic)
    implementation(libs.napier)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.caffeine)

    // Testing
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.websockets)
    testImplementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}

detekt {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    baseline = file("${rootProject.projectDir}/detekt-baseline.xml")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn("test")
}

dependencies {
    kover(project(":shared"))
}

kover.reports {
    filters {
        excludes.classes(
            "com.example.rcc.config.*",
            "com.example.rcc.plugins.*",
            "com.example.rcc.ApplicationKt",
            "*Dto",
            "*Config",
            "*Module",
            "*Kt"
        )
        excludes.annotatedBy("*Generated*")
        includes.classes("com.example.rcc.*")
    }
}
