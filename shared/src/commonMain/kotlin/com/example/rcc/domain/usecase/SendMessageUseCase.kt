package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.error.ChatError
import com.example.rcc.domain.repository.ChatRepository

/**
 * Use case for sending a message to a chat.
 *
 * Validates input before sending the message.
 *
 * @property repository Chat repository for data access.
 */
public class SendMessageUseCase(
    private val repository: ChatRepository,
) {
    /**
     * Sends a message to a chat.
     *
     * @param chatId ID of the chat to send message to.
     * @param content Text content of the message.
     * @return Result containing the created message, or failure if validation fails or operation failed.
     */
    public suspend operator fun invoke(
        chatId: String,
        content: String,
    ): Result<Message> =
        try {
            if (chatId.isBlank()) {
                return Result.failure(ChatError.InvalidInput("Chat ID cannot be blank"))
            }
            if (content.isBlank()) {
                return Result.failure(ChatError.InvalidInput("Message cannot be empty"))
            }
            repository.sendMessage(chatId, content)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
