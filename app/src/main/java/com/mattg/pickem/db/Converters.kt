package com.mattg.pickem.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mattg.pickem.models.Game
import java.lang.reflect.Type
import java.util.*

class Converters {






        @TypeConverter
        fun fromString(value: String?): ArrayList<String?>? {
            val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun fromArrayList(list: ArrayList<String?>?): String? {
            val gson = Gson()
            return gson.toJson(list)
        }

        @TypeConverter
        fun gameFromString(value: String?): Game?{
            val listType: Type = object : TypeToken<Game?>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun fromGameToString(game: Game?): String? {
            val gson = Gson()
            return gson.toJson(game)
        }

        @TypeConverter
        fun fromValuesToString(value: ArrayList<Game>?) : String?
        {
            if (value == null) {
                return null
            }
            val gson = Gson()
           val type = object: TypeToken<ArrayList<Game>>() {}.type
            return gson.toJson(value, type);
        }

        @TypeConverter
       fun toOptionValuesList( value: String) : ArrayList<Game>?
        {
            if (value == null) {
                return (null)
            }
            val gson =  Gson()
            val type = object : TypeToken<List<Game>>() {
            }.type
            return gson.fromJson(value, type)
        }

}