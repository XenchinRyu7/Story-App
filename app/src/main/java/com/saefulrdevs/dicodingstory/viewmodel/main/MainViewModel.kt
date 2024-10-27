package com.saefulrdevs.dicodingstory.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.dicodingstory.data.remote.model.Login
import com.saefulrdevs.dicodingstory.data.remote.model.Register
import com.saefulrdevs.dicodingstory.data.remote.repository.StoryRepository
import com.saefulrdevs.dicodingstory.data.remote.response.ListStoryItem
import com.saefulrdevs.dicodingstory.data.remote.response.LoginResult
import com.saefulrdevs.dicodingstory.data.remote.response.Story
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository
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

    fun register(newUser: Register) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = storyRepository.register(newUser)
            _isLoading.value = false
            result.onSuccess {
                _message.value = it.toString()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun login(login: Login) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = storyRepository.login(login)
            _isLoading.value = false
            result.onSuccess {
                _loginResult.value = it
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

}