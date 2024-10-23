package com.saefulrdevs.storyapp.data.remote.retrofit

import com.saefulrdevs.storyapp.data.remote.model.Login
import com.saefulrdevs.storyapp.data.remote.model.NewStory
import com.saefulrdevs.storyapp.data.remote.model.Register
import com.saefulrdevs.storyapp.data.remote.response.AddNewStoryResponse
import com.saefulrdevs.storyapp.data.remote.response.ListStoryResponse
import com.saefulrdevs.storyapp.data.remote.response.LoginResponse
import com.saefulrdevs.storyapp.data.remote.response.RegisterResponse
import com.saefulrdevs.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Body request: Register
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Body request: Login
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Body description: NewStory,
        @Part photo: MultipartBody.Part,
        @Header("Authorization") token: String
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

