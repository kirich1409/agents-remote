package com.example.rcc.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.rcc.database.RemoteCloudCodeDb

/**
 * Database name for persistent backend storage.
 */
private const val DATABASE_NAME = "rcc-backend.db"

/**
 * Creates JVM SQLite driver.
 *
 * Uses in-memory database by default for MVP.
 * For production, switch to persistent storage: `jdbc:sqlite:$DATABASE_NAME`
 *
 * @return JVM-specific SQL driver.
 */
public actual fun createDriver(): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        RemoteCloudCodeDb.Schema.create(this)
    }
