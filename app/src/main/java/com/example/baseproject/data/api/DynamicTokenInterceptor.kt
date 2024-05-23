package com.example.baseproject.data.api

import com.example.baseproject.storage.PreferenceHandler
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class DynamicTokenInterceptor  @Inject constructor(private val handler: PreferenceHandler) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val accessToken: String = handler.readString(handler.authorizationToken, "")
        if (accessToken.isNotBlank())
            builder.addHeader("Authorization", "Bearer $accessToken")
        return chain.proceed(builder.build())
    }
}