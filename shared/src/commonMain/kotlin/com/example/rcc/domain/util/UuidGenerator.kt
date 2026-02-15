package com.example.rcc.domain.util

/**
 * Generates a random UUID string.
 *
 * Each platform provides its own implementation using native UUID libraries.
 *
 * @return Random UUID in standard format (e.g., "550e8400-e29b-41d4-a716-446655440000").
 */
public expect fun randomUuid(): String
