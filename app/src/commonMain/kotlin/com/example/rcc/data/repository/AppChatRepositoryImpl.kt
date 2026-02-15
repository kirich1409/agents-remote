package com.example.rcc.data.repository

import com.example.rcc.data.api.RccApiClient
import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.repository.ChatRepository

internal class AppChatRepositoryImpl(private val apiClient: RccApiClient) : ChatRepository {
    override suspend fun getChats(): Result<List<Chat>> = runCatching { apiClient.getChats() }

    override suspend fun getChatById(id: String): Result<Chat> =
        runCatching { apiClient.getChats().first { it.id == id } }

    override suspend fun createChat(sessionId: String): Result<Chat> = runCatching { apiClient.createChat(sessionId) }

    override suspend fun deleteChat(id: String): Result<Unit> = runCatching { apiClient.deleteChat(id) }

    override suspend fun sendMessage(chatId: String, content: String): Result<Message> =
        runCatching { apiClient.sendMessage(chatId, content) }

    override suspend fun sendAssistantMessage(chatId: String, content: String): Result<Message> =
        runCatching { apiClient.sendMessage(chatId, content) }

    override suspend fun getMessages(chatId: String, limit: Int, offset: Int): Result<List<Message>> =
        runCatching { apiClient.getMessages(chatId) }
}
