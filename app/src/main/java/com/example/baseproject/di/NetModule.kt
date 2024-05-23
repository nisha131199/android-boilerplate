package com.example.baseproject.di

import android.content.Context
import com.example.baseproject.BuildConfig
import com.example.baseproject.data.api.ApiManager
import com.example.baseproject.data.api.DynamicTokenInterceptor
import com.example.baseproject.data.api.NullOnEmptyConverterFactory
import com.example.baseproject.utils.HttpUtils
import com.example.baseproject.storage.PreferenceHandler
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetModule {

    @Provides
    @Singleton
    fun provideLocalRepository(
        @ApplicationContext
        context: Context
    ) = PreferenceHandler(context)

    @Singleton
    @Provides
    fun provideRetrofit(handler: PreferenceHandler): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(getRetrofitClient(accessToken = null, handler = handler))
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().disableHtmlEscaping().setExclusionStrategies()
                        .create()
                )
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthApiManger(retrofit: Retrofit): ApiManager {
        return retrofit.create(ApiManager::class.java)
    }

    private fun getRetrofitClient(
        accessToken: String? = null,
        handler: PreferenceHandler? = null,
    ): OkHttpClient {
        return HttpUtils.getInstance().unsafeOkHttpClientBuilder.apply {
            /**  if network call is fails accessToken getting null, that's why i am setting DynamicTokenInterceptor for dynamically fetch access token again  */
            if (accessToken.isNullOrEmpty()) {
                handler?.let{ this.addInterceptor(DynamicTokenInterceptor(it)) }
            }
        }
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    accessToken?.let { token ->
                        it.header(
                            "Authorization",
                            "Bearer $token"
                        )
                    }

                }.build())
            }.also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
                client.connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
            }
            .build()
    }

   /* @Singleton
    @Provides
    fun buildApiManager(): ApiManager {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(getRetrofitClient())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().disableHtmlEscaping().setExclusionStrategies()
                        .create()
                )
            )
            .build()
            .create(ApiManager::class.java)
    }*/

}
