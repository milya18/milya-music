package com.milya.milyamusic.ui.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.milya.milyamusic.databinding.FragmentPlaybackBinding
import com.milya.milyamusic.databinding.ItemTrackBinding
import com.milya.milyamusic.db.Track
import com.milya.milyamusic.playback.PlaybackConnectionManager

class PlaybackFragment : Fragment() {
    private var _binding: FragmentPlaybackBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerConnectionManager: PlaybackConnectionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val playViewModel = ViewModelProvider(this)[PlayViewModel::class.java]
        _binding = FragmentPlaybackBinding.inflate(inflater, container, false)

        // Bind the Media3 abstraction engine
        playerConnectionManager = PlaybackConnectionManager(requireContext())
        playerConnectionManager.connect { /* Audio pipeline is hot and ready */ }

        // Standard linear list initialization
        val adapter = TrackAdapter { track ->
            playerConnectionManager.playTrack(track)
        }

        binding.recyclerviewTracks.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewTracks.adapter = adapter

        // Bind architecture emissions straight to your adapter
        playViewModel.downloadedTracks.observe(viewLifecycleOwner) { tracks ->
            adapter.submitList(tracks)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerConnectionManager.disconnect()
        _binding = null
    }

    class TrackAdapter(private val onPlayClicked: (Track) -> Unit) :
        ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
            val binding = ItemTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TrackViewHolder(binding)
        }

        override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
            val track = getItem(position)
            holder.binding.textTrackTitle.text = track.title
            holder.binding.btnPlayTrack.setOnClickListener { onPlayClicked(track) }
        }
    }

    class TrackViewHolder(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root)

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem == newItem
    }
}