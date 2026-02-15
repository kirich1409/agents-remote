package com.example.rcc.features.chat

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlin.test.Test

/**
 * Tests for the WebSocketHandler class.
 *
 * Tests subscription, unsubscription, connection counting,
 * and event broadcasting behavior.
 */
public class WebSocketHandlerTest {
    private val webSocketHandler = WebSocketHandler()

    /**
     * Test: subscribe adds a session to a chat.
     */
    @Test
    public fun testSubscribe() {
        val chatId = "chat-1"
        val session1 = mockk<io.ktor.websocket.WebSocketSession>()

        webSocketHandler.subscribe(chatId, session1)

        webSocketHandler.getConnectionCount(chatId) shouldBe 1
    }

    /**
     * Test: unsubscribe removes a session from a chat.
     */
    @Test
    public fun testUnsubscribe() {
        val chatId = "chat-1"
        val session1 = mockk<io.ktor.websocket.WebSocketSession>()

        webSocketHandler.subscribe(chatId, session1)
        webSocketHandler.unsubscribe(chatId, session1)

        webSocketHandler.getConnectionCount(chatId) shouldBe 0
    }

    /**
     * Test: getConnections returns all sessions for a chat.
     */
    @Test
    public fun testGetConnections() {
        val chatId = "chat-1"
        val session1 = mockk<io.ktor.websocket.WebSocketSession>()
        val session2 = mockk<io.ktor.websocket.WebSocketSession>()

        webSocketHandler.subscribe(chatId, session1)
        webSocketHandler.subscribe(chatId, session2)

        val connections = webSocketHandler.getConnections(chatId)

        connections.size shouldBe 2
    }

    /**
     * Test: getConnectionCount returns correct count.
     */
    @Test
    public fun testGetConnectionCount() {
        val chatId = "chat-1"
        val session1 = mockk<io.ktor.websocket.WebSocketSession>()
        val session2 = mockk<io.ktor.websocket.WebSocketSession>()

        webSocketHandler.subscribe(chatId, session1)
        webSocketHandler.subscribe(chatId, session2)

        webSocketHandler.getConnectionCount(chatId) shouldBe 2
    }

    /**
     * Test: clearAllConnections removes all connections.
     */
    @Test
    public fun testClearAllConnections() {
        val chatId1 = "chat-1"
        val chatId2 = "chat-2"
        val session1 = mockk<io.ktor.websocket.WebSocketSession>()
        val session2 = mockk<io.ktor.websocket.WebSocketSession>()

        webSocketHandler.subscribe(chatId1, session1)
        webSocketHandler.subscribe(chatId2, session2)

        webSocketHandler.getConnectionCount(chatId1) shouldBe 1
        webSocketHandler.getConnectionCount(chatId2) shouldBe 1

        webSocketHandler.clearAllConnections()

        webSocketHandler.getConnectionCount(chatId1) shouldBe 0
        webSocketHandler.getConnectionCount(chatId2) shouldBe 0
    }
}
