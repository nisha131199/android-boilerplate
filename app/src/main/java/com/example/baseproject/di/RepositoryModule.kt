package com.example.baseproject.di

import com.example.baseproject.data.domain.datasource.DataSource
import com.example.baseproject.data.domain.repository.Repository
import com.example.baseproject.data.domain.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providesLoginRepository(
        dataSource: DataSource
    ): Repository {
        return RepositoryImpl(dataSource)
    }
}