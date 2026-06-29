package com.milya.milyamusic.compress

enum class CompressionProfile(
    val profileName: String,
    val bitrate: String?,     // Null means variable/lossless/original
    val channels: Int,
    val codec: String?,       // Target codec for FFmpeg, null for original copy
    val isTranscodeRequired: Boolean
) {
    // --- LOSSY PROFILES ---
    VOICE_PODCAST("Voice/Podcast", "16k", 1, "libopus", true),
    MUSIC_LQ("Lossy Low Quality", "48k", 2, "libopus", true),
    MUSIC_HQ("Lossy High Quality", "64k", 2, "libopus", true),
    MUSIC_MAX_LOSSY("Lossy Transparent", "128k", 2, "libopus", true),

    // --- LOSSLESS PROFILE (FFmpeg Transcode to FLAC) ---
    MUSIC_LOSSLESS("Lossless Archive", null, 2, "flac", true),

    // --- BEST ORIGINAL STREAM (Bypass FFmpeg Transcoding Completely) ---
    BEST_ORIGINAL_AUDIO("Best Original Audio", null, 2, null, false)
}