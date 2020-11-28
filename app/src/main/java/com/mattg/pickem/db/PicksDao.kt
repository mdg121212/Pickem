package com.mattg.pickem.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PicksDao {

    @Query("SELECT * FROM picks WHERE week LIKE :week")
    suspend fun getPicksByWeek(week: String): List<Pick>

    @Query("SELECT * FROM picks order by week")
     fun getPicksLiveData(): LiveData<Pick>

    @Query("SELECT * FROM picks order by week")
    suspend fun getAllPics(): List<Pick>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPicks(picks: Pick)

    @Delete()
    suspend fun deletePick(pick: Pick)

    @Query("DELETE FROM picks")
    suspend fun clearPicks()

    @Query("DELETE FROM picks where id = :id")
    suspend fun deletePickById(id: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePicks(picks: Pick)
}