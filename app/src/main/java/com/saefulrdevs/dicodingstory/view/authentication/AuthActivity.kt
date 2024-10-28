package com.saefulrdevs.dicodingstory.view.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.saefulrdevs.dicodingstory.R
import com.saefulrdevs.dicodingstory.databinding.ActivityAuthBinding
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.view.authentication.login.LoginFragment
import com.saefulrdevs.dicodingstory.view.authentication.register.RegisterFragment
import com.saefulrdevs.dicodingstory.view.main.MainActivity
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        checkAuth()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val fragmentCount = supportFragmentManager.backStackEntryCount
            if (fragmentCount == 0) {
                supportActionBar?.title = "Dicoding Story"
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }

        binding.apply {
            btnLogin.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, LoginFragment())
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.title = "Login"
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }

            btnRegister.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, RegisterFragment())
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.title = "Register"
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun checkAuth() {
        mainViewModel.getAuthToken().observe(this) { token ->
            if (token != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("token", token)
                startActivity(intent)
            }
        }
    }
}