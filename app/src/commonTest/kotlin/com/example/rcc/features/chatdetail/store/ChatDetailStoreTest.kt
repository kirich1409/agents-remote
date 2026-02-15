package com.example.rcc.features.chatdetail.store

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChatDetailStoreTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeRepository = FakeChatRepository()
    private val sendMessageUseCase = SendMessageUseCase(fakeRepository)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createStore(chatId: String = "chat-1"): ChatDetailStore = ChatDetailStoreFactory(
        storeFactory = DefaultStoreFactory(),
        chatRepository = fakeRepository,
        sendMessageUseCase = sendMessageUseCase,
    ).create(chatId)

    @Test
    fun initialStateHasChatId() {
        val store = createStore("my-chat")
        assertEquals("my-chat", store.state.chatId)
        assertTrue(store.state.messages.isEmpty())
        assertFalse(store.state.isLoading)
        assertFalse(store.state.isSending)
        assertNull(store.state.error)
    }

    @Test
    fun loadMessagesUpdatesState() {
        fakeRepository.messages["chat-1"] = mutableListOf(
            Message(
                id = "m1",
                chatId = "chat-1",
                role = MessageRole.USER,
                content = "Hello",
                timestamp = 1000L,
            ),
        )
        val store = createStore("chat-1")
        store.accept(ChatDetailStore.Intent.LoadMessages)

        assertEquals(1, store.state.messages.size)
        assertEquals("Hello", store.state.messages[0].content)
        assertFalse(store.state.isLoading)
    }

    @Test
    fun sendMessageUpdatesState() {
        val store = createStore("chat-1")
        store.accept(ChatDetailStore.Intent.SendMessage("Hi there"))

        assertEquals(1, store.state.messages.size)
        assertEquals("Hi there", store.state.messages[0].content)
        assertEquals(MessageRole.USER, store.state.messages[0].role)
        assertFalse(store.state.isSending)
    }

    @Test
    fun sendMessageErrorUpdatesState() {
        fakeRepository.shouldFail = true
        val store = createStore("chat-1")
        store.accept(ChatDetailStore.Intent.SendMessage("test"))

        assertTrue(store.state.messages.isEmpty())
        assertFalse(store.state.isSending)
        assertEquals("Test error", store.state.error)
    }
}

private class FakeChatRepository : ChatRepository {
    val messages = mutableMapOf<String, MutableList<Message>>()
    var shouldFail = false

    override suspend fun getChats(): Result<List<Chat>> = Result.success(emptyList())

    override suspend fun getChatById(id: String): Result<Chat> = Result.failure(Exception("Not implemented"))

    override suspend fun createChat(sessionId: String): Result<Chat> = Result.failure(Exception("Not implemented"))

    override suspend fun deleteChat(id: String): Result<Unit> = Result.success(Unit)

    override suspend fun sendMessage(chatId: String, content: String): Result<Message> {
        if (shouldFail) return Result.failure(Exception("Test error"))
        val message = Message(
            chatId = chatId,
            role = MessageRole.USER,
            content = content,
            timestamp = 1000L,
        )
        messages.getOrPut(chatId) { mutableListOf() }.add(message)
        return Result.success(message)
    }

    override suspend fun getMessages(chatId: String, limit: Int, offset: Int): Result<List<Message>> = if (shouldFail) {
        Result.failure(Exception("Test error"))
    } else {
        Result.success(messages[chatId]?.toList() ?: emptyList())
    }
}
