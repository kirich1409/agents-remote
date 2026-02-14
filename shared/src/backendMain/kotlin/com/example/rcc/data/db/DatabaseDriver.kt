package com.example.rcc.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.rcc.database.RemoteCloudCodeDb

/**
 * Creates JVM SQLite driver with in-memory database.
 *
 * @return JVM-specific SQL driver.
 */
public actual fun createDriver(): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        RemoteCloudCodeDb.Schema.create(this)
    }
