package com.example.rcc.domain.entity

import com.example.rcc.domain.util.UuidGenerator
import kotlinx.serialization.Serializable

/**
 * Represents a message in a chat.
 *
 * @property id Unique identifier for the message.
 * @property chatId ID of the chat this message belongs to.
 * @property role Role of the message sender.
 * @property content Text content of the message.
 * @property timestamp When the message was sent (milliseconds since epoch).
 */
@Serializable
public data class Message(
    val id: String = UuidGenerator.randomUuid(),
    val chatId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = currentTimeMillis(),
)

/** Role of a message sender. */
@Serializable
public enum class MessageRole {
    /** Message from the user. */
    USER,

    /** Message from the assistant. */
    ASSISTANT,

    /** System message. */
    SYSTEM,
}
