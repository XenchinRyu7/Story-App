package com.saefulrdevs.dicodingstory.view.main.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.saefulrdevs.dicodingstory.R
import com.saefulrdevs.dicodingstory.databinding.FragmentAddStoryBinding
import com.saefulrdevs.dicodingstory.utils.ResultState
import com.saefulrdevs.dicodingstory.utils.ViewModelFactory
import com.saefulrdevs.dicodingstory.utils.reduceFileImage
import com.saefulrdevs.dicodingstory.utils.uriToFile
import com.saefulrdevs.dicodingstory.view.main.camera.CameraActivity
import com.saefulrdevs.dicodingstory.view.main.camera.CameraActivity.Companion.CAMERAX_RESULT
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel

class AddStoryFragment : Fragment() {

    private lateinit var binding: FragmentAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var token: String

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    interface UploadCallback {
        fun onUploadSuccess()
    }

    private var uploadCallback: UploadCallback? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStoryBinding.inflate(inflater, container, false)

        token = arguments?.getString("token").toString()

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.apply {
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCameraX() }
            btnUpload.setOnClickListener { uploadImage() }
        }

        observeViewModel()

        return binding.root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imageView.setImageURI(it)
        }
    }

    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UploadCallback) {
            uploadCallback = context
        } else {
            throw RuntimeException("$context must implement UploadCallback")
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            try {
                val description = binding.tfDescription.text.toString()
                token.let {
                    mainViewModel.addNewStory("Bearer $token", description, imageFile)
                        .observe(viewLifecycleOwner) { result ->
                            if (result != null) {
                                when (result) {
                                    is ResultState.Loading -> {
                                        showLoading(true)
                                    }

                                    is ResultState.Success -> {
                                        showToast(result.data.message())
                                        showLoading(false)
                                        uploadCallback?.onUploadSuccess()
                                    }

                                    is ResultState.Error -> {
                                        showToast(result.error)
                                        showLoading(false)
                                    }
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                showToast(getString(R.string.error_image_upload))
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun observeViewModel() {
        mainViewModel.message.observe(viewLifecycleOwner) {
            showToast(it)
            Log.d("Message", "observeViewModel: $it")
            mainViewModel.clearSnackbarMessage()
        }
        mainViewModel.errorMessage.observe(viewLifecycleOwner) {
            showToast(it)
            Log.d("Message", "observeViewModel: $it")
            mainViewModel.clearErrorMessage()
        }
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}