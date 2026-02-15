package com.example.rcc.features.chatdetail.component

import com.example.rcc.features.chatdetail.store.ChatDetailStore
import kotlinx.coroutines.flow.StateFlow

/** Component interface for the chat detail screen. */
public interface ChatDetailComponent {
    /** Observable state of the chat detail. */
    public val state: StateFlow<ChatDetailStore.State>

    /** Called when the user sends a message with the given [content]. */
    public fun onSendMessage(content: String)

    /** Called when the user navigates back from the chat detail. */
    public fun onBackClick()
}
