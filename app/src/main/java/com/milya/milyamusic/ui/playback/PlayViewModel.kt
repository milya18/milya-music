package com.milya.milyamusic.ui.playback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.milya.milyamusic.db.AppDatabase
import com.milya.milyamusic.db.Track
import com.milya.milyamusic.db.TrackRepository

class PlayViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = TrackRepository(database.trackDao())

    // Convert Room Flow into clean LiveData matching the template architecture
    val downloadedTracks: LiveData<List<Track>> = repository.allTracks.asLiveData()
}