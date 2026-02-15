package com.example.rcc.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.rcc.data.api.RccApiClient
import com.example.rcc.data.repository.AppChatRepositoryImpl
import com.example.rcc.domain.repository.ChatRepository
import com.example.rcc.domain.usecase.CreateChatUseCase
import com.example.rcc.domain.usecase.GetChatsUseCase
import com.example.rcc.domain.usecase.SendMessageUseCase
import com.example.rcc.features.chatdetail.store.ChatDetailStoreFactory
import com.example.rcc.features.chatlist.store.ChatListStoreFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"

internal val appModule =
    module {
        single {
            HttpClient {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                    )
                }
            }
        }

        single { RccApiClient(httpClient = get(), baseUrl = DEFAULT_BASE_URL) }

        single<ChatRepository> { AppChatRepositoryImpl(apiClient = get()) }

        single<StoreFactory> { DefaultStoreFactory() }

        single { GetChatsUseCase(repository = get()) }
        single { CreateChatUseCase(repository = get()) }
        single { SendMessageUseCase(repository = get()) }

        single { ChatListStoreFactory(storeFactory = get(), getChatsUseCase = get(), createChatUseCase = get()) }
        single { ChatDetailStoreFactory(storeFactory = get(), chatRepository = get(), sendMessageUseCase = get()) }
    }
