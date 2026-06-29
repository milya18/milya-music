package com.milya.milyamusic.ui.download

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.milya.milyamusic.download.DownloadWorker
import java.util.UUID

class DownloadViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)
    private val _currentWorkId = MutableLiveData<UUID?>()

    // Automatically reacts to worker updates, including setProgressAsync emissions
    val downloadWorkInfo: LiveData<WorkInfo?> = _currentWorkId.switchMap { uuid ->
        if (uuid == null) {
            MutableLiveData<WorkInfo?>(null)
        } else {
            workManager.getWorkInfoByIdLiveData(uuid)
        }
    }

    /**
     * Packages input configurations and sends the extraction task to the WorkManager system.
     */
    fun startDownload(youtubeUrl: String) {
        val inputData = Data.Builder()
            .putString("YOUTUBE_URL", youtubeUrl)
            .build()

        val downloadRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .setInputData(inputData)
            .build()

        workManager.enqueue(downloadRequest)

        // Notify observers of the updated active task reference
        _currentWorkId.value = downloadRequest.id
    }
}