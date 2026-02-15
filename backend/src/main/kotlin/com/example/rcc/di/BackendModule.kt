package com.example.rcc.di

import com.example.rcc.data.repository.ChatRepositoryImpl
import com.example.rcc.domain.repository.ChatRepository
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Koin dependency injection module for the backend.
 *
 * Uses annotation-based configuration with component scanning.
 * Classes annotated with @Single in the `com.example.rcc` package
 * are automatically discovered and registered.
 */
@Module
@ComponentScan("com.example.rcc")
public class BackendModule {
    /** Provides the in-memory [ChatRepository] implementation. */
    @Single
    public fun chatRepository(): ChatRepository = ChatRepositoryImpl()
}
