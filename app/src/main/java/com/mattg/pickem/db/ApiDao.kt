package com.mattg.pickem.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ApiDao {

    @Query("SELECT * FROM picks cache")
    fun getApiCacheString(): LiveData<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewCache(cache: String)

    @Delete()
    suspend fun deleteCache(cache: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCache(cache: String)
}