package com.kp.optikjoyoabadi.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppOverride: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger(Level.ERROR)
            androidContext(this@AppOverride)
            //add modules here
            modules(
                dbModule,
                viewModelMod
            )
        }
    }
}