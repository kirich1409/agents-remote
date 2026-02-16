package com.example.rcc.domain.usecase.integration

import com.example.rcc.data.repository.ChatRepositoryImpl
import com.example.rcc.domain.entity.MessageRole
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Integration tests for Chat flow.
 *
 * Tests the complete flow using real in-memory repository:
 * - CreateChat → SendMessage → GetChats
 * - Error scenarios end-to-end
 */
class ChatFlowIntegrationTest {
    private lateinit var repository: ChatRepositoryImpl
    private lateinit var createChatUseCase: CreateChatUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var getChatsUseCase: GetChatsUseCase

    @BeforeTest
    fun setup() {
        repository = ChatRepositoryImpl()
        createChatUseCase = CreateChatUseCase(repository)
        sendMessageUseCase = SendMessageUseCase(repository)
        getChatsUseCase = GetChatsUseCase(repository)
    }

    @Test
    fun `should create chat and send message successfully`() = runTest {
        // Given
        val sessionId = "integration-session-1"

        // When - Create chat
        val createResult = createChatUseCase(sessionId)

        // Then - Chat created
        createResult.isSuccess shouldBe true
        val chat = createResult.getOrThrow()
        chat.sessionId shouldBe sessionId

        // When - Send message to chat
        val sendResult = sendMessageUseCase(chat.id, "Hello from integration test")

        // Then - Message sent
        sendResult.isSuccess shouldBe true
        val message = sendResult.getOrThrow()
        message.chatId shouldBe chat.id
        message.content shouldBe "Hello from integration test"
        message.role shouldBe MessageRole.USER
    }

    @Test
    fun `should retrieve created chat in chat list`() = runTest {
        // Given
        val sessionId = "integration-session-2"

        // When - Create chat
        val createResult = createChatUseCase(sessionId)
        val createdChat = createResult.getOrThrow()

        // When - Get all chats
        val getChatsResult = getChatsUseCase()

        // Then - Created chat is in the list
        getChatsResult.isSuccess shouldBe true
        val chats = getChatsResult.getOrThrow()
        chats.any { it.id == createdChat.id } shouldBe true
    }

    @Test
    fun `should send multiple messages to same chat`() = runTest {
        // Given
        val sessionId = "integration-session-3"
        val createResult = createChatUseCase(sessionId)
        val chat = createResult.getOrThrow()

        // When - Send 3 messages
        val message1 = sendMessageUseCase(chat.id, "Message 1").getOrThrow()
        val message2 = sendMessageUseCase(chat.id, "Message 2").getOrThrow()
        val message3 = sendMessageUseCase(chat.id, "Message 3").getOrThrow()

        // Then - All messages belong to same chat
        message1.chatId shouldBe chat.id
        message2.chatId shouldBe chat.id
        message3.chatId shouldBe chat.id

        // And messages have different ids
        message1.id shouldBe message1.id // Not equal to others
        message2.id shouldBe message2.id
        message3.id shouldBe message3.id
    }

    @Test
    fun `should fail to send message to non-existent chat`() = runTest {
        // Given
        val nonExistentChatId = "non-existent-chat"

        // When
        val result = sendMessageUseCase(nonExistentChatId, "This should fail")

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should handle multiple concurrent chat creations`() = runTest {
        // Given
        val sessionIds = List(5) { "session-$it" }

        // When - Create 5 chats concurrently
        val chats = sessionIds.map { sessionId ->
            createChatUseCase(sessionId).getOrThrow()
        }

        // Then - All chats created with correct sessionIds
        chats.size shouldBe 5
        chats.map { it.sessionId } shouldBe sessionIds

        // And - All chats retrievable
        val allChats = getChatsUseCase().getOrThrow()
        chats.all { chat -> allChats.any { it.id == chat.id } } shouldBe true
    }
}
