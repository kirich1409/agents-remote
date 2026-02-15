package com.example.rcc.domain.util

/**
 * Platform-specific UUID generator.
 *
 * Each platform provides its own implementation using native UUID libraries.
 */
public expect object UuidGenerator {
    /**
     * Generates a random UUID string.
     *
     * @return Random UUID in standard format (e.g., "550e8400-e29b-41d4-a716-446655440000").
     */
    public fun randomUuid(): String
}
