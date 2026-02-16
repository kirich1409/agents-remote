package com.example.rcc.domain.entity

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import kotlin.test.Test

/**
 * Unit tests for Message entity.
 *
 * Tests cover:
 * - Default values (id, timestamp)
 * - Field validation
 * - MessageRole enum
 */
class MessageTest {

    @Test
    fun `should create message with all fields`() {
        // When
        val message = Message(
            id = "msg-123",
            chatId = "chat-456",
            role = MessageRole.USER,
            content = "Hello, World!",
            timestamp = 1234567890L,
        )

        // Then
        message.id shouldBe "msg-123"
        message.chatId shouldBe "chat-456"
        message.role shouldBe MessageRole.USER
        message.content shouldBe "Hello, World!"
        message.timestamp shouldBe 1234567890L
    }

    @Test
    fun `should generate unique id by default`() {
        // When
        val message1 = Message(
            chatId = "chat-1",
            role = MessageRole.USER,
            content = "Message 1",
        )
        val message2 = Message(
            chatId = "chat-1",
            role = MessageRole.USER,
            content = "Message 2",
        )

        // Then
        message1.id.shouldNotBeEmpty()
        message2.id.shouldNotBeEmpty()
        message1.id shouldNotBe message2.id
    }

    @Test
    fun `should generate timestamp by default`() {
        // When
        val message = Message(
            chatId = "chat-123",
            role = MessageRole.USER,
            content = "Test",
        )

        // Then
        message.timestamp shouldNotBe 0L
    }

    @Test
    fun `should support USER role`() {
        // When
        val message = Message(
            chatId = "chat-1",
            role = MessageRole.USER,
            content = "User message",
        )

        // Then
        message.role shouldBe MessageRole.USER
    }

    @Test
    fun `should support ASSISTANT role`() {
        // When
        val message = Message(
            chatId = "chat-1",
            role = MessageRole.ASSISTANT,
            content = "Assistant message",
        )

        // Then
        message.role shouldBe MessageRole.ASSISTANT
    }

    @Test
    fun `should support SYSTEM role`() {
        // When
        val message = Message(
            chatId = "chat-1",
            role = MessageRole.SYSTEM,
            content = "System message",
        )

        // Then
        message.role shouldBe MessageRole.SYSTEM
    }

    @Test
    fun `should handle empty content`() {
        // When
        val message = Message(
            chatId = "chat-1",
            role = MessageRole.USER,
            content = "",
        )

        // Then
        message.content shouldBe ""
    }

    @Test
    fun `should handle very long content`() {
        // Given
        val longContent = "a".repeat(100000)

        // When
        val message = Message(
            chatId = "chat-1",
            role = MessageRole.USER,
            content = longContent,
        )

        // Then
        message.content.length shouldBe 100000
    }

    @Test
    fun `should handle special characters in content`() {
        // Given
        val specialContent = "Hello! @#$%^&*() 你好 🚀\n\tMultiline"

        // When
        val message = Message(
            chatId = "chat-1",
            role = MessageRole.USER,
            content = specialContent,
        )

        // Then
        message.content shouldBe specialContent
    }
}
