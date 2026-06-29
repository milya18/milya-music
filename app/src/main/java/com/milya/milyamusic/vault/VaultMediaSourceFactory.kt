package com.milya.milyamusic.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.milya.milyamusic.db.Track
import java.io.File

class VaultMediaSourceFactory(private val context: Context) {
    private fun findLocalTrackFile(trackId: String): File {
        val tracksDir = File(context.filesDir, "tracks")
        val supportedExtensions = listOf("opus", "webm", "flac")

        for (ext in supportedExtensions) {
            val file = File(tracksDir, "$trackId.$ext")
            if (file.exists()) {
                return file
            }
        }

        // Fallback or explicit error if the file was deleted or never finished downloading
        throw IllegalStateException("Audio file not found in storage for track ID: $trackId")
    }

    /**
     * Converts a database Track entity into a playable Media3 ProgressiveMediaSource
     * pulling directly from the local filesystem container.
     */
    @OptIn(UnstableApi::class)
    fun createMediaSource(track: Track): ProgressiveMediaSource {
        // Locate the target .opus file in internal storage
        val trackFile = findLocalTrackFile(track.id)
        val fileUri = Uri.fromFile(trackFile)

        // Build metadata structures so system lock screens display the correct text
        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setDisplayTitle(track.title)
            .setArtist("Milya Downloader")
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(fileUri)
            .setMediaMetadata(metadata)
            .build()

        // Create a standard file-capable data source factory
        val dataSourceFactory = DefaultDataSource.Factory(context)

        // Return a progressive media source specialized for standalone local files
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
    }
}