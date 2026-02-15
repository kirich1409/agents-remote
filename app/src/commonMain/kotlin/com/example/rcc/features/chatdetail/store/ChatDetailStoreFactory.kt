package com.example.rcc.features.chatdetail.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.SendMessageUseCase
import com.example.rcc.features.chatdetail.store.ChatDetailStore.Intent
import com.example.rcc.features.chatdetail.store.ChatDetailStore.State
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/** Factory that creates [ChatDetailStore] instances wired with repository and use cases. */
@Single
public class ChatDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val chatRepository: ChatRepository,
    private val sendMessageUseCase: SendMessageUseCase,
) {
    /** Creates a new [ChatDetailStore] instance for the given [chatId]. */
    public fun create(chatId: String): ChatDetailStore = object :
        ChatDetailStore,
        Store<Intent, State, Nothing> by storeFactory.create(
            name = "ChatDetailStore",
            initialState = State(chatId = chatId),
            executorFactory = { ExecutorImpl(chatId) },
            reducer = { msg: Msg ->
                when (msg) {
                    is Msg.Loading -> copy(isLoading = true, error = null)
                    is Msg.MessagesLoaded -> copy(messages = msg.messages, isLoading = false)
                    is Msg.Sending -> copy(isSending = true)
                    is Msg.UserMessageAdded -> copy(messages = messages + msg.message)
                    is Msg.MessageSent -> copy(messages = messages + msg.message, isSending = false)
                    is Msg.Error -> copy(isLoading = false, isSending = false, error = msg.error)
                }
            },
        ) {}

    private sealed interface Msg {
        data object Loading : Msg

        data class MessagesLoaded(val messages: List<Message>) : Msg

        data object Sending : Msg

        data class UserMessageAdded(val message: Message) : Msg

        data class MessageSent(val message: Message) : Msg

        data class Error(val error: String) : Msg
    }

    private inner class ExecutorImpl(private val chatId: String) :
        CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.LoadMessages -> loadMessages()
                is Intent.SendMessage -> sendMessage(intent.content)
            }
        }

        private fun loadMessages() {
            dispatch(Msg.Loading)
            scope.launch {
                chatRepository.getMessages(chatId).fold(
                    onSuccess = { dispatch(Msg.MessagesLoaded(it)) },
                    onFailure = { dispatch(Msg.Error(it.message ?: "Load failed")) },
                )
            }
        }

        private fun sendMessage(content: String) {
            dispatch(Msg.Sending)
            val userMessage = Message(
                chatId = chatId,
                role = MessageRole.USER,
                content = content,
            )
            dispatch(Msg.UserMessageAdded(userMessage))
            scope.launch {
                sendMessageUseCase(chatId, content).fold(
                    onSuccess = { dispatch(Msg.MessageSent(it)) },
                    onFailure = { dispatch(Msg.Error(it.message ?: "Send failed")) },
                )
            }
        }
    }
}
