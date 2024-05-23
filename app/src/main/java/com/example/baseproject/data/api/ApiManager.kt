package com.example.baseproject.data.api

import com.example.baseproject.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiManager {
    @GET("/endpoint")
    suspend fun test(): Response<ApiResponse>
}