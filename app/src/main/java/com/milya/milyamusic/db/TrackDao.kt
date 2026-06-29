package com.milya.milyamusic.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track)

    // Flow automatically emits updates to the UI whenever the table changes
    @Query("SELECT * FROM tracks ORDER BY addedAt DESC")
    fun getAllTracks(): Flow<List<Track>>

    // Used by your future EvictionWorker to delete old tracks
    @Query("SELECT * FROM tracks WHERE lastPlayedAt < :cutoffTime AND lastPlayedAt IS NOT NULL")
    suspend fun getUnplayedBefore(cutoffTime: Long): List<Track>

    // Used by ExoPlayer's VaultDataSource when a track is opened
    @Query("UPDATE tracks SET lastPlayedAt = :timestamp WHERE id = :trackId")
    suspend fun updateLastPlayed(trackId: String, timestamp: Long)

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: String)
}