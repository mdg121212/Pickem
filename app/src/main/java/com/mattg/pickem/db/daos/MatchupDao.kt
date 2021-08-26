package com.mattg.pickem.db.daos

import androidx.room.*
import com.mattg.pickem.db.WeekMatchUp

@Dao
interface MatchupDao {

    @Query("SELECT * FROM matchups")
    suspend fun getMatchups(): List<WeekMatchUp>

    @Query("DELETE FROM matchups")
    suspend fun deleteMatchups()

    @Delete
    suspend fun deleteMatchup(matchUps: WeekMatchUp)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMatchups(matchUps: WeekMatchUp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchups(matchUps: WeekMatchUp)
}