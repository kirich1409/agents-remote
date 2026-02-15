package com.example.rcc.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.rcc.features.chatdetail.component.ChatDetailComponent
import com.example.rcc.features.chatlist.component.ChatListComponent

/** Root component that manages the navigation child stack. */
public interface RootComponent {
    /** The navigation child stack containing the current screen hierarchy. */
    public val childStack: Value<ChildStack<*, Child>>

    /** Called when the user presses the back button. */
    public fun onBackClicked()

    /** Sealed class representing the possible child screens. */
    public sealed class Child {
        /** Child wrapping the chat list screen [component]. */
        public data class ChatListChild(val component: ChatListComponent) : Child()

        /** Child wrapping the chat detail screen [component]. */
        public data class ChatDetailChild(val component: ChatDetailComponent) : Child()
    }
}
