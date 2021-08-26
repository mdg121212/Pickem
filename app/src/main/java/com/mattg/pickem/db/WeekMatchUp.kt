package com.mattg.pickem.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matchups")
data class WeekMatchUp(
    @PrimaryKey
    val timestamp: String,
    val games: String,
    val week: Int
)