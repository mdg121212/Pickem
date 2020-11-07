package com.mattg.pickem.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mattg.pickem.models.Game
import java.util.*

class Converters {



    companion object {
        val gson = Gson()

        @TypeConverter
        @JvmStatic
        fun fromListtoString(value: List<Game>?): String? {
            if(value != null){
                return gson.toJson(value)
            }
                else return null

        }

        @TypeConverter
        fun fromStringToList(value: String?): List<String> {
            if(value == null){
                return Collections.emptyList()
            }
            val pickType = object: TypeToken<List<Game>>(){}.type

            return gson.fromJson(value, pickType)
        }


    }
}