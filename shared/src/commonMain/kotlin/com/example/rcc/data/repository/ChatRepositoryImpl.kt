package com.example.rcc.data.repository

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.repository.ChatRepository

/**
 * Implementation stub of ChatRepository.
 *
 * Provides basic in-memory functionality for MVP.
 * Will be replaced with proper persistence in later phases.
 */
public class ChatRepositoryImpl : ChatRepository {
    /**
     * Retrieves all chats.
     *
     * @return Empty list (stub implementation).
     */
    override suspend fun getChats(): Result<List<Chat>> = Result.success(emptyList())

    /**
     * Retrieves a chat by ID.
     *
     * @param id Chat identifier.
     * @return Failure (not implemented).
     */
    override suspend fun getChatById(id: String): Result<Chat> = Result.failure(Exception("Not implemented"))

    /**
     * Creates a new chat.
     *
     * @param sessionId Session identifier.
     * @return New Chat with the provided session ID.
     */
    override suspend fun createChat(sessionId: String): Result<Chat> = Result.success(Chat(sessionId = sessionId))

    /**
     * Deletes a chat.
     *
     * @param id Chat identifier to delete.
     * @return Success (stub implementation).
     */
    override suspend fun deleteChat(id: String): Result<Unit> = Result.success(Unit)

    /**
     * Sends a message.
     *
     * @param chatId Chat identifier.
     * @param content Message content.
     * @return Failure (not implemented).
     */
    override suspend fun sendMessage(chatId: String, content: String): Result<Message> =
        Result.failure(Exception("Not implemented"))

    /**
     * Retrieves messages.
     *
     * @param chatId Chat identifier.
     * @param limit Maximum messages.
     * @param offset Offset for pagination.
     * @return Empty list (stub implementation).
     */
    override suspend fun getMessages(chatId: String, limit: Int, offset: Int): Result<List<Message>> =
        Result.success(emptyList())
}
