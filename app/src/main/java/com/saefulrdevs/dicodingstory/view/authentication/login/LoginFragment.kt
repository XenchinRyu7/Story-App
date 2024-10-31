package com.saefulrdevs.dicodingstory.view.authentication.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.saefulrdevs.dicodingstory.databinding.FragmentLoginBinding
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel
import androidx.fragment.app.viewModels
import com.saefulrdevs.dicodingstory.R
import com.saefulrdevs.dicodingstory.data.remote.model.Login
import com.saefulrdevs.dicodingstory.utils.ResultState
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
            mainViewModel.login(loginRequest).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            val loginResult = result.data
                            showToast(getString(R.string.login_success))
                            showLoading(false)
                            mainViewModel.saveAuthToken(loginResult.token ?: "")
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.putExtra("token", loginResult.token)
                            startActivity(intent)
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } else {
            binding.tfEmail.error = getString(R.string.email_required)
            binding.tfPassword.error = getString(R.string.password_required)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
