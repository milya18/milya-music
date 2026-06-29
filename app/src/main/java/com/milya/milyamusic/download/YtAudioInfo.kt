package com.milya.milyamusic.download

data class YtAudioInfo(
    val streamUrl: String,
    val title: String,
    val durationSeconds: Long,
    val thumbnailUrl: String,
    val httpHeaders: Map<String, String>
)