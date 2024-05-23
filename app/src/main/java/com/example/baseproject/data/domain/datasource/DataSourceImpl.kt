package com.example.baseproject.data.domain.datasource

import com.example.baseproject.data.api.ApiManager
import com.example.baseproject.data.model.ApiResponse
import retrofit2.Response

class DataSourceImpl(
    private val apiManager: ApiManager
): DataSource {
    override suspend fun test(): Response<ApiResponse> {
        return apiManager.test()
    }
}