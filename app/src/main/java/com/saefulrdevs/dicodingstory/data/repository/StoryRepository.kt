package com.saefulrdevs.dicodingstory.data.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.saefulrdevs.dicodingstory.data.remote.model.Login
import com.saefulrdevs.dicodingstory.data.remote.model.Register
import com.saefulrdevs.dicodingstory.data.remote.response.AddNewStoryResponse
import com.saefulrdevs.dicodingstory.data.remote.response.ListStoryItem
import com.saefulrdevs.dicodingstory.data.remote.response.LoginResult
import com.saefulrdevs.dicodingstory.data.remote.response.RegisterResponse
import com.saefulrdevs.dicodingstory.data.remote.response.Story
import com.saefulrdevs.dicodingstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import androidx.lifecycle.liveData
import com.saefulrdevs.dicodingstory.data.remote.response.LoginResponse
import com.saefulrdevs.dicodingstory.utils.ResultState
import retrofit2.HttpException

class StoryRepository(
    private val apiService: ApiService
) {

    fun register(newUser: Register) = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(newUser)

            if (response.isSuccessful) {
                val successMessage = response.body()?.message ?: "Registration successful"
                emit(ResultState.Success(successMessage))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                val errorMessage = errorResponse?.message ?: "Unknown error occurred"
                emit(ResultState.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(ResultState.Error("Unexpected error: ${e.message}"))
        }
    }

    fun login(login: Login) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.login(login)

            if (successResponse.isSuccessful) {
                val loginResult = successResponse.body()?.loginResult ?: LoginResult()
                emit(ResultState.Success(loginResult))
            } else {
                val errorBody = successResponse.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                emit(ResultState.Error(errorResponse.message ?: "Unknown error occurred"))
            }
        } catch (e: HttpException) {
            emit(ResultState.Error("Network error: ${e.message()}"))
        } catch (e: Exception) {
            emit(ResultState.Error("Unexpected error: ${e.message}"))
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

    fun addNewStory(
        token: String,
        description: String,
        imageFile: File
    ) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.addNewStory(token, requestBody, multipartBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddNewStoryResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
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