package com.example.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.model.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class PlayerState {
    object Idle : PlayerState()
    object Buffering : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    data class Error(val message: String) : PlayerState()
}

@OptIn(UnstableApi::class)
class RadioPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    
    private val _currentStation = MutableStateFlow<RadioStation?>(null)
    val currentStation: StateFlow<RadioStation?> = _currentStation.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_BUFFERING -> {
                    _playerState.value = PlayerState.Buffering
                }
                Player.STATE_READY -> {
                    if (exoPlayer?.isPlaying == true) {
                        _playerState.value = PlayerState.Playing
                    } else {
                        _playerState.value = PlayerState.Paused
                    }
                }
                Player.STATE_ENDED -> {
                    _playerState.value = PlayerState.Paused
                }
                Player.STATE_IDLE -> {
                    _playerState.value = PlayerState.Idle
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val ex = exoPlayer ?: return
            if (ex.playbackState == Player.STATE_READY) {
                _playerState.value = if (isPlaying) PlayerState.Playing else PlayerState.Paused
            } else if (ex.playbackState == Player.STATE_BUFFERING) {
                _playerState.value = PlayerState.Buffering
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            val friendlyError = when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Bağlantı Hatası: İnternetinizi kontrol edin."
                PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "Yayın akışı şu anda aktif değil veya geçici olarak çevrimdışı."
                else -> "Oynatma hatası oluştu: Yayına bağlanılamıyor."
            }
            _playerState.value = PlayerState.Error(friendlyError)
        }
    }

    private fun initPlayer() {
        if (exoPlayer == null) {
            val buildExoPlayer = ExoPlayer.Builder(context.applicationContext).build()
            buildExoPlayer.addListener(playerListener)
            exoPlayer = buildExoPlayer
        }
    }

    fun play(station: RadioStation) {
        initPlayer()
        val player = exoPlayer ?: return

        val mimeType = if (station.streamUrl.contains(".m3u8")) {
            MimeTypes.APPLICATION_M3U8
        } else {
            MimeTypes.AUDIO_MPEG
        }

        // Check if we are already playing this exact stream to avoid restarting
        if (_currentStation.value?.id == station.id && playerState.value is PlayerState.Playing) {
            return
        }

        _currentStation.value = station
        _playerState.value = PlayerState.Buffering

        try {
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(station.streamUrl))
                .setMimeType(mimeType)
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            _playerState.value = PlayerState.Error("Yayına bağlanamadı: ${e.localizedMessage}")
        }
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        exoPlayer?.play()
    }

    fun stop() {
        exoPlayer?.stop()
        _playerState.value = PlayerState.Idle
        _currentStation.value = null
    }

    fun release() {
        exoPlayer?.let {
            it.removeListener(playerListener)
            it.release()
            exoPlayer = null
        }
        _playerState.value = PlayerState.Idle
        _currentStation.value = null
    }
}
