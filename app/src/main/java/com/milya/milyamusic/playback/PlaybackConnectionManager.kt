package com.milya.milyamusic.playback

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.milya.milyamusic.db.Track
import com.milya.milyamusic.player.VaultMediaSourceFactory

class PlaybackConnectionManager(private val context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    var mediaController: MediaController? = null

    private val mediaSourceFactory = VaultMediaSourceFactory(context)

    /**
     * Binds the application UI to the background PlaybackService.
     * Call this in your Activity's onStart() or a ViewModel initialization.
     */
    fun connect(onReady: (MediaController) -> Unit) {
        if (mediaController != null) return

        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            try {
                val controller = controllerFuture?.get()
                mediaController = controller
                if (controller != null) {
                    onReady(controller)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    /**
     * Loads a track from your Room DB, converts it via the Vault factory, and plays it.
     */
    @OptIn(UnstableApi::class)
    fun playTrack(track: Track) {
        val controller = mediaController ?: return

        // Convert Room entity to playable media source
        val mediaSource = mediaSourceFactory.createMediaSource(track)

        // MediaController implements the standard Player interface, making commands identical
        controller.setMediaItem(mediaSource.mediaItem)
        controller.prepare()
        controller.play()
    }

    /**
     * Unbinds the controller safely.
     * Call this in your Activity's onStop() to prevent leaks.
     */
    fun disconnect() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        mediaController = null
        controllerFuture = null
    }
}