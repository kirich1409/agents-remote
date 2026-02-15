package com.example.rcc.features.chatlist.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.rcc.features.chatlist.store.ChatListStore
import com.example.rcc.features.chatlist.store.ChatListStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal class DefaultChatListComponent(
    componentContext: ComponentContext,
    private val onChatSelected: (String) -> Unit,
) : ChatListComponent,
    ComponentContext by componentContext,
    KoinComponent {
    private val store: ChatListStore = get<ChatListStoreFactory>().create()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<ChatListStore.State> = store.stateFlow

    init {
        store.accept(ChatListStore.Intent.LoadChats)
    }

    override fun onChatClick(chatId: String) {
        onChatSelected(chatId)
    }

    override fun onNewChatClick() {
        store.accept(ChatListStore.Intent.CreateNewChat)
    }

    override fun onDeleteClick(chatId: String) {
        store.accept(ChatListStore.Intent.DeleteChat(chatId))
    }

    override fun onRefresh() {
        store.accept(ChatListStore.Intent.LoadChats)
    }
}
