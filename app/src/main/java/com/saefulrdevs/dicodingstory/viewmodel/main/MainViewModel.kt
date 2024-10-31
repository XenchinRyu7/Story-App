package com.saefulrdevs.dicodingstory.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.dicodingstory.data.local.AuthPreferences
import com.saefulrdevs.dicodingstory.data.remote.model.Login
import com.saefulrdevs.dicodingstory.data.remote.model.Register
import com.saefulrdevs.dicodingstory.data.repository.StoryRepository
import com.saefulrdevs.dicodingstory.data.remote.response.ListStoryItem
import com.saefulrdevs.dicodingstory.data.remote.response.LoginResult
import com.saefulrdevs.dicodingstory.data.remote.response.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun clearSnackbarMessage() {
        _errorMessage.value = ""
        _message.value = ""
    }

    fun getAllStory(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = storyRepository.getAllStory(token)
            _isLoading.value = false
            result.onSuccess {
                _listStory.value = it
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun getStoryById(storyId: String, token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = storyRepository.getStoryById(storyId, token)
            _isLoading.value = false
            result.onSuccess {
                _story.value = it
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun addNewStory(token: String, description: String, file: File) =
        storyRepository.addNewStory(token, description, file)

    fun register(newUser: Register) = storyRepository.register(newUser)

    fun login(login: Login) = storyRepository.login(login)

    fun getAuthToken(): LiveData<String?> {
        return authPreferences.getAuthToken().asLiveData()
    }

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authPreferences.saveAuthToken(token)
        }
    }

    fun deleteAuthToken() {
        viewModelScope.launch {
            authPreferences.deleteAuthToken()
        }
    }

    fun clearMessage() {
        _message.value = ""
    }

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }
}