package com.example.data

import com.example.model.FavoriteStation
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    val allFavorites: Flow<List<FavoriteStation>> = favoriteDao.getAllFavorites()

    suspend fun addFavorite(stationId: String) {
        favoriteDao.insertFavorite(FavoriteStation(stationId = stationId))
    }

    suspend fun removeFavorite(stationId: String) {
        favoriteDao.deleteFavoriteById(stationId = stationId)
    }
}
