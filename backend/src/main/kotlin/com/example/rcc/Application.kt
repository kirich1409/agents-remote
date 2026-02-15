package com.example.rcc

import com.example.rcc.di.appModule
import com.example.rcc.features.chat.configureChatWebSocketRoutes
import com.example.rcc.plugins.configureRouting
import com.example.rcc.plugins.configureSerialization
import com.example.rcc.plugins.configureWebSockets
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.koin.core.context.startKoin
import org.koin.ktor.plugin.Koin

/**
 * Application entry point.
 *
 * Initializes Koin DI container and starts the Ktor server on port 3000.
 */
public fun main() {
    startKoin {
        modules(appModule)
    }

    embeddedServer(CIO, port = 3000, host = "0.0.0.0") {
        configureSerialization()
        configureWebSockets()
        configureRouting()
        configureChatWebSocketRoutes()
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
        modules(appModule)
    }
    configureSerialization()
    configureWebSockets()
    configureRouting()
    configureChatWebSocketRoutes()
}
