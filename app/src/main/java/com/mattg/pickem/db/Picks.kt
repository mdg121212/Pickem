package com.mattg.pickem.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "picks")
data class Picks (
    @PrimaryKey
    val week: Int,
    val name: String,
    val picks: String,
    val finalPoints: Int
) {
    //add secondary constructor for auto
}