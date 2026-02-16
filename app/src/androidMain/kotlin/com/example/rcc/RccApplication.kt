package com.example.rcc

import android.app.Application
import com.example.rcc.di.KoinApp
import com.example.rcc.di.httpClientModule
import org.koin.core.context.stopKoin
import org.koin.plugin.module.dsl.startKoin

/** Android [Application] subclass that initializes Koin dependency injection. */
public class RccApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<KoinApp> {
            modules(httpClientModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}
