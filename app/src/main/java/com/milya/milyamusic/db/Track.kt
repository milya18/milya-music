package com.milya.milyamusic.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: String,
    val title: String,
    val durationSeconds: Int,
    val thumbnailUrl: String,
    val addedAt: Long,
    val lastPlayedAt: Long? = null
)