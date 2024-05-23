package com.example.baseproject.data.model

data class ApiResponse (
    val statusCode: String,
    val message: String,
    val data: Any? = null
)