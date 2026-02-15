package com.example.rcc.di

import com.example.rcc.data.repository.ChatRepositoryImpl
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import com.example.rcc.features.chat.ChatHandler
import com.example.rcc.features.chat.WebSocketHandler
import org.koin.dsl.module

/**
 * Koin dependency injection module.
 *
 * Defines all dependency bindings for the application, including:
 * - Repository implementations for data access
 * - Use cases for business logic
 * - Handler for request processing
 */
public val appModule: org.koin.core.module.Module =
    module {
        // Repository bindings
        single<ChatRepository> { ChatRepositoryImpl() }

        // Use case bindings
        single { GetChatsUseCase(get()) }
        single { CreateChatUseCase(get()) }
        single { SendMessageUseCase(get()) }

        // Handler bindings
        single {
            ChatHandler(
                getChatsUseCase = get(),
                createChatUseCase = get(),
                sendMessageUseCase = get(),
            )
        }
        single { WebSocketHandler() }
    }
