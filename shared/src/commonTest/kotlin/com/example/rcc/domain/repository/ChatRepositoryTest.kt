package com.example.rcc.domain.repository

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
import kotlin.test.Test
import kotlin.test.assertNotNull

class ChatRepositoryTest {
    @Test
    fun testChatRepositoryInterfaceHasRequiredMethods() {
        // Verify interface compiles by creating anonymous implementation
        val repo =
            object : ChatRepository {
                override suspend fun getChats() = Result.success(emptyList<Chat>())

                override suspend fun getChatById(id: String) = Result.success(
                    Chat(sessionId = "session-1"),
                )

                override suspend fun createChat(sessionId: String) = Result.success(
                    Chat(sessionId = sessionId),
                )

                override suspend fun deleteChat(id: String) = Result.success(Unit)

                override suspend fun getMessages(chatId: String, limit: Int, offset: Int) =
                    Result.success(emptyList<Message>())

                override suspend fun sendMessage(chatId: String, content: String) = Result.success(
                    Message(chatId = chatId, role = MessageRole.USER, content = content),
                )
            }
        // If this compiles and runs, the interface contract is correct
        assertNotNull(repo)
    }
}

class SessionRepositoryTest {
    @Test
    fun testSessionRepositoryInterfaceHasRequiredMethods() {
        // Verify interface compiles by creating anonymous implementation
        val repo =
            object : SessionRepository {
                override suspend fun saveSessionToken(token: String) = Result.success(Unit)

                override suspend fun getSessionToken() = Result.success<String?>(null)

                override suspend fun clearSessionToken() = Result.success(Unit)

                override suspend fun isAuthenticated() = Result.success(false)
            }
        // If this compiles and runs, the interface contract is correct
        assertNotNull(repo)
    }
}
