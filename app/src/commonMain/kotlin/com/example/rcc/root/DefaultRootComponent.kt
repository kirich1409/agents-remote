package com.example.rcc.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.example.rcc.features.chatdetail.component.ChatDetailComponent
import com.example.rcc.features.chatdetail.component.DefaultChatDetailComponent
import com.example.rcc.features.chatlist.component.ChatListComponent
import com.example.rcc.features.chatlist.component.DefaultChatListComponent
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

internal class DefaultRootComponent(componentContext: ComponentContext) :
    RootComponent,
    ComponentContext by componentContext,
    KoinComponent {
    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.ChatList,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    override fun onBackClicked() {
        navigation.pop()
    }

    private fun createChild(config: Config, componentContext: ComponentContext): RootComponent.Child = when (config) {
        is Config.ChatList -> RootComponent.Child.ChatListChild(chatListComponent(componentContext))

        is Config.ChatDetail -> RootComponent.Child.ChatDetailChild(
            chatDetailComponent(componentContext, config.chatId),
        )
    }

    private fun chatListComponent(componentContext: ComponentContext): ChatListComponent = DefaultChatListComponent(
        componentContext = componentContext,
        onChatSelected = { chatId ->
            navigation.pushNew(Config.ChatDetail(chatId))
        },
    )

    private fun chatDetailComponent(componentContext: ComponentContext, chatId: String): ChatDetailComponent =
        DefaultChatDetailComponent(
            componentContext = componentContext,
            chatId = chatId,
            onBack = navigation::pop,
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object ChatList : Config

        @Serializable
        data class ChatDetail(val chatId: String) : Config
    }
}
