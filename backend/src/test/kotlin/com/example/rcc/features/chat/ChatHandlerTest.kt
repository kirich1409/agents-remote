package com.example.rcc.features.chat

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test

/**
 * Tests for the ChatHandler class.
 *
 * Tests the getChats endpoint behavior with successful and failed responses.
 */
public class ChatHandlerTest {
    private val getChatsUseCase: GetChatsUseCase = mockk()
    private val createChatUseCase: CreateChatUseCase = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()

    private val chatHandler: ChatHandler =
        ChatHandler(
            getChatsUseCase = getChatsUseCase,
            createChatUseCase = createChatUseCase,
            sendMessageUseCase = sendMessageUseCase,
        )

    /**
     * Test: getChats returns list of chats successfully.
     */
    @Test
    public fun testGetChatsSuccess() =
        shouldNotThrow {
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

            val result: Result<List<*>> = chatHandler.getChats()

            result.isSuccess shouldBe true
            result.getOrNull()?.size shouldBe 1
        }

    /**
     * Test: getChats handles failures gracefully.
     */
    @Test
    public fun testGetChatsFailure() =
        shouldNotThrow {
            val exception = Exception("Database error")

            coEvery { getChatsUseCase() } returns Result.failure(exception)

            val result: Result<List<*>> = chatHandler.getChats()

            result.isFailure shouldBe true
        }
}
