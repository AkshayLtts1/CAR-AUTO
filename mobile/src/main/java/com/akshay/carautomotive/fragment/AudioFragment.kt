package com.akshay.carautomotive.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.akshay.carautomotive.view.MainActivity
import com.akshay.carautomotive.R
import com.akshay.carautomotive.viewModel.AudioAdapter
import com.akshay.carautomotive.databinding.FragmentAudioBinding

class AudioFragment : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_audio, container, false)
        val binding = FragmentAudioBinding.bind(view)

        binding.audioRV.setHasFixedSize(true)
        binding.audioRV.setItemViewCacheSize(10)
        binding.audioRV.layoutManager = LinearLayoutManager(requireContext())
        binding.audioRV.adapter = AudioAdapter(requireContext(), MainActivity.audioList)
        binding.totalAudios.text = "Total Audios: ${MainActivity.audioList.size}"
        return view
    }

}