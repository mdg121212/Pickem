package com.mattg.pickem.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "picks")
data class Pick (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val week: String,
    val name: String,
    val picks: String,
    val picksGamesOnly: String,
    val finalPoints: String
) {
    @Ignore
    constructor(
        week: String,
        name: String,
        picks: String,
        picksGamesOnly: String,
        finalPoints: String
    ): this(
        0,
        week,
        name,
        picks,
        picksGamesOnly,
        finalPoints
    )

}