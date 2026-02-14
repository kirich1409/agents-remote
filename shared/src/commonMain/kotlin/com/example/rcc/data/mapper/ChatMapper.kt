package com.example.rcc.data.mapper

import com.example.rcc.data.dto.ChatDto
import com.example.rcc.data.dto.MessageDto
import com.example.rcc.domain.entity.Chat
import com.example.rcc.domain.entity.Message
import com.example.rcc.domain.entity.MessageRole

/**
 * Mapper for converting between domain entities and DTOs.
 */
public object ChatMapper {
    /**
     * Converts ChatDto to domain Chat entity.
     *
     * @param dto Data transfer object to convert.
     * @return Domain Chat entity.
     */
    public fun toDomain(dto: ChatDto): Chat =
        Chat(
            id = dto.id,
            sessionId = dto.sessionId,
            createdAt = dto.createdAt,
            lastActivity = dto.lastActivity,
            title = dto.title,
        )

    /**
     * Converts domain Chat entity to ChatDto.
     *
     * @param domain Domain entity to convert.
     * @return Data transfer object.
     */
    public fun toDto(domain: Chat): ChatDto =
        ChatDto(
            id = domain.id,
            sessionId = domain.sessionId,
            createdAt = domain.createdAt,
            lastActivity = domain.lastActivity,
            title = domain.title,
        )

    /**
     * Converts MessageDto to domain Message entity.
     *
     * @param dto Data transfer object to convert.
     * @return Domain Message entity.
     */
    public fun messageToDomain(dto: MessageDto): Message =
        Message(
            id = dto.id,
            chatId = dto.chatId,
            role = MessageRole.valueOf(dto.role),
            content = dto.content,
            timestamp = dto.timestamp,
        )

    /**
     * Converts domain Message entity to MessageDto.
     *
     * @param domain Domain entity to convert.
     * @return Data transfer object.
     */
    public fun messageToDto(domain: Message): MessageDto =
        MessageDto(
            id = domain.id,
            chatId = domain.chatId,
            role = domain.role.name,
            content = domain.content,
            timestamp = domain.timestamp,
        )
}
