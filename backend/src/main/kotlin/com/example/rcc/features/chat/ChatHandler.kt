package com.example.rcc.features.chat

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import com.example.rcc.features.chat.dto.ChatResponse
import com.example.rcc.features.chat.dto.MessageResponse
import com.example.rcc.service.ClaudeCodeService
import io.github.aakira.napier.Napier
import org.koin.core.annotation.Single

/**
 * Handler for chat operations.
 *
 * Provides methods to handle chat CRUD operations and messaging by delegating
 * to the appropriate use cases.
 *
 * @param chatRepository Repository for chat data access.
 * @param getChatsUseCase Use case for retrieving all chats.
 * @param createChatUseCase Use case for creating a new chat.
 * @param sendMessageUseCase Use case for sending messages to a chat.
 * @param claudeCodeService Service for interacting with Claude Code CLI.
 * @param webSocketHandler Handler for WebSocket broadcasting.
 */
@Single
public class ChatHandler(
    private val chatRepository: ChatRepository,
    private val getChatsUseCase: GetChatsUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val claudeCodeService: ClaudeCodeService,
    private val webSocketHandler: WebSocketHandler,
) {
    /**
     * Retrieves all chats.
     *
     * @return Result containing list of chat responses, or failure if operation failed.
     */
    public suspend fun getChats(): Result<List<ChatResponse>> = getChatsUseCase().mapCatching { chats ->
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
    public suspend fun sendMessage(chatId: String, content: String): Result<MessageResponse> = runCatching {
        // 1. Save user message
        sendMessageUseCase(chatId, content).getOrThrow()

        // 2. Call Claude CLI (use chatId as session ID — it's already a UUID)
        val response = claudeCodeService.sendMessage(chatId, content).getOrThrow()

        // 3. Save assistant message
        val assistantMessage = chatRepository.sendAssistantMessage(chatId, response).getOrThrow()

        // 4. Broadcast via WebSocket
        val event = WebSocketEvent(type = "message", data = assistantMessage.content)
        try {
            webSocketHandler.broadcast(chatId, event)
        } catch (e: Exception) {
            Napier.w("Failed to broadcast WebSocket event", e)
        }

        // 5. Return assistant message
        assistantMessage.toResponse()
    }

    /**
     * Deletes a chat by ID.
     *
     * @param chatId ID of the chat to delete.
     * @return Result success or failure.
     */
    public suspend fun deleteChat(chatId: String): Result<Unit> = chatRepository.deleteChat(chatId)

    /**
     * Retrieves messages for a chat.
     *
     * @param chatId ID of the chat.
     * @return Result containing list of message responses.
     */
    public suspend fun getMessages(chatId: String): Result<List<MessageResponse>> =
        chatRepository.getMessages(chatId).mapCatching { msgs ->
            msgs.map { it.toResponse() }
        }

    private fun Chat.toResponse(): ChatResponse = ChatResponse(
        id = id,
        sessionId = sessionId,
        createdAt = createdAt,
        lastActivity = lastActivity,
        title = title,
    )

    private fun Message.toResponse(): MessageResponse = MessageResponse(
        id = id,
        chatId = chatId,
        role = role.name,
        content = content,
        timestamp = timestamp,
    )
}
