package com.example.rcc.features.chatlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rcc.domain.entity.Chat

@Composable
internal fun ChatItem(chat: Chat, onClick: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(chat.title) },
        supportingContent = { Text("Session: ${chat.sessionId}") },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        },
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
    )
}
