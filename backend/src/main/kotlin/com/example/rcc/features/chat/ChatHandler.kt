package com.example.rcc.features.chat

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import com.example.rcc.features.chat.dto.ChatResponse
import com.example.rcc.features.chat.dto.MessageResponse

/**
 * Handler for chat operations.
 *
 * Provides methods to handle chat CRUD operations and messaging by delegating
 * to the appropriate use cases.
 *
 * @property getChatsUseCase Use case for retrieving all chats.
 * @property createChatUseCase Use case for creating a new chat.
 * @property sendMessageUseCase Use case for sending messages to a chat.
 */
public class ChatHandler(
    private val getChatsUseCase: GetChatsUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
) {
    /**
     * Retrieves all chats.
     *
     * @return Result containing list of chat responses, or failure if operation failed.
     */
    public suspend fun getChats(): Result<List<ChatResponse>> =
        getChatsUseCase().mapCatching { chats ->
            chats.map { it.toResponse() }
        }

    /**
     * Creates a new chat for a session.
     *
     * @param sessionId Cloud Code session identifier.
     * @return Result containing the created chat response, or failure if validation fails or operation failed.
     */
    public suspend fun createChat(sessionId: String): Result<ChatResponse> =
        createChatUseCase(sessionId).mapCatching { chat ->
            chat.toResponse()
        }

    /**
     * Sends a message to a chat.
     *
     * @param chatId ID of the chat to send message to.
     * @param content Text content of the message.
     * @return Result containing the created message response, or failure if validation fails or operation failed.
     */
    public suspend fun sendMessage(
        chatId: String,
        content: String,
    ): Result<MessageResponse> =
        sendMessageUseCase(chatId, content).mapCatching { message ->
            message.toResponse()
        }

    /**
     * Converts a Chat entity to a ChatResponse DTO.
     */
    private fun Chat.toResponse(): ChatResponse =
        ChatResponse(
            id = id,
            sessionId = sessionId,
            createdAt = createdAt,
            lastActivity = lastActivity,
            title = title,
        )

    /**
     * Converts a Message entity to a MessageResponse DTO.
     */
    private fun Message.toResponse(): MessageResponse =
        MessageResponse(
            id = id,
            chatId = chatId,
            role = role.name,
            content = content,
            timestamp = timestamp,
        )
}
