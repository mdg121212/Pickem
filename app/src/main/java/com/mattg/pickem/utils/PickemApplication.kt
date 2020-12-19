package com.mattg.pickem.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.mattg.pickem.BuildConfig
import com.parse.Parse
import timber.log.Timber
import java.util.*

const val sharedPrefsFile = "pickem_pref"
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
                .applicationId(BuildConfig.APP_KEY)
                .clientKey(BuildConfig.CLIENT_KEY)
                .server("https://parseapi.back4app.com")
                .build()
        )


    }



}