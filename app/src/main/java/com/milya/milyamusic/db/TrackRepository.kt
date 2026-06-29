package com.milya.milyamusic.db

import kotlinx.coroutines.flow.Flow

class TrackRepository(private val dao: TrackDao) {
    val allTracks: Flow<List<Track>> = dao.getAllTracks()

    suspend fun insertTrack(track: Track) = dao.insert(track)
    suspend fun updateLastPlayed(trackId: String, timestamp: Long) = dao.updateLastPlayed(trackId, timestamp)
    suspend fun getStaleTracks(cutoffTime: Long): List<Track> = dao.getUnplayedBefore(cutoffTime)
    suspend fun deleteTrack(trackId: String) = dao.deleteById(trackId)
}