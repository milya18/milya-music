package com.milya.milyamusic.ui.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.milya.milyamusic.R
import com.milya.milyamusic.databinding.FragmentDownloadBinding

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val downloadViewModel = ViewModelProvider(this)[DownloadViewModel::class.java]

        _binding = FragmentDownloadBinding.inflate(inflater, container, false)

        binding.downloadButton.setOnClickListener {
            val url = binding.downloadUrlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                downloadViewModel.startDownload(url)
            } else {
                Toast.makeText(context, getString(R.string.error_empty_url), Toast.LENGTH_SHORT).show()
            }
        }

        // Observe progress data updates emitted while the worker is actively processing
        downloadViewModel.downloadWorkInfo.observe(viewLifecycleOwner) { workInfo ->
            if (workInfo != null) {
                binding.textDownloadStatus.text = when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> getString(R.string.status_queued)

                    WorkInfo.State.RUNNING -> {
                        val stage = workInfo.progress.getString("STAGE")
                        val progress = workInfo.progress.getInt("PROGRESS", 0)

                        // Render UI updates mapping cleanly to current task operations
                        when (stage) {
                            "FETCHING" -> "Fetching audio metadata..."
                            "DOWNLOADING" -> "Downloading: $progress%"
                            "CONVERTING" -> "Converting audio format: $progress%"
                            else -> getString(R.string.status_running)
                        }
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        binding.downloadUrlInput.text.clear()
                        getString(R.string.status_success)
                    }
                    WorkInfo.State.FAILED -> getString(R.string.status_failed)
                    else -> getString(R.string.status_idle)
                }
            } else {
                binding.textDownloadStatus.text = getString(R.string.status_idle)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}