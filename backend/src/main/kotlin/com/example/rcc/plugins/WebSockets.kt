package com.example.rcc.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import kotlin.time.Duration.Companion.seconds

private const val WS_TIMEOUT_MILLIS = 15_000L

/**
 * Configures WebSocket support for the application.
 *
 * Enables real-time bidirectional communication between client and server.
 * Sets timeout and ping interval for connection stability.
 */
public fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeoutMillis = WS_TIMEOUT_MILLIS
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}
