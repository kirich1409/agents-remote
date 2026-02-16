package com.example.rcc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.example.rcc.di.KoinApp
import com.example.rcc.di.httpClientModule
import com.example.rcc.root.DefaultRootComponent
import org.koin.core.context.stopKoin
import org.koin.plugin.module.dsl.startKoin

/** Desktop application entry point. Initializes Koin and launches the Compose window. */
public fun main() {
    startKoin<KoinApp> {
        modules(httpClientModule)
    }

    val lifecycle = LifecycleRegistry()
    val rootComponent = DefaultRootComponent(DefaultComponentContext(lifecycle = lifecycle))

    application {
        Window(
            onCloseRequest = {
                stopKoin()
                exitApplication()
            },
            title = "RCC",
        ) {
            App(rootComponent)
        }
    }
}
