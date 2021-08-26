package com.mattg.pickem

import android.app.Application
import android.content.Context

open class App : Application() {
    val PREFS_NAME = "pickemprefs"
    val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

}