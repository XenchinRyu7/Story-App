package com.saefulrdevs.dicodingstory.data.remote.retrofit

import com.saefulrdevs.dicodingstory.data.remote.model.Login
import com.saefulrdevs.dicodingstory.data.remote.model.NewStory
import com.saefulrdevs.dicodingstory.data.remote.model.Register
import com.saefulrdevs.dicodingstory.data.remote.response.AddNewStoryResponse
import com.saefulrdevs.dicodingstory.data.remote.response.ListStoryResponse
import com.saefulrdevs.dicodingstory.data.remote.response.LoginResponse
import com.saefulrdevs.dicodingstory.data.remote.response.RegisterResponse
import com.saefulrdevs.dicodingstory.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: Register
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body request: Login
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<AddNewStoryResponse>

    @Multipart
    @POST("stories/guest")
    suspend fun addNewStoryWithGuestAccount(
        @Body description: NewStory,
        @Part photo: MultipartBody.Part
    ): Response<AddNewStoryResponse>

    @GET("stories/{id}")
    suspend fun getStoryById(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<StoryResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): Response<ListStoryResponse>
}

