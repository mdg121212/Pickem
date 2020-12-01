package com.mattg.pickem.db.daos



import androidx.lifecycle.LiveData
import androidx.room.*
import com.mattg.pickem.db.ApiResponseCached

@Dao
interface ApiDao {

    @Query("SELECT * FROM cache")
    fun getApiCacheString() : LiveData<ApiResponseCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertNewCache(cache: ApiResponseCached)

    @Delete()
    fun deleteCache(cache: ApiResponseCached)

    @Update(onConflict = OnConflictStrategy.REPLACE)
     fun updateCache(cache: ApiResponseCached)
}