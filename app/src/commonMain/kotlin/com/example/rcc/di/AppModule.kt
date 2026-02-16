package com.example.rcc.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.rcc.data.api.RccApiClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.dsl.module

private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"
private const val REQUEST_TIMEOUT_MS = 180_000L
private const val CONNECT_TIMEOUT_MS = 10_000L
private const val SOCKET_TIMEOUT_MS = 180_000L

/**
 * DSL module for HttpClient with proper resource cleanup.
 * Uses onClose callback to ensure HttpClient is properly closed on app shutdown.
 */
internal val httpClientModule =
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
                install(HttpTimeout) {
                    requestTimeoutMillis = REQUEST_TIMEOUT_MS
                    connectTimeoutMillis = CONNECT_TIMEOUT_MS
                    socketTimeoutMillis = SOCKET_TIMEOUT_MS
                }
            }
        } onClose { httpClient ->
            httpClient?.close()
        }
    }

@Module
@ComponentScan("com.example.rcc")
internal class AppModule {
    @Single
    internal fun rccApiClient(httpClient: HttpClient): RccApiClient =
        RccApiClient(httpClient = httpClient, baseUrl = DEFAULT_BASE_URL)

    @Single
    internal fun storeFactory(): StoreFactory = DefaultStoreFactory()
}

/** Koin application configuration for the app. */
@KoinApplication(modules = [AppModule::class])
internal class KoinApp
