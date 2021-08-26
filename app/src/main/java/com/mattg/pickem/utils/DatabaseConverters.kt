package com.mattg.pickem.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mattg.pickem.models.general.Game

object DatabaseConverters {

    fun toOptionValuesList(value: String): java.util.ArrayList<Game>? {
        if (value == null) {
            return (null)
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Game>>() {}.type
        return gson.fromJson(value, type)
    }

    fun fromValuesToString(value: java.util.ArrayList<Game>?): String? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<java.util.ArrayList<Game>>() {}.type

        return gson.toJson(value, type)
    }
}