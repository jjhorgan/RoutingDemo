package com.jjh.platformsciencedemo.businesslogic

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object LogicModule {

    @Provides
    fun providesSecretAlgorithm() : SecretAlgorithm{
        return SecretAlgorithmImpl()
    }
}