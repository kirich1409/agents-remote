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
    private val db: RemoteCloudCodeDb by lazy { RemoteCloudCodeDb(createDriver()) }

    /**
     * Gets the database instance.
     *
     * @return Database instance.
     */
    public fun getInstance(): RemoteCloudCodeDb = db
}
