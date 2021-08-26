package com.mattg.pickem.utils

import androidx.appcompat.app.AppCompatDelegate

enum class DarkModeConfig(val value: Int) {
    ON(AppCompatDelegate.MODE_NIGHT_YES),
    OFF(AppCompatDelegate.MODE_NIGHT_NO),
    AUTO(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}