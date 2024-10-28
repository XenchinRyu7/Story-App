package com.saefulrdevs.dicodingstory.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saefulrdevs.dicodingstory.data.local.AuthPreferences
import com.saefulrdevs.dicodingstory.data.local.dataStore
import com.saefulrdevs.dicodingstory.data.repository.StoryRepository
import com.saefulrdevs.dicodingstory.di.Injection
import com.saefulrdevs.dicodingstory.viewmodel.main.MainViewModel

class ViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val authPreferences: AuthPreferences
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepository, authPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val storyRepository = Injection.provideStoryRepository(context)
                val authPreferences = Injection.provideAuthPreferences(context)
                instance ?: ViewModelFactory(storyRepository, authPreferences)
            }.also { instance = it }
    }
}