package com.saefulrdevs.dicodingstory.view.main.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.saefulrdevs.dicodingstory.R
import com.saefulrdevs.dicodingstory.databinding.FragmentDetailBinding
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        val storyId = arguments?.getString("storyId")
        val token = arguments?.getString("token")

        if (storyId != null && token != null) {
            mainViewModel.getStoryById(storyId, "Bearer $token")
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        mainViewModel.story.observe(viewLifecycleOwner) {
            binding.apply {
                Glide.with(requireContext())
                    .load(it.photoUrl)
                    .into(imageView)
                tvName.text = it.name
                tvDescription.text = it.description
            }
        }
    }
}