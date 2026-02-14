package com.example.rcc.domain.entity

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
    val id: String = @OptIn(ExperimentalUuidApi::class) Uuid.random().toString(),
    val chatId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
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
