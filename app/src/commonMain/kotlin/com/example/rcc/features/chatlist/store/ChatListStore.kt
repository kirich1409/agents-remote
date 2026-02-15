package com.example.rcc.features.chatlist.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.rcc.domain.entity.Chat
import com.example.rcc.features.chatlist.store.ChatListStore.Intent
import com.example.rcc.features.chatlist.store.ChatListStore.State

/** MVIKotlin store managing chat list state and intents. */
public interface ChatListStore : Store<Intent, State, Nothing> {
    /** Intents that can be dispatched to the chat list store. */
    public sealed interface Intent {
        /** Intent to load all chats from the server. */
        public data object LoadChats : Intent

        /** Intent to select a chat by its [id]. */
        public data class SelectChat(val id: String) : Intent

        /** Intent to create a new chat session. */
        public data object CreateNewChat : Intent

        /** Intent to delete a chat by its [id]. */
        public data class DeleteChat(val id: String) : Intent
    }

    /**
     * State of the chat list screen.
     *
     * @property chats list of available chats
     * @property isLoading whether a loading operation is in progress
     * @property error error message if the last operation failed, or null
     */
    public data class State(
        val chats: List<Chat> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
    )
}
