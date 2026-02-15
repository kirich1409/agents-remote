package com.example.rcc.data.repository

import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
import com.example.rcc.domain.repository.ChatRepository

/**
 * In-memory implementation of ChatRepository.
 *
 * Stores chats and messages in memory for MVP.
 * Will be replaced with proper persistence in later phases.
 */
public class ChatRepositoryImpl : ChatRepository {
    private val chats = mutableListOf<Chat>()
    private val messages = mutableListOf<Message>()

    override suspend fun getChats(): Result<List<Chat>> = Result.success(chats.toList())

    override suspend fun getChatById(id: String): Result<Chat> {
        val chat = chats.find { it.id == id }
        return if (chat != null) {
            Result.success(chat)
        } else {
            Result.failure(Exception("Chat not found: $id"))
        }
    }

    override suspend fun createChat(sessionId: String): Result<Chat> {
        val chat = Chat(sessionId = sessionId)
        chats.add(chat)
        return Result.success(chat)
    }

    override suspend fun deleteChat(id: String): Result<Unit> {
        chats.removeAll { it.id == id }
        messages.removeAll { it.chatId == id }
        return Result.success(Unit)
    }

    override suspend fun sendMessage(chatId: String, content: String): Result<Message> {
        val chat = chats.find { it.id == chatId }
            ?: return Result.failure(Exception("Chat not found: $chatId"))

        val message = Message(
            chatId = chatId,
            role = MessageRole.USER,
            content = content,
        )
        messages.add(message)
        return Result.success(message)
    }

    override suspend fun sendAssistantMessage(chatId: String, content: String): Result<Message> {
        val chat = chats.find { it.id == chatId }
            ?: return Result.failure(Exception("Chat not found: $chatId"))

        val message = Message(
            chatId = chatId,
            role = MessageRole.ASSISTANT,
            content = content,
        )
        messages.add(message)
        return Result.success(message)
    }

    override suspend fun getMessages(chatId: String, limit: Int, offset: Int): Result<List<Message>> {
        val chatMessages = messages
            .filter { it.chatId == chatId }
            .sortedBy { it.timestamp }
            .drop(offset)
            .take(limit)
        return Result.success(chatMessages)
    }
}
