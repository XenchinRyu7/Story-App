package com.saefulrdevs.dicodingstory.view.authentication.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.saefulrdevs.dicodingstory.data.remote.model.Register
import com.saefulrdevs.dicodingstory.databinding.FragmentRegisterBinding
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.view.main.MainActivity
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupUI()
        observeViewModel()
        observeSnackbarMessages()
        return binding.root
    }

    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        binding.apply {
            val name = tfName.editText?.text.toString()
            val email = tfEmail.editText?.text.toString()
            val password = tfPassword.editText?.text.toString()
            val verifPassword = tfVerifPassword.editText?.text.toString()

            btnRegister.isEnabled = name.isNotEmpty() && email.isNotEmpty() &&
                    password.isNotEmpty() && verifPassword.isNotEmpty()

            if (password == verifPassword) {
                val newUser = Register(name, email, password)
                mainViewModel.register(newUser)
            } else {
                tfVerifPassword.error = "Password tidak sama"
            }
        }
    }

    private fun observeViewModel() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
