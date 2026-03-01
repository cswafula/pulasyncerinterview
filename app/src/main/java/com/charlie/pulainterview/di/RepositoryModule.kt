package com.charlie.pulainterview.di

import com.charlie.data.repository.FarmerRepositoryImpl
import com.charlie.data.repository.SurveyRepositoryImpl
import com.charlie.data.repository.SurveyTemplateRepositoryImpl
import com.charlie.domain.repository.FarmerRepository
import com.charlie.domain.repository.SurveyRepository
import com.charlie.domain.repository.SurveyTemplateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFarmerRepository(
        impl: FarmerRepositoryImpl
    ): FarmerRepository

    @Binds
    @Singleton
    abstract fun bindSurveyTemplateRepository(
        impl: SurveyTemplateRepositoryImpl
    ): SurveyTemplateRepository

    @Binds
    @Singleton
    abstract fun bindSurveyRepository(
        impl: SurveyRepositoryImpl
    ): SurveyRepository
}
