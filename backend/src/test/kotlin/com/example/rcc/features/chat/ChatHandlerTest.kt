package com.example.rcc.features.chat

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import com.example.rcc.service.ClaudeCodeService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Tests for the ChatHandler class.
 *
 * Tests the getChats endpoint behavior with successful and failed responses.
 */
public class ChatHandlerTest {
    private val chatRepository: ChatRepository = mockk()
    private val getChatsUseCase: GetChatsUseCase = mockk()
    private val createChatUseCase: CreateChatUseCase = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()
    private val claudeCodeService: ClaudeCodeService = mockk()
    private val webSocketHandler: WebSocketHandler = mockk()

    private val chatHandler: ChatHandler =
        ChatHandler(
            chatRepository = chatRepository,
            getChatsUseCase = getChatsUseCase,
            createChatUseCase = createChatUseCase,
            sendMessageUseCase = sendMessageUseCase,
            claudeCodeService = claudeCodeService,
            webSocketHandler = webSocketHandler,
        )

    /**
     * Test: getChats returns list of chats successfully.
     */
    @Test
    public fun testGetChatsSuccess() = runTest {
        val mockChats =
            listOf(
                Chat(
                    id = "chat-1",
                    sessionId = "session-1",
                    createdAt = 1000L,
                    lastActivity = 2000L,
                    title = "Test Chat",
                ),
            )

        coEvery { getChatsUseCase() } returns Result.success(mockChats)

        val result = chatHandler.getChats()

        result.isSuccess shouldBe true
        result.getOrNull()?.size shouldBe 1
    }

    /**
     * Test: getChats handles failures gracefully.
     */
    @Test
    public fun testGetChatsFailure() = runTest {
        val exception = Exception("Database error")

        coEvery { getChatsUseCase() } returns Result.failure(exception)

        val result = chatHandler.getChats()

        result.isFailure shouldBe true
    }
}
