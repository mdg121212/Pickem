package com.mattg.pickem.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache")
data class ApiResponseCached(
    @PrimaryKey
    val dateTime: String,
    val content: String
)