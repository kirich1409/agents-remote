package com.example.rcc.domain.repository

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message

/**
 * Repository interface for chat operations.
 *
 * Provides methods to manage chats and messages following the Repository pattern.
 * Implementations handle data persistence and retrieval.
 */
public interface ChatRepository {
    /**
     * Retrieves all chats.
     *
     * @return Result containing list of chats, or failure if operation failed.
     */
    public suspend fun getChats(): Result<List<Chat>>

    /**
     * Retrieves a chat by its ID.
     *
     * @param id Unique identifier of the chat.
     * @return Result containing the chat, or failure if not found or operation failed.
     */
    public suspend fun getChatById(id: String): Result<Chat>

    /**
     * Creates a new chat for a session.
     *
     * @param sessionId Cloud Code session identifier.
     * @return Result containing the created chat, or failure if operation failed.
     */
    public suspend fun createChat(sessionId: String): Result<Chat>

    /**
     * Deletes a chat by its ID.
     *
     * @param id Unique identifier of the chat to delete.
     * @return Result success or failure.
     */
    public suspend fun deleteChat(id: String): Result<Unit>

    /**
     * Sends a message to a chat.
     *
     * @param chatId ID of the chat to send message to.
     * @param content Text content of the message.
     * @return Result containing the created message, or failure if operation failed.
     */
    public suspend fun sendMessage(chatId: String, content: String): Result<Message>

    /**
     * Sends an assistant message to a chat.
     *
     * @param chatId ID of the chat to send message to.
     * @param content Text content of the assistant message.
     * @return Result containing the created message, or failure if operation failed.
     */
    public suspend fun sendAssistantMessage(chatId: String, content: String): Result<Message>

    /**
     * Retrieves messages from a chat with pagination.
     *
     * @param chatId ID of the chat.
     * @param limit Maximum number of messages to retrieve (default 100).
     * @param offset Number of messages to skip (default 0).
     * @return Result containing list of messages, or failure if operation failed.
     */
    public suspend fun getMessages(chatId: String, limit: Int = 100, offset: Int = 0): Result<List<Message>>
}
