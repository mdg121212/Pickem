package com.mattg.pickem.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PicksDao {

    @Query("SELECT * FROM picks WHERE week = :week")
    suspend fun getPicksByWeek(week: Int): Picks

    @Query("SELECT * FROM picks order by week")
     fun getPicksLiveData(): LiveData<Picks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicks(picks: Picks)

    @Delete()
    suspend fun deletePick(pick: Picks)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePicks(picks: Picks)
}