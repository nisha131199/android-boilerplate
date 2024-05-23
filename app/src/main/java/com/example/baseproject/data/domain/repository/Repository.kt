package com.example.baseproject.data.domain.repository

import com.example.baseproject.data.api.Resource
import com.example.baseproject.data.model.ApiResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun test(): Flow<Resource<ApiResponse>>
}