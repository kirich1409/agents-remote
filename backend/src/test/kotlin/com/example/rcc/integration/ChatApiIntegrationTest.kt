package com.example.rcc.integration

import com.example.rcc.features.chat.dto.ChatResponse
import com.example.rcc.features.chat.dto.ErrorResponse
import com.example.rcc.module
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test

/**
 * Integration tests for the Chat REST API.
 *
 * Tests the full HTTP request/response cycle through Ktor test server.
 */
public class ChatApiIntegrationTest {
    /** Test: health endpoint returns OK. */
    @Test
    public fun testHealthEndpoint() = testApplication {
        application { module() }
        val response = client.get("/health")
        response.status shouldBe HttpStatusCode.OK
    }

    /** Test: create chat returns created status with correct data. */
    @Test
    public fun testCreateChat() = testApplication {
        application { module() }
        val jsonClient = createClient {
            install(ContentNegotiation) { json() }
        }

        val createResponse = jsonClient.post("/api/chats") {
            contentType(ContentType.Application.Json)
            setBody("""{"sessionId":"test-session"}""")
        }
        createResponse.status shouldBe HttpStatusCode.Created
        val created = createResponse.body<ChatResponse>()
        created.sessionId shouldBe "test-session"
        created.id shouldNotBe null
    }

    /** Test: list chats returns OK with empty list (stub repository). */
    @Test
    public fun testListChats() = testApplication {
        application { module() }
        val jsonClient = createClient {
            install(ContentNegotiation) { json() }
        }

        val listResponse = jsonClient.get("/api/chats")
        listResponse.status shouldBe HttpStatusCode.OK
        val chats = listResponse.body<List<ChatResponse>>()
        chats shouldBe emptyList()
    }

    /** Test: create chat with blank session ID returns error. */
    @Test
    public fun testCreateChatWithBlankSessionId() = testApplication {
        application { module() }
        val jsonClient = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = jsonClient.post("/api/chats") {
            contentType(ContentType.Application.Json)
            setBody("""{"sessionId":""}""")
        }
        response.status shouldBe HttpStatusCode.BadRequest
    }

    /** Test: delete chat returns not implemented. */
    @Test
    public fun testDeleteChatNotImplemented() = testApplication {
        application { module() }
        val jsonClient = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = jsonClient.delete("/api/chats/some-id")
        response.status shouldBe HttpStatusCode.NotImplemented
        val error = response.body<ErrorResponse>()
        error.error shouldContain "not implemented"
    }

    /** Test: get messages returns not implemented. */
    @Test
    public fun testGetMessagesNotImplemented() = testApplication {
        application { module() }
        val jsonClient = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = jsonClient.get("/api/chats/some-id/messages")
        response.status shouldBe HttpStatusCode.NotImplemented
    }
}
