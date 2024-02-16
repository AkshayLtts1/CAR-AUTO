package com.akshay.carautomotive.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.akshay.carautomotive.view.MainActivity
import com.akshay.carautomotive.R
import com.akshay.carautomotive.databinding.FragmentVideoBinding
import com.akshay.carautomotive.viewModel.VideoAdapter

class VideoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        val binding = FragmentVideoBinding.bind(view)

        binding.VideoRV.setHasFixedSize(true)
        binding.VideoRV.setItemViewCacheSize(10)
        binding.VideoRV.layoutManager = LinearLayoutManager(context)
        binding.VideoRV.adapter = VideoAdapter(requireContext(), MainActivity.videoList)
        binding.totalVideos.text = "Total Videos: ${MainActivity.videoList.size}"
        return view
    }
}