package com.example.rcc.features.chatdetail.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.rcc.domain.entity.Message
import com.example.rcc.features.chatdetail.store.ChatDetailStore.Intent
import com.example.rcc.features.chatdetail.store.ChatDetailStore.State

/** MVIKotlin store managing chat detail state and intents. */
public interface ChatDetailStore : Store<Intent, State, Nothing> {
    /** Intents that can be dispatched to the chat detail store. */
    public sealed interface Intent {
        /** Intent to load messages for the current chat. */
        public data object LoadMessages : Intent

        /** Intent to send a message with the given [content]. */
        public data class SendMessage(val content: String) : Intent
    }

    /**
     * State of the chat detail screen.
     *
     * @property chatId identifier of the current chat
     * @property messages list of messages in the chat
     * @property isLoading whether messages are being loaded
     * @property isSending whether a message is currently being sent
     * @property error error message if the last operation failed, or null
     */
    public data class State(
        val chatId: String = "",
        val messages: List<Message> = emptyList(),
        val isLoading: Boolean = false,
        val isSending: Boolean = false,
        val error: String? = null,
    )
}
