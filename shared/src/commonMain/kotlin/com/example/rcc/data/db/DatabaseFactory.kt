package com.example.rcc.data.db

import app.cash.sqldelight.db.SqlDriver
import com.example.rcc.database.RemoteCloudCodeDb

/**
 * Platform-specific database driver factory.
 *
 * Each platform (Android, JVM) provides its own implementation.
 */
public expect fun createDriver(): SqlDriver

/**
 * Database instance factory.
 *
 * Provides thread-safe singleton access to the database.
 */
public object DatabaseFactory {
    @Volatile
    private var instance: RemoteCloudCodeDb? = null

    /**
     * Gets or creates the database instance.
     *
     * Thread-safe double-checked locking pattern.
     *
     * @return Database instance.
     */
    public fun getInstance(): RemoteCloudCodeDb =
        instance ?: synchronized(this) {
            instance ?: RemoteCloudCodeDb(createDriver()).also { instance = it }
        }

    /**
     * Resets the database instance for testing purposes.
     *
     * **Warning**: Only use in tests. Not thread-safe with getInstance().
     */
    internal fun resetForTesting() {
        synchronized(this) {
            instance = null
        }
    }
}
