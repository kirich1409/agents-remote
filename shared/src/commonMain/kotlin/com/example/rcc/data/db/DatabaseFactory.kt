package com.example.rcc.data.db

import app.cash.sqldelight.db.SqlDriver
import com.example.rcc.database.RemoteCloudCodeDb

/**
 * Platform-specific database driver factory.
 *
 * Each platform (Android, JVM, iOS) provides its own implementation.
 */
public expect fun createDriver(): SqlDriver

/**
 * Database instance factory.
 *
 * Provides singleton access to the database.
 */
public object DatabaseFactory {
    private var instance: RemoteCloudCodeDb? = null

    /**
     * Gets or creates the database instance.
     *
     * @return Database instance.
     */
    public fun getInstance(): RemoteCloudCodeDb = instance ?: RemoteCloudCodeDb(createDriver()).also { instance = it }

    /**
     * Resets the database instance for testing purposes.
     */
    internal fun resetForTesting() {
        instance = null
    }
}
