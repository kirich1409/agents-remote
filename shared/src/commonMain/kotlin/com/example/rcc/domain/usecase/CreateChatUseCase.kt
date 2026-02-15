package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.error.ChatError
import com.example.rcc.domain.repository.ChatRepository

/**
 * Use case for creating a new chat.
 *
 * Validates input before creating the chat.
 *
 * @property repository Chat repository for data access.
 */
public class CreateChatUseCase(
    private val repository: ChatRepository,
) {
    /**
     * Creates a new chat for a session.
     *
     * @param sessionId Cloud Code session identifier.
     * @return Result containing the created chat, or failure if validation fails or operation failed.
     */
    public suspend operator fun invoke(sessionId: String): Result<Chat> =
        try {
            if (sessionId.isBlank()) {
                return Result.failure(ChatError.InvalidInput("Session ID cannot be blank"))
            }
            repository.createChat(sessionId)
        } catch (e: kotlin.coroutines.cancellation.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
