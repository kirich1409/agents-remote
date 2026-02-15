package com.example.rcc.features.chatlist.store

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
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

class ChatListStoreTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeRepository = FakeChatRepository()
    private val getChatsUseCase = GetChatsUseCase(fakeRepository)
    private val createChatUseCase = CreateChatUseCase(fakeRepository)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createStore(): ChatListStore = ChatListStoreFactory(
        storeFactory = DefaultStoreFactory(),
        getChatsUseCase = getChatsUseCase,
        createChatUseCase = createChatUseCase,
    ).create()

    @Test
    fun initialStateIsEmpty() {
        val store = createStore()
        assertTrue(store.state.chats.isEmpty())
        assertFalse(store.state.isLoading)
        assertNull(store.state.error)
    }

    @Test
    fun loadChatsUpdatesState() {
        fakeRepository.chats.add(
            Chat(id = "1", sessionId = "s1", createdAt = 1000L, lastActivity = 1000L),
        )
        val store = createStore()
        store.accept(ChatListStore.Intent.LoadChats)

        assertEquals(1, store.state.chats.size)
        assertEquals("1", store.state.chats[0].id)
        assertFalse(store.state.isLoading)
    }

    @Test
    fun createNewChatAddsChat() {
        val store = createStore()
        store.accept(ChatListStore.Intent.CreateNewChat)

        assertEquals(1, store.state.chats.size)
        assertFalse(store.state.isLoading)
    }

    @Test
    fun deleteChatRemovesFromState() {
        fakeRepository.chats.add(
            Chat(id = "1", sessionId = "s1", createdAt = 1000L, lastActivity = 1000L),
        )
        val store = createStore()
        store.accept(ChatListStore.Intent.LoadChats)

        store.accept(ChatListStore.Intent.DeleteChat("1"))

        assertTrue(store.state.chats.isEmpty())
    }

    @Test
    fun loadChatsErrorUpdatesState() {
        fakeRepository.shouldFail = true
        val store = createStore()
        store.accept(ChatListStore.Intent.LoadChats)

        assertTrue(store.state.chats.isEmpty())
        assertFalse(store.state.isLoading)
        assertEquals("Test error", store.state.error)
    }
}

private class FakeChatRepository : ChatRepository {
    val chats = mutableListOf<Chat>()
    var shouldFail = false

    override suspend fun getChats(): Result<List<Chat>> = if (shouldFail) {
        Result.failure(Exception("Test error"))
    } else {
        Result.success(chats.toList())
    }

    override suspend fun getChatById(id: String): Result<Chat> = chats.find { it.id == id }?.let { Result.success(it) }
        ?: Result.failure(Exception("Not found"))

    override suspend fun createChat(sessionId: String): Result<Chat> {
        val chat = Chat(sessionId = sessionId, createdAt = 1000L, lastActivity = 1000L)
        chats.add(chat)
        return Result.success(chat)
    }

    override suspend fun deleteChat(id: String): Result<Unit> {
        chats.removeAll { it.id == id }
        return Result.success(Unit)
    }

    override suspend fun sendMessage(chatId: String, content: String): Result<Message> =
        Result.failure(Exception("Not implemented"))

    override suspend fun sendAssistantMessage(chatId: String, content: String): Result<Message> =
        Result.failure(Exception("Not implemented"))

    override suspend fun getMessages(chatId: String, limit: Int, offset: Int): Result<List<Message>> =
        Result.success(emptyList())
}
