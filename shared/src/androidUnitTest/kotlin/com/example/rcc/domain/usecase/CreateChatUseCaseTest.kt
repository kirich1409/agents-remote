package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.repository.ChatRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    @Test
    fun `should fail when sessionId is whitespace only`() = runTest {
        // Given
        val whitespaceSessionId = "   "

        // When
        val result = useCase(whitespaceSessionId)

        // Then
        result.isFailure shouldBe true
        coVerify(exactly = 0) { repository.createChat(any()) }
    }

    @Test
    fun `should fail when sessionId contains only tabs`() = runTest {
        // Given
        val tabSessionId = "\t\t"

        // When
        val result = useCase(tabSessionId)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should fail when sessionId contains only newlines`() = runTest {
        // Given
        val newlineSessionId = "\n\n"

        // When
        val result = useCase(newlineSessionId)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should succeed with valid UUID sessionId`() = runTest {
        // Given
        val uuidSessionId = "123e4567-e89b-12d3-a456-426614174000"
        val expectedChat = Chat(sessionId = uuidSessionId)
        coEvery { repository.createChat(uuidSessionId) } returns Result.success(expectedChat)

        // When
        val result = useCase(uuidSessionId)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.sessionId shouldBe uuidSessionId
    }

    @Test
    fun `should succeed with non-UUID but valid sessionId`() = runTest {
        // Given
        val alphanumericSessionId = "session-abc-123"
        val expectedChat = Chat(sessionId = alphanumericSessionId)
        coEvery { repository.createChat(alphanumericSessionId) } returns Result.success(expectedChat)

        // When
        val result = useCase(alphanumericSessionId)

        // Then
        result.isSuccess shouldBe true
    }

    @Test
    fun `should handle concurrent chat creation for different sessions`() = runTest {
        // Given
        val sessionId1 = "session-1"
        val sessionId2 = "session-2"
        val chat1 = Chat(sessionId = sessionId1)
        val chat2 = Chat(sessionId = sessionId2)

        coEvery { repository.createChat(sessionId1) } returns Result.success(chat1)
        coEvery { repository.createChat(sessionId2) } returns Result.success(chat2)

        // When - Launch concurrent calls
        val deferred1 = async { useCase(sessionId1) }
        val deferred2 = async { useCase(sessionId2) }
        val result1 = deferred1.await()
        val result2 = deferred2.await()

        // Then
        result1.isSuccess shouldBe true
        result2.isSuccess shouldBe true
        result1.getOrNull()?.sessionId shouldBe sessionId1
        result2.getOrNull()?.sessionId shouldBe sessionId2
        coVerify(exactly = 1) { repository.createChat(sessionId1) }
        coVerify(exactly = 1) { repository.createChat(sessionId2) }
    }

    @Test
    fun `should handle concurrent chat creation for same session`() = runTest {
        // Given
        val sessionId = "session-duplicate"
        val chat = Chat(sessionId = sessionId)
        coEvery { repository.createChat(sessionId) } returns Result.success(chat)

        // When - Launch multiple concurrent calls with same sessionId
        val results = List(5) {
            async { useCase(sessionId) }
        }.awaitAll()

        // Then - All succeed (repository determines uniqueness)
        results.forEach { result ->
            result.isSuccess shouldBe true
        }
        coVerify(exactly = 5) { repository.createChat(sessionId) }
    }
}
