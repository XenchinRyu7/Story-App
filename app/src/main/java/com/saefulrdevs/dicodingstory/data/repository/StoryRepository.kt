package com.saefulrdevs.dicodingstory.data.repository

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.saefulrdevs.dicodingstory.data.remote.model.Login
import com.saefulrdevs.dicodingstory.data.remote.model.Register
import com.saefulrdevs.dicodingstory.data.remote.response.ListStoryItem
import com.saefulrdevs.dicodingstory.data.remote.response.LoginResult
import com.saefulrdevs.dicodingstory.data.remote.response.RegisterResponse
import com.saefulrdevs.dicodingstory.data.remote.response.Story
import com.saefulrdevs.dicodingstory.data.remote.response.StoryResponse
import com.saefulrdevs.dicodingstory.data.remote.retrofit.ApiService

class StoryRepository(
    private val apiService: ApiService
) {

    suspend fun register(newUser: Register): Result<String> {
        return try {
            val response = apiService.register(newUser)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Registration successful")
            } else {
                val errorResponse = try {
                    response.errorBody()?.string()?.let {
                        Gson().fromJson(it, RegisterResponse::class.java)
                    }
                } catch (e: JsonSyntaxException) {
                    null
                }

                val errorMessage = errorResponse?.message ?: "Unknown error occurred"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(login: Login): Result<LoginResult> {
        return try {
            val response = apiService.login(login)
            if (response.isSuccessful) {
                Result.success(response.body()?.loginResult ?: LoginResult())
            } else {
                val errorResponse = try {
                    response.errorBody()?.string()?.let {
                        Gson().fromJson(it, RegisterResponse::class.java)
                    }
                } catch (e: JsonSyntaxException) {
                    null
                }

                val errorMessage = errorResponse?.message ?: "Unknown error occurred"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllStory(token: String): Result<List<ListStoryItem>> {
        return try {
            val response = apiService.getAllStories(token)
            if (response.isSuccessful) {
                Result.success(response.body()?.listStory ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStoryById(storyId: String, token: String): Result<Story> {
        return try {
            val response = apiService.getStoryById(storyId, token)
            if (response.isSuccessful) {
                Result.success(response.body()?.story ?: Story())
            } else {
                Result.failure(Exception("Failed to load data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }
                .also { instance = it }
    }
}