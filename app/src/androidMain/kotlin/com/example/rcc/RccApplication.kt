package com.example.rcc

import android.app.Application
import com.example.rcc.di.appModule
import org.koin.core.context.startKoin

/** Android [Application] subclass that initializes Koin dependency injection. */
public class RccApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}
