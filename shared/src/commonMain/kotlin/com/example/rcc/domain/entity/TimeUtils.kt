package com.example.rcc.domain.entity

import kotlin.time.Clock

/**
 * Returns current time in milliseconds since epoch.
 * KMP-compatible wrapper around Clock.System.
 */
internal fun currentTimeMillis(): Long =
    Clock.System.now().toEpochMilliseconds()
