package com.jjh.platformsciencedemo.di

import android.content.Context
import com.jjh.platformsciencedemo.repository.RouteDataRepo
import com.jjh.platformsciencedemo.repository.RouteDataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun provideRputeDataRepo(@ApplicationContext context: Context) : RouteDataRepo {
        return RouteDataRepositoryImpl(context)
    }
}