package com.example.rcc.features.chatlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rcc.features.chatlist.component.ChatListComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatListScreen(component: ChatListComponent, modifier: Modifier = Modifier) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chats") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = component::onNewChatClick) {
                Icon(Icons.Default.Add, contentDescription = "New chat")
            }
        },
        modifier = modifier,
    ) { padding ->
        when {
            state.isLoading && state.chats.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(state.error.orEmpty())
                }
            }

            else -> {
                LazyColumn(contentPadding = padding) {
                    items(state.chats, key = { it.id }) { chat ->
                        ChatItem(
                            chat = chat,
                            onClick = { component.onChatClick(chat.id) },
                            onDelete = { component.onDeleteClick(chat.id) },
                        )
                    }
                }
            }
        }
    }
}
