package com.example.rcc

import com.example.rcc.di.appModule
import com.example.rcc.plugins.configureRouting
import com.example.rcc.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.koin.core.context.startKoin

/**
 * Application entry point.
 *
 * Starts the Ktor server on port 3000.
 */
public fun main() {
    startKoin {
        modules(appModule)
    }

    embeddedServer(CIO, port = 3000, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}

/**
 * Application module configuration.
 *
 * Configures all server plugins.
 */
public fun Application.module() {
    configureSerialization()
    configureRouting()
}
