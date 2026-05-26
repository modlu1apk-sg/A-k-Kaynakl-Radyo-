package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stations")
data class FavoriteStation(
    @PrimaryKey val stationId: String,
    val timestamp: Long = System.currentTimeMillis()
)
