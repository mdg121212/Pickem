package com.mattg.pickem.utils

import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

const val SHARED_PREF_NAME = "pickem_pref"

object SharedPrefHelper {

    fun setSignUpSuccessParse(context: Context) {
        defaultPrefs(context).edit().putBoolean("signUpSuccess", true).apply()
    }

    fun setSignUpFailParse(context: Context) {
        defaultPrefs(context).edit().putBoolean("signUpSuccess", false).apply()
    }

    fun getSignUpSuccess(context: Context): Boolean {
        return defaultPrefs(context).getBoolean("signUpSuccess", false)
    }

    fun setShowExpert(context: Context, show: Boolean) {
        defaultPrefs(context).edit().putBoolean("showExpert", show).apply()
    }

    fun getShowExpert(context: Context): Boolean {
        return defaultPrefs(context).getBoolean("showExpert", false)
    }

    fun nukeUserData(context: Context) {
        defaultPrefs(context).edit().remove("parseUserName").remove("parsePassword").apply()
    }

    fun addParseUserToPrefs(context: Context, userName: String, userPassword: String) {
        defaultPrefs(context).edit().putString("parseUserName", userName)
            .putString("parsePassword", userPassword).apply()

    }

    fun getParseUser(context: Context): Pair<String?, String?> {
        val prefs = defaultPrefs(context)
        val userName = prefs.getString("parseUserName", "none")
        val userPassword = prefs.getString("parsePassword", "none")
        return Pair(userName, userPassword)
    }

    fun setRememberMeParse(context: Context) {
        defaultPrefs(context).edit().putBoolean("isRemember", true).apply()
    }

    fun setDontRememberParse(context: Context) {
        defaultPrefs(context).edit().putBoolean("isRemember", false).apply()
    }

    fun setLoginSuccessParse(context: Context) {
        defaultPrefs(context).edit().putBoolean("loginSucceed", true).apply()
    }

    fun setLoginFailParse(context: Context) {
        defaultPrefs(context).edit().putBoolean("loginSucceed", false).apply()
    }

    fun getLoginResultParse(context: Context): Boolean {
        return defaultPrefs(context).getBoolean("loginSucceed", false)
    }

    fun getRememberMe(context: Context): Boolean {
        return defaultPrefs(context).getBoolean("isRemember", false)
    }

    fun defaultPrefs(context: Context): SharedPreferences {

//        val mainKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()

        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//        return EncryptedSharedPreferences.create(
//            context,
//            SHARED_PREF_NAME,
//            mainKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
    }

    fun addWeekToPrefs(context: Context, weekString: String) {
        val prefs = defaultPrefs(context)
        val editor = prefs.edit()
        Timber.i(",,,,,,,,,,,,,current week to prefs is $weekString")
        editor.putString("currentWeek", weekString).apply()
    }

    fun addLastOrCurrentWeekToPrefs(context: Context, weekString: String) {
        defaultPrefs(context).edit().putString("lastWeek", weekString).apply()
    }

    fun addDateToCheckToPrefs(context: Context, timeString: String, weekString: String) {
        defaultPrefs(context).edit().putString("dateToCheck", timeString)
            .putString("dateToCheckWeek", weekString).apply()
    }

    fun getDateToCheckFromPrefs(context: Context): String? {
        return defaultPrefs(context).getString("dateToCheck", "null")
    }

    fun clearDateToCheckFromPrefs(context: Context) {
        val prefs = defaultPrefs(context)
        val editor = prefs.edit()
        editor.clear()
        editor.remove("dateToCheck")
        editor.remove("dateToCheckWeek")
        editor.apply()
    }

    fun getLastOrCurrentWeekFromPrefs(context: Context): String? {
        return defaultPrefs(context).getString("lastWeek", "null")
    }

    fun getWeekFromPrefs(context: Context): String? {
        return defaultPrefs(context).getString("currentWeek", "null")
    }

    fun clearWeekFromPrefs(context: Context) {
        val prefs = defaultPrefs(context)
        val editor = prefs.edit()
        editor.clear()
        editor.remove("currentWeek")
        editor.remove("lastWeek")
        editor.apply()
    }

}