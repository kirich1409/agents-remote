package com.example.rcc.di

import com.example.rcc.data.repository.ChatRepositoryImpl
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import com.example.rcc.features.chat.ChatHandler
import org.koin.dsl.module

/**
 * Koin dependency injection module.
 *
 * Defines all dependency bindings for the application, including:
 * - Repository implementations for data access
 * - Use cases for business logic
 * - Handler for request processing
 */
public val appModule =
    module {
        // Repository bindings
        /**
         * Provides ChatRepository implementation.
         *
         * Binds the ChatRepository interface to ChatRepositoryImpl for in-memory
         * chat data management. Single instance shared across the application.
         */
        single<ChatRepository> { ChatRepositoryImpl() }

        // Use case bindings
        /**
         * Provides GetChatsUseCase.
         *
         * Depends on ChatRepository for retrieving all chats.
         * Handles business logic for fetching chat data with error handling.
         */
        single { GetChatsUseCase(get()) }

        /**
         * Provides CreateChatUseCase.
         *
         * Depends on ChatRepository for creating new chats.
         * Validates session ID and handles chat creation with error handling.
         */
        single { CreateChatUseCase(get()) }

        /**
         * Provides SendMessageUseCase.
         *
         * Depends on ChatRepository for sending messages.
         * Validates chat ID and message content with error handling.
         */
        single { SendMessageUseCase(get()) }

        // Handler bindings
        /**
         * Provides ChatHandler.
         *
         * Orchestrates chat operations by delegating to use cases.
         * Converts domain entities to DTOs for API responses.
         * Depends on all chat use cases.
         */
        single {
            ChatHandler(
                getChatsUseCase = get(),
                createChatUseCase = get(),
                sendMessageUseCase = get(),
            )
        }
    }
