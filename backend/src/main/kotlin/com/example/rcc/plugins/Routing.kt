package com.example.rcc.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Configures HTTP routing for the application.
 *
 * Defines all API endpoints and routes.
 */
public fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respondText("OK")
        }

        // TODO: Add feature routes here
    }
}
