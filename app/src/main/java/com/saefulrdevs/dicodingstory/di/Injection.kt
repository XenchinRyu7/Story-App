package com.saefulrdevs.dicodingstory.di

import android.content.Context
import com.saefulrdevs.dicodingstory.data.local.AuthPreferences
import com.saefulrdevs.dicodingstory.data.local.dataStore
import com.saefulrdevs.dicodingstory.data.repository.StoryRepository
import com.saefulrdevs.dicodingstory.data.remote.retrofit.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
    fun provideAuthPreferences(context: Context): AuthPreferences {
        return AuthPreferences.getInstance(context.dataStore)
    }
}