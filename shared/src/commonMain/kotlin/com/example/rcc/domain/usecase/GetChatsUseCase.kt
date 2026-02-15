package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.repository.ChatRepository
import org.koin.core.annotation.Single

/**
 * Use case for retrieving all chats.
 *
 * @property repository Chat repository for data access.
 */
@Single
public class GetChatsUseCase(private val repository: ChatRepository) {
    /**
     * Retrieves all chats.
     *
     * @return Result containing list of chats, or failure if operation failed.
     */
    public suspend operator fun invoke(): Result<List<Chat>> = try {
        repository.getChats()
    } catch (e: kotlin.coroutines.cancellation.CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
