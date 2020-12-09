package com.mattg.pickem.ui.detailui


import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mattg.pickem.R
import com.mattg.pickem.utils.DarkModeConfig

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
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

    private fun shouldEnableDarkMode(darkModeConfig: DarkModeConfig) {
        when (darkModeConfig) {
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