package com.example.rcc.data.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for Chat entity.
 *
 * Used for serialization and network/database communication.
 */
@Serializable
public data class ChatDto(
    val id: String,
    val sessionId: String,
    val createdAt: Long,
    val lastActivity: Long,
    val title: String = "Chat",
)

/**
 * Data transfer object for Message entity.
 *
 * Used for serialization and network/database communication.
 */
@Serializable
public data class MessageDto(
    val id: String,
    val chatId: String,
    val role: String,
    val content: String,
    val timestamp: Long,
)
