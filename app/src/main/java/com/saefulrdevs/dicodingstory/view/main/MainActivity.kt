package com.saefulrdevs.dicodingstory.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.saefulrdevs.dicodingstory.R
import com.saefulrdevs.dicodingstory.databinding.ActivityMainBinding
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.view.authentication.AuthActivity
import com.saefulrdevs.dicodingstory.view.main.add.AddStoryFragment
import com.saefulrdevs.dicodingstory.view.main.detail.DetailFragment
import com.saefulrdevs.dicodingstory.viewmodel.main.AdapterStory
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel

class MainActivity : AppCompatActivity(), AddStoryFragment.UploadCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterStory: AdapterStory
    private lateinit var token: String

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

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

        token = intent.getStringExtra("token").toString()
        setupRv()
        val authHeader = "Bearer $token"
        mainViewModel.getAllStory(authHeader)
        observeViewModel()
        setupAdapter()

        supportFragmentManager.addOnBackStackChangedListener {
            val fragmentCount = supportFragmentManager.backStackEntryCount
            if (fragmentCount == 0) {
                binding.rvStory.visibility = View.VISIBLE
                binding.btnAddStory.visibility = View.VISIBLE
                supportActionBar?.title = "Dicoding Story"
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }

        binding.apply {
            btnAddStory.setOnClickListener {
                rvStory.visibility = View.GONE
                val bundle = Bundle().apply {
                    putString("token", token)
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, AddStoryFragment().apply {
                        arguments = bundle
                    })
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.title = "Add New Story"
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                btnAddStory.visibility = View.GONE
            }
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
            binding.rvStory.visibility = View.GONE
            val bundle = Bundle().apply {
                storyId?.let { putString("storyId", it) }
                putString("token", token)
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, DetailFragment().apply {
                    arguments = bundle
                })
                .addToBackStack(null)
                .commit()
            supportActionBar?.title = "Detail Story"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.rvStory.adapter = adapterStory
    }

    private fun observeViewModel() {
        mainViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        mainViewModel.listStory.observe(this) { listStory ->
            adapterStory.submitList(listStory)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onUploadSuccess() {
        supportFragmentManager.popBackStack()
        mainViewModel.getAllStory("Bearer $token")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_exit -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.title_exit))
                    .setMessage(resources.getString(R.string.description_exit))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                        finishAffinity()
                    }
                    .show()
                true
            }

            R.id.action_logout -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.title_logout))
                    .setMessage(resources.getString(R.string.description_logout))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                        mainViewModel.deleteAuthToken()
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}