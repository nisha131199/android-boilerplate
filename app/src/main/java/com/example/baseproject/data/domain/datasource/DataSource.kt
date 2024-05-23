package com.example.baseproject.data.domain.datasource

import com.example.baseproject.data.model.ApiResponse
import retrofit2.Response

interface DataSource {
    suspend fun test(): Response<ApiResponse>
}