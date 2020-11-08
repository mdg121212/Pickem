package com.mattg.pickem.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "picks")
data class Pick (
    @PrimaryKey
    val week: String,
    val name: String,
    val picks: String,
    val finalPoints: Int
)