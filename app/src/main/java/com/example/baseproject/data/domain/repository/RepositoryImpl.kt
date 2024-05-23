package com.example.baseproject.data.domain.repository

import com.example.baseproject.App
import com.example.baseproject.data.api.Resource
import com.example.baseproject.data.domain.datasource.DataSource
import com.example.baseproject.data.model.ErrorResponse
import com.example.baseproject.data.model.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(
    private val dataSource: DataSource
): Repository {
    override fun test(): Flow<Resource<ApiResponse>> {
        return flow {
            val response = dataSource.test()
            if(response.isSuccessful){
                response.body()?.let {result->
                    emit(
                        Resource.Success(
                            result
                        )
                    )
                }
            }
            else if (response.code() == 403) {
                emit(
                    Resource.InvalidToken()
                )
            }
            else {
                emit(
                    Resource.Error(
                        App.gson.fromJson(
                            response.errorBody()?.charStream(),
                            ErrorResponse::class.java
                        ).message
                    )
                )
            }
        }
    }
}