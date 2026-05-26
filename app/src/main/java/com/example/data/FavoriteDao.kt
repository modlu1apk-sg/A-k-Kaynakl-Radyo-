package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.FavoriteStation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_stations ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteStation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteStation)

    @Query("DELETE FROM favorite_stations WHERE stationId = :stationId")
    suspend fun deleteFavoriteById(stationId: String)
}
