package com.saefulrdevs.dicodingstory.view.authentication.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.saefulrdevs.dicodingstory.R
import com.saefulrdevs.dicodingstory.data.remote.model.Register
import com.saefulrdevs.dicodingstory.databinding.FragmentRegisterBinding
import com.saefulrdevs.dicodingstory.utils.ResultState
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.view.authentication.login.LoginFragment
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
                mainViewModel.register(newUser).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultState.Loading -> showLoading(true)
                            is ResultState.Success -> {
                                showToast(result.data)
                                showLoading(false)

                                val loginFragment = LoginFragment()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.nav_host_fragment_activity_main, loginFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }

                            is ResultState.Error -> {
                                showToast(result.error)
                                showLoading(false)
                            }
                        }
                    }
                }
            } else {
                tfVerifPassword.error = "Password tidak sama"
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
