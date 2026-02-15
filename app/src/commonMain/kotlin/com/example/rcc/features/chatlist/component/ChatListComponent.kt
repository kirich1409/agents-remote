package com.example.rcc.features.chatlist.component

import com.example.rcc.features.chatlist.store.ChatListStore
import kotlinx.coroutines.flow.StateFlow

/** Component interface for the chat list screen. */
public interface ChatListComponent {
    /** Observable state of the chat list. */
    public val state: StateFlow<ChatListStore.State>

    /** Called when a chat is selected by the user. */
    public fun onChatClick(chatId: String)

    /** Called when the user requests to create a new chat. */
    public fun onNewChatClick()

    /** Called when the user requests to delete a chat. */
    public fun onDeleteClick(chatId: String)

    /** Called when the user pulls to refresh the chat list. */
    public fun onRefresh()
}
