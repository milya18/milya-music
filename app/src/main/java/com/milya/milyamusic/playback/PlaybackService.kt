package com.milya.milyamusic.playback

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize ExoPlayer instance bound to the service lifecycle
        player = ExoPlayer.Builder(this).build()

        // 2. Pair it up to an active MediaSession
        player?.let { exoPlayer ->
            mediaSession = MediaSession.Builder(this, exoPlayer).build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    // Crucial for cleaning up system audio focuses when dismissed or destroyed
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}