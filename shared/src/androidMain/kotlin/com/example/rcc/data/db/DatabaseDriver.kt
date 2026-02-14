package com.example.rcc.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.rcc.database.RemoteCloudCodeDb

/**
 * Application context for Android database driver.
 *
 * Must be initialized before accessing the database.
 */
public lateinit var appContext: Context

/**
 * Creates Android SQLite driver.
 *
 * @return Android-specific SQL driver.
 */
public actual fun createDriver(): SqlDriver = AndroidSqliteDriver(RemoteCloudCodeDb.Schema, appContext, "rcc.db")
