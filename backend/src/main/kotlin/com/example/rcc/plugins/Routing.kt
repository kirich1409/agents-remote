package com.example.rcc.plugins

import com.example.rcc.features.chat.configureChatRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Configures HTTP routing for the application.
 *
 * Registers all API endpoints including:
 * - Health check endpoint
 * - Chat feature routes
 */
public fun Application.configureRouting() {
    routing {
        /**
         * GET /health
         *
         * Health check endpoint to verify server is running.
         * Returns: 200 OK with "OK" text response.
         */
        get("/health") {
            call.respondText("OK")
        }
    }

    /**
     * Configure chat-related routes.
     *
     * Registers all chat API endpoints under /api/chats path.
     * Requires ChatHandler to be available in the DI container.
     */
    configureChatRoutes()
}
