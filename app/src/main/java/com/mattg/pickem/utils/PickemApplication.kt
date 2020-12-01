package com.mattg.pickem.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import timber.log.Timber
import java.util.*

class PickemApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.getString(
            "key_night_mode",
            "auto"
        ).apply {
            val mode = DarkModeConfig.valueOf(this!!.toUpperCase(Locale.US))
            Timber.i("*******mode = $mode")
            AppCompatDelegate.setDefaultNightMode(mode.value)
        }
    }



}