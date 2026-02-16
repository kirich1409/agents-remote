package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
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

    @Test
    fun `should fail when content is whitespace only`() = runTest {
        // Given
        val chatId = "chat-123"
        val whitespaceContent = "   "

        // When
        val result = useCase(chatId, whitespaceContent)

        // Then
        result.isFailure shouldBe true
        coVerify(exactly = 0) { repository.sendMessage(any(), any()) }
    }

    @Test
    fun `should succeed with very long content`() = runTest {
        // Given
        val chatId = "chat-123"
        val longContent = "a".repeat(10000)
        val expectedMessage = Message(
            chatId = chatId,
            role = MessageRole.USER,
            content = longContent,
        )
        coEvery { repository.sendMessage(chatId, longContent) } returns Result.success(expectedMessage)

        // When
        val result = useCase(chatId, longContent)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.content shouldBe longContent
    }

    @Test
    fun `should succeed with special characters in content`() = runTest {
        // Given
        val chatId = "chat-123"
        val specialContent = "Hello! @#$%^&*() 你好 🚀"
        val expectedMessage = Message(
            chatId = chatId,
            role = MessageRole.USER,
            content = specialContent,
        )
        coEvery { repository.sendMessage(chatId, specialContent) } returns Result.success(expectedMessage)

        // When
        val result = useCase(chatId, specialContent)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.content shouldBe specialContent
    }

    @Test
    fun `should succeed with multiline content`() = runTest {
        // Given
        val chatId = "chat-123"
        val multilineContent = "Line 1\nLine 2\nLine 3"
        val expectedMessage = Message(
            chatId = chatId,
            role = MessageRole.USER,
            content = multilineContent,
        )
        coEvery { repository.sendMessage(chatId, multilineContent) } returns Result.success(expectedMessage)

        // When
        val result = useCase(chatId, multilineContent)

        // Then
        result.isSuccess shouldBe true
    }

    @Test
    fun `should fail when chatId is whitespace only`() = runTest {
        // Given
        val whitespaceChatId = "   "
        val content = "Hello"

        // When
        val result = useCase(whitespaceChatId, content)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should succeed with UUID chatId`() = runTest {
        // Given
        val uuidChatId = "550e8400-e29b-41d4-a716-446655440000"
        val content = "Hello"
        val expectedMessage = Message(
            chatId = uuidChatId,
            role = MessageRole.USER,
            content = content,
        )
        coEvery { repository.sendMessage(uuidChatId, content) } returns Result.success(expectedMessage)

        // When
        val result = useCase(uuidChatId, content)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.chatId shouldBe uuidChatId
    }

    @Test
    fun `should succeed with alphanumeric chatId`() = runTest {
        // Given
        val alphanumericChatId = "chat-abc-123"
        val content = "Hello"
        val expectedMessage = Message(
            chatId = alphanumericChatId,
            role = MessageRole.USER,
            content = content,
        )
        coEvery { repository.sendMessage(alphanumericChatId, content) } returns Result.success(expectedMessage)

        // When
        val result = useCase(alphanumericChatId, content)

        // Then
        result.isSuccess shouldBe true
    }

    @Test
    fun `should propagate repository failure`() = runTest {
        // Given
        val chatId = "chat-123"
        val content = "Hello"
        val exception = RuntimeException("Database error")
        coEvery { repository.sendMessage(chatId, content) } returns Result.failure(exception)

        // When
        val result = useCase(chatId, content)

        // Then
        result.isFailure shouldBe true
        coVerify { repository.sendMessage(chatId, content) }
    }

    @Test
    fun `should handle chat not found error`() = runTest {
        // Given
        val nonExistentChatId = "non-existent-chat"
        val content = "Hello"
        coEvery { repository.sendMessage(nonExistentChatId, content) } returns Result.failure(
            NoSuchElementException("Chat not found")
        )

        // When
        val result = useCase(nonExistentChatId, content)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should handle network timeout`() = runTest {
        // Given
        val chatId = "chat-123"
        val content = "Hello"
        coEvery { repository.sendMessage(chatId, content) } returns Result.failure(
            java.net.SocketTimeoutException("Timeout")
        )

        // When
        val result = useCase(chatId, content)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should handle concurrent messages to different chats`() = runTest {
        // Given
        val chatId1 = "chat-1"
        val chatId2 = "chat-2"
        val content1 = "Message 1"
        val content2 = "Message 2"

        val message1 = Message(chatId = chatId1, role = MessageRole.USER, content = content1)
        val message2 = Message(chatId = chatId2, role = MessageRole.USER, content = content2)

        coEvery { repository.sendMessage(chatId1, content1) } returns Result.success(message1)
        coEvery { repository.sendMessage(chatId2, content2) } returns Result.success(message2)

        // When
        val deferred1 = async { useCase(chatId1, content1) }
        val deferred2 = async { useCase(chatId2, content2) }
        val result1 = deferred1.await()
        val result2 = deferred2.await()

        // Then
        result1.isSuccess shouldBe true
        result2.isSuccess shouldBe true
        result1.getOrNull()?.chatId shouldBe chatId1
        result2.getOrNull()?.chatId shouldBe chatId2
    }

    @Test
    fun `should handle concurrent messages to same chat`() = runTest {
        // Given
        val chatId = "chat-123"
        val messages = List(10) { index ->
            Message(
                chatId = chatId,
                role = MessageRole.USER,
                content = "Message $index",
            )
        }

        messages.forEachIndexed { index, message ->
            coEvery {
                repository.sendMessage(chatId, "Message $index")
            } returns Result.success(message)
        }

        // When - Send 10 concurrent messages to same chat
        val results = List(10) { index ->
            async { useCase(chatId, "Message $index") }
        }.awaitAll()

        // Then
        results.forEach { result ->
            result.isSuccess shouldBe true
        }
        coVerify(exactly = 10) { repository.sendMessage(chatId, any()) }
    }
}
