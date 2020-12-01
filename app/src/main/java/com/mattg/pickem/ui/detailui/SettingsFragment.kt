package com.mattg.pickem.ui.detailui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mattg.pickem.MainActivity
import com.mattg.pickem.R
import com.mattg.pickem.utils.DarkModeConfig
import com.mattg.pickem.utils.PickemApplication
import timber.log.Timber
import java.util.logging.Level.OFF

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onAttach(context: Context) {
        val context = context
        super.onAttach(context)
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Timber.i("*****shared preferences change listener activated/fired")
        val darkModeString = "key_night_mode"
        key?.let{
            if(it == darkModeString && context != null) sharedPreferences.let { pref ->
                val darkModeValues = context?.resources?.getStringArray(R.array.night_mode_value)
                when(pref?.getString(darkModeString, darkModeValues?.get(0))) {
                    darkModeValues?.get(0) -> { shouldEnableDarkMode(DarkModeConfig.AUTO) }
                    darkModeValues?.get(1) -> {shouldEnableDarkMode(DarkModeConfig.ON)}
                    darkModeValues?.get(2) -> {shouldEnableDarkMode(DarkModeConfig.OFF)}
                    else -> shouldEnableDarkMode(DarkModeConfig.AUTO)
                }
            }
        }
    }

    fun shouldEnableDarkMode(darkModeConfig: DarkModeConfig){
        when(darkModeConfig){
            DarkModeConfig.ON -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            DarkModeConfig.OFF -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            DarkModeConfig.AUTO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
        requireActivity().recreate()
    }
}