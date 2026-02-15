package com.example.rcc.domain.util

import java.util.UUID

/**
 * Android/JVM UUID generator using java.util.UUID.
 */
public actual object UuidGenerator {
    /**
     * Generates a random UUID string using java.util.UUID.
     *
     * @return Random UUID in standard format.
     */
    public actual fun randomUuid(): String = UUID.randomUUID().toString()
}
