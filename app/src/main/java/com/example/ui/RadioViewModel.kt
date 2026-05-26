package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DefaultStations
import com.example.data.FavoriteRepository
import com.example.model.RadioStation
import com.example.player.PlayerState
import com.example.player.RadioPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RadioViewModel(application: Application) : AndroidViewModel(application) {

    private val playerManager = RadioPlayerManager(application)
    private val favoriteRepository: FavoriteRepository

    init {
        val database = AppDatabase.getDatabase(application)
        favoriteRepository = FavoriteRepository(database.favoriteDao())
    }

    // Filter controls
    private val _selectedCategory = MutableStateFlow("Tümü")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Favorites stream mapping to List<String> of IDs
    val favoriteIds: StateFlow<Set<String>> = favoriteRepository.allFavorites
        .map { list -> list.map { it.stationId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Player States from player manager
    val currentStation: StateFlow<RadioStation?> = playerManager.currentStation
    val playerState: StateFlow<PlayerState> = playerManager.playerState

    // Filtered Radio Stations Flow
    val radioStations: StateFlow<List<RadioStation>> = combine(
        _selectedCategory,
        _searchQuery,
        favoriteIds
    ) { category, query, favs ->
        DefaultStations.list.filter { station ->
            val matchesCategory = if (category == "Tümü") {
                true
            } else if (category == "Favoriler") {
                favs.contains(station.id)
            } else {
                station.category == category
            }

            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                station.name.contains(query, ignoreCase = true) ||
                        station.frequency.contains(query, ignoreCase = true) ||
                        station.category.contains(query, ignoreCase = true) ||
                        station.description.contains(query, ignoreCase = true)
            }

            matchesCategory && matchesSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DefaultStations.list
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Playback functions
    fun playStation(station: RadioStation) {
        playerManager.play(station)
    }

    fun togglePlayPause() {
        val state = playerState.value
        val station = currentStation.value
        if (state is PlayerState.Playing) {
            playerManager.pause()
        } else if (state is PlayerState.Paused) {
            playerManager.resume()
        } else if (station != null) {
            playerManager.play(station)
        }
    }

    fun stopPlayback() {
        playerManager.stop()
    }

    // Favorites Interaction
    fun toggleFavorite(stationId: String) {
        viewModelScope.launch {
            if (favoriteIds.value.contains(stationId)) {
                favoriteRepository.removeFavorite(stationId)
            } else {
                favoriteRepository.addFavorite(stationId)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
