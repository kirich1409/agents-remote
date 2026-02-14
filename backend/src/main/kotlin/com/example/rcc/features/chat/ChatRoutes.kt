package com.example.rcc.features.chat

import com.example.rcc.features.chat.dto.CreateChatRequest
import com.example.rcc.features.chat.dto.ErrorResponse
import com.example.rcc.features.chat.dto.SendMessageRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.close
import org.koin.ktor.ext.inject

/**
 * Configures chat routes for the application.
 *
 * Registers all chat-related HTTP endpoints under the /api/chats path.
 * Requires ChatHandler to be available in the DI container.
 */
public fun Application.configureChatRoutes() {
    val chatHandler: ChatHandler by inject()

    routing {
        chatRoutes(chatHandler)
    }
}

/**
 * Configures chat WebSocket routes for the application.
 *
 * Registers WebSocket endpoint for real-time chat messaging.
 * Requires WebSocketHandler to be available in the DI container.
 */
public fun Application.configureChatWebSocketRoutes() {
    val webSocketHandler: WebSocketHandler by inject()

    routing {
        chatWebSocketRoutes(webSocketHandler)
    }
}

/**
 * Defines all chat API routes.
 *
 * Routes:
 * - GET /api/chats - Retrieve all chats
 * - POST /api/chats - Create a new chat
 * - DELETE /api/chats/{id} - Delete a chat
 * - POST /api/chats/{chatId}/messages - Send a message to a chat
 * - GET /api/chats/{chatId}/messages - Get messages in a chat (reserved for future)
 *
 * @param chatHandler The chat handler for processing requests.
 */
private fun Route.chatRoutes(chatHandler: ChatHandler) {
    route("/api/chats") {
        /**
         * GET /api/chats
         *
         * Retrieves all chats.
         *
         * Returns: 200 OK with list of ChatResponse objects
         */
        get {
            chatHandler.getChats()
                .onSuccess { chats ->
                    call.respond(HttpStatusCode.OK, chats)
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(
                            error = "Failed to retrieve chats",
                            details = error.message,
                        ),
                    )
                }
        }

        /**
         * POST /api/chats
         *
         * Creates a new chat.
         *
         * Request body: CreateChatRequest with sessionId
         * Returns: 201 Created with ChatResponse
         */
        post {
            val request = call.receive<CreateChatRequest>()
            chatHandler.createChat(request.sessionId)
                .onSuccess { chat ->
                    call.respond(HttpStatusCode.Created, chat)
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            error = "Failed to create chat",
                            details = error.message,
                        ),
                    )
                }
        }

        /**
         * DELETE /api/chats/{id}
         *
         * Deletes a chat by ID.
         *
         * Returns: 204 No Content on success
         */
        delete("{id}") {
            val chatId = call.parameters["id"] ?: ""
            // TODO: Implement deleteChat in ChatHandler and use case
            call.respond(HttpStatusCode.NoContent)
        }

        /**
         * POST /api/chats/{chatId}/messages
         *
         * Sends a message to a chat.
         *
         * Request body: SendMessageRequest with content
         * Returns: 200 OK with MessageResponse
         */
        route("{chatId}/messages") {
            post {
                val chatId = call.parameters["chatId"] ?: ""
                val request = call.receive<SendMessageRequest>()
                chatHandler.sendMessage(chatId, request.content)
                    .onSuccess { message ->
                        call.respond(HttpStatusCode.OK, message)
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(
                                error = "Failed to send message",
                                details = error.message,
                            ),
                        )
                    }
            }

            /**
             * GET /api/chats/{chatId}/messages
             *
             * Retrieves messages in a chat.
             *
             * Returns: 200 OK with list of MessageResponse objects
             */
            get {
                val chatId = call.parameters["chatId"] ?: ""
                // TODO: Implement getMessages in ChatHandler and use case
                call.respond(HttpStatusCode.OK, emptyList<String>())
            }
        }
    }
}

/**
 * Defines chat WebSocket routes.
 *
 * Routes:
 * - WS /ws/chats/{chatId} - WebSocket connection for real-time chat messaging
 *
 * @param webSocketHandler The WebSocket handler for managing connections.
 */
private fun Route.chatWebSocketRoutes(webSocketHandler: WebSocketHandler) {
    route("/ws/chats") {
        /**
         * WS /ws/chats/{chatId}
         *
         * Establishes a WebSocket connection for a chat.
         *
         * Operations:
         * - Subscribes the client to real-time messages for the chat
         * - Receives incoming events and broadcasts to all connected clients
         * - Unsubscribes when connection closes
         */
        webSocket("{chatId}") {
            val chatId = call.parameters["chatId"] ?: return@webSocket

            webSocketHandler.subscribe(chatId, this)

            try {
                for (message in incoming) {
                    val event = WebSocketEvent(
                        type = "message",
                        data = message.data.toString(Charsets.UTF_8),
                    )
                    webSocketHandler.broadcast(chatId, event)
                }
            } finally {
                webSocketHandler.unsubscribe(chatId, this)
                close()
            }
        }
    }
}
