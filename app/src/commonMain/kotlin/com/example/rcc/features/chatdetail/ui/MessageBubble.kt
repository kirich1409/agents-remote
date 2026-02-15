package com.example.rcc.features.chatdetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole

private val BUBBLE_MAX_WIDTH = 280.dp
private val BUBBLE_CORNER = 12.dp

@Composable
internal fun MessageBubble(message: Message, modifier: Modifier = Modifier) {
    val isUser = message.role == MessageRole.USER
    val arrangement = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = arrangement,
    ) {
        Surface(
            shape = RoundedCornerShape(BUBBLE_CORNER),
            color = if (isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.widthIn(max = BUBBLE_MAX_WIDTH),
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}
