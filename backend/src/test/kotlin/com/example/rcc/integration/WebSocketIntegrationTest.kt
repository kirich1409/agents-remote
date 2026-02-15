package com.example.rcc.integration

import com.example.rcc.module
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.testing.testApplication
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlin.test.Test

/**
 * Integration tests for the WebSocket chat endpoint.
 *
 * Tests real-time messaging through the `/ws/chats/{chatId}` WebSocket endpoint.
 */
public class WebSocketIntegrationTest {
    /** Test: WebSocket connection can be established. */
    @Test
    public fun testWebSocketConnection() = testApplication {
        application { module() }
        val wsClient = createClient {
            install(WebSockets)
        }

        wsClient.webSocket("/ws/chats/test-chat") {
            send(Frame.Text("hello"))
            val response = incoming.receive() as Frame.Text
            val text = response.readText()
            text.contains("message") shouldBe true
        }
    }
}
