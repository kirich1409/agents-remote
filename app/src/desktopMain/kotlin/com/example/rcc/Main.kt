package com.example.rcc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.example.rcc.di.appModule
import com.example.rcc.root.DefaultRootComponent
import org.koin.core.context.startKoin

/** Desktop application entry point. Initializes Koin and launches the Compose window. */
public fun main() {
    startKoin {
        modules(appModule)
    }

    val lifecycle = LifecycleRegistry()
    val rootComponent = DefaultRootComponent(DefaultComponentContext(lifecycle = lifecycle))

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "RCC",
        ) {
            App(rootComponent)
        }
    }
}
