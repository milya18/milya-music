package com.milya.milyamusic.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.milya.milyamusic.compress.AudioConverter
import com.milya.milyamusic.compress.CompressionProfile
import com.milya.milyamusic.db.AppDatabase
import com.milya.milyamusic.db.Track
import com.milya.milyamusic.db.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File

class DownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    private val httpClient = OkHttpClient()

    override suspend fun doWork(): Result {
        val youtubeUrl = inputData.getString("YOUTUBE_URL") ?: return Result.failure()
        val trackId = id.toString()
        val profileStr = inputData.getString("COMPRESSION_PROFILE")
        val profile = try {
            if (profileStr != null) CompressionProfile.valueOf(profileStr) else CompressionProfile.BEST_ORIGINAL_AUDIO
        } catch (e: IllegalArgumentException) {
            CompressionProfile.BEST_ORIGINAL_AUDIO
        }

        val extension = when (profile) {
            CompressionProfile.MUSIC_LOSSLESS -> "flac"
            CompressionProfile.BEST_ORIGINAL_AUDIO -> "webm"
            else -> "opus"
        }

        try {
            // STAGE 1: Fetching metadata
            setProgressAsync(workDataOf("STAGE" to "FETCHING", "PROGRESS" to 0))

            val extractor = YtDlpExtractor()
            val audioInfo = extractor.extractInfo(youtubeUrl).getOrThrow()

            val finalOutputFile = File(applicationContext.filesDir, "tracks/$trackId.$extension")
            finalOutputFile.parentFile?.mkdirs()

            // STAGE 2: Downloading with streaming callback progress updates
            if (!profile.isTranscodeRequired) {
                // --- FAST-PATH BRANCH: Best Original Audio (Skip FFmpeg completely) ---
                downloadStreamToFile(
                    audioInfo.streamUrl,
                    audioInfo.httpHeaders,
                    finalOutputFile
                ) { downloadPercent ->
                    setProgressAsync(
                        workDataOf(
                            "STAGE" to "DOWNLOADING",
                            "PROGRESS" to downloadPercent
                        )
                    )
                }
            } else {
                // --- SLOW-PATH BRANCH: Transcoding Required ---
                val tempFile = File(applicationContext.cacheDir, "temp_$trackId.raw")

                // STAGE 2: Download raw stream to cache
                downloadStreamToFile(
                    audioInfo.streamUrl,
                    audioInfo.httpHeaders,
                    tempFile
                ) { downloadPercent ->
                    setProgressAsync(
                        workDataOf(
                            "STAGE" to "DOWNLOADING",
                            "PROGRESS" to downloadPercent
                        )
                    )
                }

                // STAGE 3: Convert file via FFmpeg
                setProgressAsync(workDataOf("STAGE" to "CONVERTING", "PROGRESS" to 0))
                val converter = AudioConverter()

                converter.transcodeToOpus(
                    inputFile = tempFile,
                    outputFile = finalOutputFile,
                    profile = profile,
                    totalDurationSeconds = audioInfo.durationSeconds
                ) { convertPercent ->
                    setProgressAsync(
                        workDataOf(
                            "STAGE" to "CONVERTING",
                            "PROGRESS" to convertPercent
                        )
                    )
                }.getOrThrow()

                // Housekeeping: delete temporary disk space immediately
                tempFile.delete()
            }

            val db = AppDatabase.getDatabase(applicationContext)
            val repository = TrackRepository(db.trackDao())

            repository.insertTrack(
                Track(
                    id = trackId,
                    title = audioInfo.title,
                    durationSeconds = audioInfo.durationSeconds.toInt(),
                    thumbnailUrl = audioInfo.thumbnailUrl,
                    addedAt = System.currentTimeMillis()
                )
            )

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }

    private suspend fun downloadStreamToFile(
        streamUrl: String,
        extractedHeaders: Map<String, String>?,
        destination: File,
        onProgressUpdate: suspend (Int) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val requestBuilder = Request.Builder().url(streamUrl)

            if (!extractedHeaders.isNullOrEmpty()) {
                extractedHeaders.forEach { (key, value) ->
                    requestBuilder.header(key, value)
                }
            } else {
                requestBuilder.header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
                )
                requestBuilder.header("Accept", "*/*")
                requestBuilder.header("Connection", "keep-alive")
            }

            val request = requestBuilder.build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Failed to download stream: ${response.code}")
                }

                val body = response.body ?: throw IllegalStateException("Empty response body")
                val totalBytes = body.contentLength()
                val source = body.source()

                destination.sink().buffer().use { sink ->
                    val buffer = okio.Buffer()
                    var totalBytesRead = 0L
                    var bytesRead: Long
                    var lastEmittedProgress = -1

                    while (source.read(buffer, 65536L).also { bytesRead = it } != -1L) {
                        sink.write(buffer, bytesRead)
                        totalBytesRead += bytesRead

                        if (totalBytes > 0) {
                            val progress =
                                ((totalBytesRead * 100) / totalBytes).toInt().coerceIn(0, 100)
                            if (progress != lastEmittedProgress) {
                                lastEmittedProgress = progress
                                onProgressUpdate(progress)
                            }
                        }
                    }
                }
            }
        }
    }
}
