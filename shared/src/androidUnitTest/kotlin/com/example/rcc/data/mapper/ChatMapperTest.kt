package com.example.rcc.data.mapper

import com.example.rcc.data.dto.ChatDto
import com.example.rcc.data.dto.MessageDto
import com.example.rcc.domain.entity.MessageRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * Unit tests for ChatMapper.
 *
 * Tests cover:
 * - Chat DTO ↔ Domain mapping
 * - Message DTO ↔ Domain mapping
 * - Role parsing (valid and invalid)
 * - Error handling for invalid data
 */
class ChatMapperTest {

    @Test
    fun `should map ChatDto to domain Chat`() {
        // Given
        val dto = ChatDto(
            id = "chat-123",
            sessionId = "session-456",
            createdAt = 1234567890L,
            lastActivity = 1234567899L,
            title = "Test Chat",
        )

        // When
        val domain = ChatMapper.toDomain(dto)

        // Then
        domain.id shouldBe "chat-123"
        domain.sessionId shouldBe "session-456"
        domain.createdAt shouldBe 1234567890L
        domain.lastActivity shouldBe 1234567899L
        domain.title shouldBe "Test Chat"
    }

    @Test
    fun `should map domain Chat to ChatDto`() {
        // Given
        val domain = com.example.rcc.domain.entity.Chat(
            id = "chat-789",
            sessionId = "session-012",
            createdAt = 9876543210L,
            lastActivity = 9876543219L,
            title = "Another Chat",
        )

        // When
        val dto = ChatMapper.toDto(domain)

        // Then
        dto.id shouldBe "chat-789"
        dto.sessionId shouldBe "session-012"
        dto.createdAt shouldBe 9876543210L
        dto.lastActivity shouldBe 9876543219L
        dto.title shouldBe "Another Chat"
    }

    @Test
    fun `should handle default title in ChatDto`() {
        // Given
        val dto = ChatDto(
            id = "chat-123",
            sessionId = "session-456",
            createdAt = 1234567890L,
            lastActivity = 1234567899L,
        )

        // When
        val domain = ChatMapper.toDomain(dto)

        // Then
        domain.title shouldBe "Chat"
    }

    @Test
    fun `should map MessageDto to domain Message with USER role`() {
        // Given
        val dto = MessageDto(
            id = "msg-1",
            chatId = "chat-123",
            role = "USER",
            content = "Hello",
            timestamp = 1234567890L,
        )

        // When
        val domain = ChatMapper.messageToDomain(dto)

        // Then
        domain.id shouldBe "msg-1"
        domain.chatId shouldBe "chat-123"
        domain.role shouldBe MessageRole.USER
        domain.content shouldBe "Hello"
        domain.timestamp shouldBe 1234567890L
    }

    @Test
    fun `should map MessageDto to domain Message with ASSISTANT role`() {
        // Given
        val dto = MessageDto(
            id = "msg-2",
            chatId = "chat-456",
            role = "ASSISTANT",
            content = "Hi there!",
            timestamp = 9876543210L,
        )

        // When
        val domain = ChatMapper.messageToDomain(dto)

        // Then
        domain.role shouldBe MessageRole.ASSISTANT
        domain.content shouldBe "Hi there!"
    }

    @Test
    fun `should map MessageDto to domain Message with SYSTEM role`() {
        // Given
        val dto = MessageDto(
            id = "msg-3",
            chatId = "chat-789",
            role = "SYSTEM",
            content = "System message",
            timestamp = 1111111111L,
        )

        // When
        val domain = ChatMapper.messageToDomain(dto)

        // Then
        domain.role shouldBe MessageRole.SYSTEM
    }

    @Test
    fun `should handle lowercase role in MessageDto`() {
        // Given
        val dto = MessageDto(
            id = "msg-4",
            chatId = "chat-123",
            role = "user",
            content = "Lowercase role",
            timestamp = 1234567890L,
        )

        // When
        val domain = ChatMapper.messageToDomain(dto)

        // Then
        domain.role shouldBe MessageRole.USER
    }

    @Test
    fun `should handle mixed case role in MessageDto`() {
        // Given
        val dto = MessageDto(
            id = "msg-4b",
            chatId = "chat-123",
            role = "Assistant",
            content = "Mixed case role",
            timestamp = 1234567890L,
        )

        // When
        val domain = ChatMapper.messageToDomain(dto)

        // Then
        domain.role shouldBe MessageRole.ASSISTANT
    }

    @Test
    fun `should throw exception for invalid role`() {
        // Given
        val dto = MessageDto(
            id = "msg-5",
            chatId = "chat-123",
            role = "INVALID_ROLE",
            content = "Bad role",
            timestamp = 1234567890L,
        )

        // When/Then
        shouldThrow<com.example.rcc.domain.error.ChatError.InvalidInput> {
            ChatMapper.messageToDomain(dto)
        }
    }

    @Test
    fun `should map domain Message to MessageDto`() {
        // Given
        val domain = com.example.rcc.domain.entity.Message(
            id = "msg-6",
            chatId = "chat-321",
            role = MessageRole.USER,
            content = "Domain to DTO",
            timestamp = 5555555555L,
        )

        // When
        val dto = ChatMapper.messageToDto(domain)

        // Then
        dto.id shouldBe "msg-6"
        dto.chatId shouldBe "chat-321"
        dto.role shouldBe "USER"
        dto.content shouldBe "Domain to DTO"
        dto.timestamp shouldBe 5555555555L
    }
}
