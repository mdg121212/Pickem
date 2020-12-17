package com.mattg.pickem.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.parse.Parse
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

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("eg4nAdShJCVsRjT7QCyoA3ohlSOfxq7rhEsvquqD")
                .clientKey("mKiBRFdZI2136yMFZnvwSEFcbyfefMX9VhOV5RYG")
                .server("https://parseapi.back4app.com")
                .build()
        )


    }



}