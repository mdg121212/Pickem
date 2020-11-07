package com.mattg.pickem.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities =[Picks::class, ApiResponseCached::class], version = 2, exportSchema = false)

abstract class PicksDatabase : RoomDatabase() {

        abstract fun picksDao(): PicksDao
        abstract fun apiDao(): ApiDao

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