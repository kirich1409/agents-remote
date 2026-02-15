package com.example.rcc.features.chatdetail.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.rcc.features.chatdetail.store.ChatDetailStore
import com.example.rcc.features.chatdetail.store.ChatDetailStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal class DefaultChatDetailComponent(
    componentContext: ComponentContext,
    private val chatId: String,
    private val onBack: () -> Unit,
) : ChatDetailComponent,
    ComponentContext by componentContext,
    KoinComponent {
    private val store: ChatDetailStore = get<ChatDetailStoreFactory>().create(chatId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<ChatDetailStore.State> = store.stateFlow

    init {
        store.accept(ChatDetailStore.Intent.LoadMessages)
    }

    override fun onSendMessage(content: String) {
        store.accept(ChatDetailStore.Intent.SendMessage(content))
    }

    override fun onBackClick() {
        onBack()
    }
}
