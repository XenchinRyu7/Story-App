package com.saefulrdevs.dicodingstory.view.authentication.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saefulrdevs.dicodingstory.databinding.FragmentLoginBinding
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.saefulrdevs.dicodingstory.data.remote.model.Login
import com.saefulrdevs.dicodingstory.view.main.MainActivity

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupUI()
        observeViewModel()
        observeSnackbarMessages()
        return binding.root
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.tfEmail.editText?.text.toString()
        val password = binding.tfPassword.editText?.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            val loginRequest = Login(email, password)
            mainViewModel.login(loginRequest)
            binding.btnLogin.isEnabled = true
        } else {
            binding.btnLogin.isEnabled = false
        }
    }

    private fun observeViewModel() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        mainViewModel.loginResult.observe(viewLifecycleOwner) { loginResult ->
            mainViewModel.saveAuthToken(loginResult.token ?: "")
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("token", loginResult.token)
            startActivity(intent)
        }
    }

    private fun observeSnackbarMessages() {
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                mainViewModel.clearSnackbarMessage()
            }
        }
        mainViewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                mainViewModel.clearSnackbarMessage()
            }
        }
    }
}
