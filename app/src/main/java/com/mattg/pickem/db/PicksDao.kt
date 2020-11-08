package com.mattg.pickem.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PicksDao {

    @Query("SELECT * FROM picks WHERE week = :week")
    suspend fun getPicksByWeek(week: Int): Pick

    @Query("SELECT * FROM picks order by week")
     fun getPicksLiveData(): LiveData<Pick>

    @Query("SELECT * FROM picks order by week")
    suspend fun getAllPics(): List<Pick>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicks(picks: Pick)

    @Delete()
    suspend fun deletePick(pick: Pick)

    @Query("DELETE FROM picks")
    suspend fun clearPicks()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePicks(picks: Pick)
}