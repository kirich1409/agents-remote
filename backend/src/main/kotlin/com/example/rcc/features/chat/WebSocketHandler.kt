package com.example.rcc.features.chat

import io.github.aakira.napier.Napier
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * Represents a WebSocket connection event.
 *
 * @property type The type of event (e.g., "message", "user_joined", "user_left").
 * @property data The event data payload.
 */
@Serializable
public data class WebSocketEvent(val type: String, val data: String)

/**
 * Handler for managing WebSocket connections and broadcasting events.
 *
 * Maintains a registry of active WebSocket connections grouped by chat ID.
 * Handles subscription, unsubscription, and broadcasting of real-time events.
 */
@Single
public class WebSocketHandler {
    private val connections: ConcurrentHashMap<String, MutableSet<WebSocketSession>> =
        ConcurrentHashMap()
    private val cleanupScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Subscribes a WebSocket session to a chat.
     *
     * Adds the session to the connections map for the specified chat ID.
     * If this is the first connection for a chat, creates a new set.
     *
     * @param chatId The chat identifier.
     * @param session The WebSocket session to subscribe.
     */
    public fun subscribe(chatId: String, session: WebSocketSession) {
        connections
            .computeIfAbsent(chatId) {
                Collections.newSetFromMap(ConcurrentHashMap())
            }.add(session)
    }

    /**
     * Unsubscribes a WebSocket session from a chat.
     *
     * Removes the session from the connections map for the specified chat ID.
     * If no sessions remain for the chat, removes the chat entry.
     *
     * @param chatId The chat identifier.
     * @param session The WebSocket session to unsubscribe.
     */
    public fun unsubscribe(chatId: String, session: WebSocketSession) {
        connections[chatId]?.let { sessions ->
            sessions.remove(session)
            if (sessions.isEmpty()) {
                connections.remove(chatId)
            }
        }
    }

    /**
     * Gets all active connections for a chat.
     *
     * @param chatId The chat identifier.
     * @return Set of WebSocket sessions connected to the chat.
     */
    public fun getConnections(chatId: String): Set<WebSocketSession> = connections[chatId]?.toSet().orEmpty()

    /**
     * Broadcasts a WebSocket event to all connections in a chat.
     *
     * Serializes the event to JSON and sends it to all active sessions.
     * Dead connections (send failures) are automatically removed.
     *
     * @param chatId The chat identifier to broadcast to.
     * @param event The event to broadcast.
     */
    public suspend fun broadcast(chatId: String, event: WebSocketEvent) {
        val snapshot = connections[chatId]?.toList() ?: return
        val json = Json.encodeToString(WebSocketEvent.serializer(), event)

        snapshot.forEach { session ->
            try {
                session.send(Frame.Text(json))
            } catch (e: Exception) {
                Napier.w("Failed to send message to session", e)
                // Асинхронный cleanup - не блокирует broadcast
                cleanupScope.launch {
                    try {
                        unsubscribe(chatId, session)
                    } catch (e: Exception) {
                        Napier.e("Failed to unsubscribe session", e)
                    }
                }
            }
        }
    }

    /**
     * Gets the count of active connections for a chat.
     *
     * @param chatId The chat identifier.
     * @return Number of active WebSocket sessions for the chat.
     */
    public fun getConnectionCount(chatId: String): Int = connections[chatId]?.size ?: 0

    /**
     * Clears all connections (useful for testing or shutdown).
     */
    public fun clearAllConnections() {
        connections.clear()
    }
}
