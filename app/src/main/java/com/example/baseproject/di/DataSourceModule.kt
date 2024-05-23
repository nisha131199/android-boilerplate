package com.example.baseproject.di

import com.example.baseproject.data.api.ApiManager
import com.example.baseproject.data.domain.datasource.DataSource
import com.example.baseproject.data.domain.datasource.DataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataSourceModule {

    @Singleton
    @Provides
    fun provideLoginDataSource(
        apiManger: ApiManager
    ): DataSource {
        return DataSourceImpl(apiManger)
    }
}