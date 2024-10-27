package com.saefulrdevs.dicodingstory.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.dicodingstory.R
import com.saefulrdevs.dicodingstory.databinding.ActivityMainBinding
import com.saefulrdevs.dicodingstory.viewmodel.main.AdapterStory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterStory: AdapterStory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRv() {
        binding.apply {
            val verticalLayout = LinearLayoutManager(this@MainActivity)
            rvStory.layoutManager = verticalLayout
            val itemFavoriteEventDecoration =
                DividerItemDecoration(this@MainActivity, verticalLayout.orientation)
            rvStory.addItemDecoration(itemFavoriteEventDecoration)
        }
    }

    private fun setupAdapter() {
        adapterStory = AdapterStory { storyId ->
            val bundle = Bundle().apply {
                storyId?.let { putString("storyId", it)}
            }

        }
        binding.rvStory.adapter = adapterStory
    }
}