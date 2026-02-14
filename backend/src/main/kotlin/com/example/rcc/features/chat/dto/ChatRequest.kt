package com.example.rcc.features.chat.dto

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a new chat.
 *
 * @property sessionId Cloud Code session identifier for the new chat.
 */
@Serializable
public data class CreateChatRequest(
    val sessionId: String,
)

/**
 * Response DTO for chat information.
 *
 * Represents a chat session with metadata.
 *
 * @property id Unique identifier for the chat.
 * @property sessionId Cloud Code session identifier.
 * @property createdAt Timestamp when chat was created (milliseconds since epoch).
 * @property lastActivity Timestamp of last activity (milliseconds since epoch).
 * @property title Display title for the chat.
 */
@Serializable
public data class ChatResponse(
    val id: String,
    val sessionId: String,
    val createdAt: Long,
    val lastActivity: Long,
    val title: String,
)

/**
 * Request DTO for sending a message to a chat.
 *
 * @property content Text content of the message to send.
 */
@Serializable
public data class SendMessageRequest(
    val content: String,
)

/**
 * Response DTO for message information.
 *
 * Represents a message within a chat.
 *
 * @property id Unique identifier for the message.
 * @property chatId ID of the chat this message belongs to.
 * @property role Role of the message sender (USER, ASSISTANT, SYSTEM).
 * @property content Text content of the message.
 * @property timestamp When the message was sent (milliseconds since epoch).
 */
@Serializable
public data class MessageResponse(
    val id: String,
    val chatId: String,
    val role: String,
    val content: String,
    val timestamp: Long,
)

/**
 * Response DTO for error information.
 *
 * Represents an error response from the API.
 *
 * @property error Error message describing what went wrong.
 * @property details Optional additional details about the error.
 */
@Serializable
public data class ErrorResponse(
    val error: String,
    val details: String? = null,
)
