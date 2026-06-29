package com.milya.milyamusic.compress

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import com.arthenica.ffmpegkit.FFmpegKitConfig
import kotlinx.coroutines.withContext
import java.io.File

class AudioConverter {
    /**
     * Transcodes any audio file to standard Opus format.
     * Executes synchronously on the IO dispatcher.
     */
    suspend fun transcodeToOpus(
        inputFile: File,
        outputFile: File,
        profile: CompressionProfile = CompressionProfile.MUSIC_HQ,
        totalDurationSeconds: Long,
        onProgress: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {

        // FIXED: Removed duplication, fixed missing space breaks, structured syntax clearly
        val command = when {
            profile == CompressionProfile.MUSIC_LOSSLESS -> {
                // Lossless extraction uses the native flac encoder flags
                "-y -loglevel warning -i \"${inputFile.absolutePath}\" -c:a flac \"${outputFile.absolutePath}\""
            }
            profile.isTranscodeRequired -> {
                // Lossy Opus configuration pipeline
                "-y -loglevel warning -i \"${inputFile.absolutePath}\" " +
                        "-c:a libopus " +
                        "-b:a ${profile.bitrate} " +
                        "-ac ${profile.channels} " +
                        "-vbr constrained " +
                        "\"${outputFile.absolutePath}\""
            }
            else -> {
                // Safe absolute fallback string copy command if called by mistake
                "-y -loglevel warning -i \"${inputFile.absolutePath}\" -c copy \"${outputFile.absolutePath}\""
            }
        }

        // Set up the statistics callback
        FFmpegKitConfig.enableStatisticsCallback { statistics ->
            if (totalDurationSeconds > 0) {
                val progressMs = statistics.time
                val totalMs = totalDurationSeconds * 1000
                val percentage = ((progressMs.toFloat() / totalMs.toFloat()) * 100).toInt()

                onProgress(percentage.coerceIn(0, 100))
            }
        }

        val session = FFmpegKit.execute(command)

        // Always disable the callback when done to avoid memory leaks
        FFmpegKitConfig.enableStatisticsCallback(null)

        if (ReturnCode.isSuccess(session.returnCode)) {
            Result.success(outputFile)
        } else {
            Result.failure(Exception("FFmpeg failed: ${session.failStackTrace}"))
        }
    }
}