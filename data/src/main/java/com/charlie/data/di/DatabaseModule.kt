package com.charlie.data.di

import android.content.Context
import androidx.room.Room
import com.charlie.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pula_survey.db"
        ).build()
    }

    @Provides
    fun provideFarmerDao(db: AppDatabase) = db.farmerDao()

    @Provides
    fun provideSurveyTemplateDao(db: AppDatabase) = db.surveyTemplateDao()

    @Provides
    fun provideSurveyResponseDao(db: AppDatabase) = db.surveyResponseDao()

    @Provides
    fun provideSurveyAnswerDao(db: AppDatabase) = db.surveyAnswerDao()

    @Provides
    fun provideSurveyAnswerSectionDao(db: AppDatabase) = db.surveyAnswerSectionDao()

    @Provides
    fun provideAttachmentDao(db: AppDatabase) = db.attachmentDao()
}