package com.example.rcc.data.api

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateChatRequest(val sessionId: String)

@Serializable
internal data class SendMessageRequest(val content: String)

internal class RccApiClient(private val httpClient: HttpClient, private val baseUrl: String) {
    suspend fun getChats(): List<Chat> = httpClient.get("$baseUrl/api/chats").body()

    suspend fun createChat(sessionId: String): Chat = httpClient
        .post("$baseUrl/api/chats") {
            contentType(ContentType.Application.Json)
            setBody(CreateChatRequest(sessionId))
        }.body()

    suspend fun deleteChat(id: String) {
        httpClient.delete("$baseUrl/api/chats/$id")
    }

    suspend fun sendMessage(chatId: String, content: String): Message = httpClient
        .post("$baseUrl/api/chats/$chatId/messages") {
            contentType(ContentType.Application.Json)
            setBody(SendMessageRequest(content))
        }.body()

    suspend fun getMessages(chatId: String): List<Message> =
        httpClient.get("$baseUrl/api/chats/$chatId/messages").body()
}
