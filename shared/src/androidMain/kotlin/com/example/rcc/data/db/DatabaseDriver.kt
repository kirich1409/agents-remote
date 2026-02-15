package com.example.rcc.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.rcc.database.RemoteCloudCodeDb

/**
 * Database name for Android SQLite.
 */
private const val DATABASE_NAME = "rcc.db"

/**
 * Application context holder for Android database driver.
 */
private var _appContext: Context? = null

/**
 * Initializes Android database context.
 *
 * Must be called before accessing the database (typically in Application.onCreate()).
 *
 * @param context Application context.
 */
public fun initializeDatabaseContext(context: Context) {
    _appContext = context.applicationContext
}

/**
 * Gets initialized application context.
 *
 * @return Application context.
 * @throws IllegalStateException if context not initialized.
 */
private val appContext: Context
    get() = checkNotNull(_appContext) {
        "Database context not initialized. Call initializeDatabaseContext() first."
    }

/**
 * Creates Android SQLite driver.
 *
 * @return Android-specific SQL driver.
 * @throws IllegalStateException if context not initialized.
 */
public actual fun createDriver(): SqlDriver =
    AndroidSqliteDriver(RemoteCloudCodeDb.Schema, appContext, DATABASE_NAME)
