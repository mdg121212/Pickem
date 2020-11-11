package com.mattg.pickem.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities =[Pick::class, ApiResponseCached::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PicksDatabase : RoomDatabase() {

        abstract fun picksDao(): PicksDao
        abstract fun apiDao(): ApiDao
      //  abstract fun matchupDao(): MatchupDao

    //singleton
    companion object {
        @Volatile private var INSTANCE: PicksDatabase?= null

        fun getInstance(context: Context) : PicksDatabase {
            synchronized(this){

                var instance = INSTANCE

                if(instance == null) instance = Room.databaseBuilder(
                    context.applicationContext,
                    PicksDatabase::class.java,
                    "picks.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }

}