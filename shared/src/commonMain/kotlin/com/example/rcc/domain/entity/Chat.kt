package com.example.rcc.domain.entity

import com.example.rcc.domain.util.UuidGenerator
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * Represents a chat session with Cloud Code.
 *
 * @property id Unique identifier for the chat.
 * @property sessionId Cloud Code session identifier.
 * @property createdAt Timestamp when chat was created (milliseconds since epoch).
 * @property lastActivity Timestamp of last activity (milliseconds since epoch).
 * @property title Display title for the chat.
 */
@Serializable
public data class Chat(
    val id: String = UuidGenerator.randomUuid(),
    val sessionId: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val lastActivity: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String = "Chat",
) {
    /** Returns true if this chat has a valid session ID. */
    val isValid: Boolean get() = sessionId.isNotEmpty()
}
