package com.example.rcc.domain.usecase

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
import com.example.rcc.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetChatsUseCaseTest {
    @Test
    fun testGetChatsUseCaseReturnsChats() = runTest {
        val expectedChat = Chat(sessionId = "session-1")
        val mockRepository =
            object : ChatRepository {
                override suspend fun getChats() = Result.success(listOf(expectedChat))

                override suspend fun getChatById(id: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun createChat(sessionId: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun deleteChat(id: String) = Result.failure<Unit>(NotImplementedError())

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.failure<List<Message>>(NotImplementedError())

                override suspend fun sendMessage(chatId: String, content: String) =
                    Result.failure<Message>(NotImplementedError())
            }

        val useCase = GetChatsUseCase(mockRepository)
        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("session-1", result.getOrThrow().first().sessionId)
    }

    @Test
    fun testGetChatsUseCaseReturnsFailureOnError() = runTest {
        val mockRepository =
            object : ChatRepository {
                override suspend fun getChats() = Result.failure<List<Chat>>(RuntimeException("DB error"))

                override suspend fun getChatById(id: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun createChat(sessionId: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun deleteChat(id: String) = Result.failure<Unit>(NotImplementedError())

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.failure<List<Message>>(NotImplementedError())

                override suspend fun sendMessage(chatId: String, content: String) =
                    Result.failure<Message>(NotImplementedError())
            }

        val useCase = GetChatsUseCase(mockRepository)
        val result = useCase()

        assertTrue(result.isFailure)
    }
}

class CreateChatUseCaseTest {
    @Test
    fun testCreateChatUseCaseCreatesChat() = runTest {
        val expectedChat = Chat(sessionId = "session-123")
        val mockRepository =
            object : ChatRepository {
                override suspend fun getChats() = Result.failure<List<Chat>>(NotImplementedError())

                override suspend fun getChatById(id: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun createChat(sessionId: String) = Result.success(expectedChat)

                override suspend fun deleteChat(id: String) = Result.failure<Unit>(NotImplementedError())

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.failure<List<Message>>(NotImplementedError())

                override suspend fun sendMessage(chatId: String, content: String) =
                    Result.failure<Message>(NotImplementedError())
            }

        val useCase = CreateChatUseCase(mockRepository)
        val result = useCase("session-123")

        assertTrue(result.isSuccess)
        assertEquals("session-123", result.getOrThrow().sessionId)
    }

    @Test
    fun testCreateChatUseCaseFailsOnBlankSessionId() = runTest {
        val mockRepository =
            object : ChatRepository {
                override suspend fun getChats() = Result.failure<List<Chat>>(NotImplementedError())

                override suspend fun getChatById(id: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun createChat(sessionId: String) = Result.success(Chat(sessionId = sessionId))

                override suspend fun deleteChat(id: String) = Result.failure<Unit>(NotImplementedError())

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.failure<List<Message>>(NotImplementedError())

                override suspend fun sendMessage(chatId: String, content: String) =
                    Result.failure<Message>(NotImplementedError())
            }

        val useCase = CreateChatUseCase(mockRepository)
        val result = useCase("")

        assertTrue(result.isFailure)
    }
}

class SendMessageUseCaseTest {
    @Test
    fun testSendMessageUseCaseFailsOnBlankChatId() = runTest {
        val mockRepository =
            object : ChatRepository {
                override suspend fun getChats() = Result.failure<List<Chat>>(NotImplementedError())

                override suspend fun getChatById(id: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun createChat(sessionId: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun deleteChat(id: String) = Result.failure<Unit>(NotImplementedError())

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.failure<List<Message>>(NotImplementedError())

                override suspend fun sendMessage(chatId: String, content: String) =
                    Result.failure<Message>(NotImplementedError())
            }

        val useCase = SendMessageUseCase(mockRepository)
        val result = useCase("", "Hello")

        assertTrue(result.isFailure)
    }

    @Test
    fun testSendMessageUseCaseFailsOnBlankContent() = runTest {
        val mockRepository =
            object : ChatRepository {
                override suspend fun getChats() = Result.failure<List<Chat>>(NotImplementedError())

                override suspend fun getChatById(id: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun createChat(sessionId: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun deleteChat(id: String) = Result.failure<Unit>(NotImplementedError())

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.failure<List<Message>>(NotImplementedError())

                override suspend fun sendMessage(chatId: String, content: String) =
                    Result.failure<Message>(NotImplementedError())
            }

        val useCase = SendMessageUseCase(mockRepository)
        val result = useCase("chat-1", "")

        assertTrue(result.isFailure)
    }

    @Test
    fun testSendMessageUseCaseSendsMessage() = runTest {
        val expectedMessage =
            Message(
                chatId = "chat-123",
                role = MessageRole.USER,
                content = "Hello",
            )
        val mockRepository =
            object : ChatRepository {
                override suspend fun getChats() = Result.failure<List<Chat>>(NotImplementedError())

                override suspend fun getChatById(id: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun createChat(sessionId: String) = Result.failure<Chat>(NotImplementedError())

                override suspend fun deleteChat(id: String) = Result.failure<Unit>(NotImplementedError())

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.failure<List<Message>>(NotImplementedError())

                override suspend fun sendMessage(chatId: String, content: String) = Result.success(expectedMessage)
            }

        val useCase = SendMessageUseCase(mockRepository)
        val result = useCase("chat-123", "Hello")

        assertTrue(result.isSuccess)
        assertEquals("chat-123", result.getOrThrow().chatId)
        assertEquals("Hello", result.getOrThrow().content)
    }
}
