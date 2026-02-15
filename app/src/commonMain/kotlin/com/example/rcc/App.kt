package com.example.rcc

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.example.rcc.features.chatdetail.ui.ChatDetailScreen
import com.example.rcc.features.chatlist.ui.ChatListScreen
import com.example.rcc.root.RootComponent
import com.example.rcc.theme.RccTheme

/** Root composable function for the RCC application. */
@Composable
public fun App(component: RootComponent) {
    RccTheme {
        Children(
            stack = component.childStack,
            animation = stackAnimation(fade()),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.ChatListChild ->
                    ChatListScreen(instance.component)

                is RootComponent.Child.ChatDetailChild ->
                    ChatDetailScreen(instance.component)
            }
        }
    }
}
