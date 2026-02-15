package com.example.rcc.domain.entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ChatTest {
    @Test
    fun testChatIsValidWhenSessionIdNotEmpty() {
        val chat = Chat(sessionId = "session-123")
        assertTrue(chat.isValid)
    }

    @Test
    fun testChatIsInvalidWhenSessionIdEmpty() {
        val chat = Chat(sessionId = "")
        assertFalse(chat.isValid)
    }

    @Test
    fun testChatGeneratesUniqueIds() {
        val chat1 = Chat(sessionId = "session-1")
        val chat2 = Chat(sessionId = "session-2")
        assertNotEquals(chat1.id, chat2.id)
    }

    @Test
    fun testChatHasDefaultTitle() {
        val chat = Chat(sessionId = "session-123")
        assertEquals("Chat", chat.title)
    }
}

class MessageTest {
    @Test
    fun testMessageHasAllRoles() {
        val userMsg = Message(chatId = "chat-1", role = MessageRole.USER, content = "Hello")
        val assistantMsg = Message(chatId = "chat-1", role = MessageRole.ASSISTANT, content = "Hi")
        val systemMsg = Message(chatId = "chat-1", role = MessageRole.SYSTEM, content = "Info")

        assertEquals(MessageRole.USER, userMsg.role)
        assertEquals(MessageRole.ASSISTANT, assistantMsg.role)
        assertEquals(MessageRole.SYSTEM, systemMsg.role)
    }

    @Test
    fun testMessageGeneratesUniqueIds() {
        val msg1 = Message(chatId = "chat-1", role = MessageRole.USER, content = "Test")
        val msg2 = Message(chatId = "chat-1", role = MessageRole.USER, content = "Test")
        assertNotEquals(msg1.id, msg2.id)
    }
}
