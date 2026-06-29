package com.milya.milyamusic.download

import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YtDlpExtractor {
    /**
     * Resolves a YouTube URL to a direct stream URL and metadata via yt-dlp.
     * Executes on the IO dispatcher.
     */
    suspend fun extractInfo(youtubeUrl: String): Result<YtAudioInfo> = withContext(Dispatchers.IO) {
        try {
            val py = Python.getInstance()
            val module = py.getModule("yt_extractor")

            // Execute the python function
            val pyDict = module.callAttr("get_audio_info", youtubeUrl).asMap()

            // Map the Python dictionary back to Kotlin types safely
            val streamUrl = pyDict[Python.getInstance().builtins.callAttr("str", "stream_url")]?.toString()
                ?: throw IllegalStateException("Stream URL not found")

            val title = pyDict[Python.getInstance().builtins.callAttr("str", "title")]?.toString() ?: "Unknown"
            val duration = pyDict[Python.getInstance().builtins.callAttr("str", "duration")]?.toLong() ?: 0L
            val thumbnail = pyDict[Python.getInstance().builtins.callAttr("str", "thumbnail")]?.toString() ?: ""
            val headersMap = mutableMapOf<String, String>()
            val pyHeaders = pyDict[Python.getInstance().builtins.callAttr("str", "http_headers")]?.asMap()

            pyHeaders?.forEach { (key, value) ->
                if (key != null && value != null) {
                    headersMap[key.toString()] = value.toString()
                }
            }
            Result.success(
                YtAudioInfo(
                    streamUrl = streamUrl,
                    title = title,
                    durationSeconds = duration,
                    thumbnailUrl = thumbnail,
                    httpHeaders = headersMap
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}