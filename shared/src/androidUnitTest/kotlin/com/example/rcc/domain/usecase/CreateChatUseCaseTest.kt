package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.repository.ChatRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Unit tests for CreateChatUseCase.
 *
 * Tests cover:
 * - Happy path: successful chat creation
 * - Input validation: blank/empty sessionId
 * - Error handling: repository failures
 */
class CreateChatUseCaseTest {
    private val repository = mockk<ChatRepository>()
    private val useCase = CreateChatUseCase(repository)

    @Test
    fun `should create chat with valid sessionId`() = runTest {
        // Given
        val sessionId = "session-123"
        val expectedChat = Chat(sessionId = sessionId)
        coEvery { repository.createChat(sessionId) } returns Result.success(expectedChat)

        // When
        val result = useCase(sessionId)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.sessionId shouldBe sessionId
        coVerify { repository.createChat(sessionId) }
    }

    @Test
    fun `should fail when sessionId is blank`() = runTest {
        // Given
        val blankSessionId = ""

        // When
        val result = useCase(blankSessionId)

        // Then
        result.isFailure shouldBe true
        coVerify(exactly = 0) { repository.createChat(any()) }
    }

    @Test
    fun `should propagate repository failure`() = runTest {
        // Given
        val sessionId = "session-123"
        val exception = RuntimeException("Database connection failed")
        coEvery { repository.createChat(sessionId) } returns Result.failure(exception)

        // When
        val result = useCase(sessionId)

        // Then
        result.isFailure shouldBe true
        coVerify { repository.createChat(sessionId) }
    }

    @Test
    fun `should handle repository network timeout`() = runTest {
        // Given
        val sessionId = "session-123"
        coEvery { repository.createChat(sessionId) } returns Result.failure(
            java.net.SocketTimeoutException("Connection timeout")
        )

        // When
        val result = useCase(sessionId)

        // Then
        result.isFailure shouldBe true
    }
}
