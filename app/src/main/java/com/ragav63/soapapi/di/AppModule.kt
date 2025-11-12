package com.ragav63.soapapi.di

import com.ragav63.soapapi.data.remote.soap.TempConvertSoapServiceImpl
import com.ragav63.soapapi.data.repository.TempRepositoryImpl
import com.ragav63.soapapi.domain.repository.TempRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSoapService(): TempConvertSoapServiceImpl = TempConvertSoapServiceImpl()

    @Provides
    @Singleton
    fun provideRepository(service: TempConvertSoapServiceImpl): TempRepository =
        TempRepositoryImpl(service)
}