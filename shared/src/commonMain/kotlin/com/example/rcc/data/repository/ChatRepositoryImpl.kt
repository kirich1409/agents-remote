package com.example.rcc.data.repository

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
import com.example.rcc.domain.repository.ChatRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory implementation of ChatRepository.
 *
 * Stores chats and messages in memory for MVP.
 * Will be replaced with proper persistence in later phases.
 * Thread-safe implementation using Mutex to prevent race conditions.
 */
public class ChatRepositoryImpl : ChatRepository {
    private val chatsMutex = Mutex()
    private val messagesMutex = Mutex()
    private val chats = mutableListOf<Chat>()
    private val messages = mutableListOf<Message>()

    override suspend fun getChats(): Result<List<Chat>> = chatsMutex.withLock {
        Result.success(chats.toList())
    }

    override suspend fun getChatById(id: String): Result<Chat> = chatsMutex.withLock {
        chats.find { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(Exception("Chat not found: $id"))
    }

    override suspend fun createChat(sessionId: String): Result<Chat> = chatsMutex.withLock {
        val chat = Chat(sessionId = sessionId)
        chats.add(chat)
        Result.success(chat)
    }

    override suspend fun deleteChat(id: String): Result<Unit> {
        chatsMutex.withLock {
            chats.removeAll { it.id == id }
        }
        messagesMutex.withLock {
            messages.removeAll { it.chatId == id }
        }
        return Result.success(Unit)
    }

    override suspend fun sendMessage(chatId: String, content: String): Result<Message> {
        val chatExists = chatsMutex.withLock {
            chats.find { it.id == chatId }
        } ?: return Result.failure(Exception("Chat not found: $chatId"))

        val message = Message(
            chatId = chatId,
            role = MessageRole.USER,
            content = content,
        )
        messagesMutex.withLock {
            messages.add(message)
        }
        return Result.success(message)
    }

    override suspend fun sendAssistantMessage(chatId: String, content: String): Result<Message> {
        val chatExists = chatsMutex.withLock {
            chats.find { it.id == chatId }
        } ?: return Result.failure(Exception("Chat not found: $chatId"))

        val message = Message(
            chatId = chatId,
            role = MessageRole.ASSISTANT,
            content = content,
        )
        messagesMutex.withLock {
            messages.add(message)
        }
        return Result.success(message)
    }

    override suspend fun getMessages(chatId: String, limit: Int, offset: Int): Result<List<Message>> = messagesMutex.withLock {
        val chatMessages = messages
            .filter { it.chatId == chatId }
            .sortedBy { it.timestamp }
            .drop(offset)
            .take(limit)
        Result.success(chatMessages)
    }
}
