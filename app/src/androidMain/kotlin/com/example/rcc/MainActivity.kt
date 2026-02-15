package com.example.rcc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.example.rcc.root.DefaultRootComponent

/** Main Android activity that hosts the Compose UI. */
public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootComponent = DefaultRootComponent(defaultComponentContext())
        setContent {
            App(rootComponent)
        }
    }
}
