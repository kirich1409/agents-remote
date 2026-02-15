package com.example.rcc.features.chatlist.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.features.chatlist.store.ChatListStore.Intent
import com.example.rcc.features.chatlist.store.ChatListStore.State
import kotlinx.coroutines.launch

/** Factory that creates [ChatListStore] instances wired with use cases. */
public class ChatListStoreFactory(
    private val storeFactory: StoreFactory,
    private val getChatsUseCase: GetChatsUseCase,
    private val createChatUseCase: CreateChatUseCase,
) {
    /** Creates a new [ChatListStore] instance. */
    public fun create(): ChatListStore = object :
        ChatListStore,
        Store<Intent, State, Nothing> by storeFactory.create(
            name = "ChatListStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = { msg: Msg ->
                when (msg) {
                    is Msg.Loading -> {
                        copy(isLoading = true, error = null)
                    }

                    is Msg.ChatsLoaded -> {
                        copy(chats = msg.chats, isLoading = false)
                    }

                    is Msg.ChatCreated -> {
                        copy(
                            chats = chats + msg.chat,
                            isLoading = false,
                        )
                    }

                    is Msg.ChatDeleted -> {
                        copy(
                            chats = chats.filter { it.id != msg.id },
                        )
                    }

                    is Msg.Error -> {
                        copy(isLoading = false, error = msg.error)
                    }
                }
            },
        ) {}

    private sealed interface Msg {
        data object Loading : Msg

        data class ChatsLoaded(val chats: List<Chat>) : Msg

        data class ChatCreated(val chat: Chat) : Msg

        data class ChatDeleted(val id: String) : Msg

        data class Error(val error: String) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.LoadChats -> loadChats()
                is Intent.CreateNewChat -> createChat()
                is Intent.DeleteChat -> dispatch(Msg.ChatDeleted(intent.id))
                is Intent.SelectChat -> Unit
            }
        }

        private fun loadChats() {
            dispatch(Msg.Loading)
            scope.launch {
                getChatsUseCase().fold(
                    onSuccess = { dispatch(Msg.ChatsLoaded(it)) },
                    onFailure = { dispatch(Msg.Error(it.message ?: "Unknown error")) },
                )
            }
        }

        private fun createChat() {
            dispatch(Msg.Loading)
            scope.launch {
                createChatUseCase("new-session").fold(
                    onSuccess = { dispatch(Msg.ChatCreated(it)) },
                    onFailure = { dispatch(Msg.Error(it.message ?: "Creation failed")) },
                )
            }
        }
    }
}
