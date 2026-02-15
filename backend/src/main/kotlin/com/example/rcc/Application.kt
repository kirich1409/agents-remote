package com.example.rcc

import com.example.rcc.config.AppConfig
import com.example.rcc.di.BackendModule
import com.example.rcc.features.chat.configureChatWebSocketRoutes
import com.example.rcc.plugins.configureRouting
import com.example.rcc.plugins.configureSerialization
import com.example.rcc.plugins.configureWebSockets
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.koin.core.annotation.KoinApplication
import org.koin.ktor.plugin.Koin
import org.koin.plugin.module.dsl.withConfiguration

/**
 * Koin application configuration for the backend.
 */
@KoinApplication(modules = [BackendModule::class])
public class KoinBackendApplication

/**
 * Application entry point.
 *
 * Starts the Ktor server using configuration from [AppConfig].
 */
public fun main() {
    embeddedServer(CIO, port = AppConfig.gatewayPort, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

/**
 * Application module configuration.
 *
 * Configures all server plugins including:
 * - Koin dependency injection
 * - Serialization for JSON handling
 * - WebSocket support for real-time communication
 * - HTTP routing and endpoints
 */
public fun Application.module() {
    install(Koin) {
        withConfiguration<KoinBackendApplication>()
    }
    configureSerialization()
    configureWebSockets()
    configureRouting()
    configureChatWebSocketRoutes()
}
