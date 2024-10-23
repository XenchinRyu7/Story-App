package com.saefulrdevs.storyapp.data.remote.retrofit

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("events")
    suspend fun getAllActiveEvent(
        @Query("active") active: Int = 1
    ): Response<EventResponse>

    @GET("events")
    suspend fun getAllFinishedEvent(
        @Query("active") active: Int = 0
    ): Response<EventResponse>

    @GET("events")
    suspend fun searchEvent(
        @Query("q") keyword: String,
        @Query("active") active: Int = -1
    ): Response<EventResponse>

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: Int
    ): Response<EventDetailResponse>
}

