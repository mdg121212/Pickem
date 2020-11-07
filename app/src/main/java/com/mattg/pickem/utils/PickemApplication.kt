package com.mattg.pickem.utils

import android.app.Application
import timber.log.Timber

class PickemApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}