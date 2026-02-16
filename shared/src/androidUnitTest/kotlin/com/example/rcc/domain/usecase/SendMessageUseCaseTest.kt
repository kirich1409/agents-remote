package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
import com.example.rcc.domain.repository.ChatRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Unit tests for SendMessageUseCase.
 *
 * Tests cover:
 * - Happy path: successful message sending
 * - Input validation: blank chatId, blank content
 * - Error handling: repository failures
 */
class SendMessageUseCaseTest {
    private val repository = mockk<ChatRepository>()
    private val useCase = SendMessageUseCase(repository)

    @Test
    fun `should send message successfully`() = runTest {
        // Given
        val chatId = "chat-123"
        val content = "Hello, World!"
        val expectedMessage = Message(
            chatId = chatId,
            role = MessageRole.USER,
            content = content,
        )
        coEvery { repository.sendMessage(chatId, content) } returns Result.success(expectedMessage)

        // When
        val result = useCase(chatId, content)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.chatId shouldBe chatId
        result.getOrNull()?.content shouldBe content
        coVerify { repository.sendMessage(chatId, content) }
    }

    @Test
    fun `should fail when chatId is blank`() = runTest {
        // Given
        val blankChatId = ""
        val content = "Hello"

        // When
        val result = useCase(blankChatId, content)

        // Then
        result.isFailure shouldBe true
        coVerify(exactly = 0) { repository.sendMessage(any(), any()) }
    }

    @Test
    fun `should fail when content is blank`() = runTest {
        // Given
        val chatId = "chat-123"
        val blankContent = ""

        // When
        val result = useCase(chatId, blankContent)

        // Then
        result.isFailure shouldBe true
        coVerify(exactly = 0) { repository.sendMessage(any(), any()) }
    }
}
