package com.example.rcc.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.rcc.database.RemoteCloudCodeDb

private const val DATABASE_NAME = "remote_cloud_code.db"

public actual fun createDriver(): SqlDriver = NativeSqliteDriver(RemoteCloudCodeDb.Schema, DATABASE_NAME)
