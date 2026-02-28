package com.charlie.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.charlie.data.local.converter.SyncStatusConverter
import com.charlie.data.local.entity.AttachmentEntity
import com.charlie.data.local.entity.FarmerEntity
import com.charlie.data.local.entity.SurveyAnswerEntity
import com.charlie.data.local.entity.SurveyAnswerSectionEntity
import com.charlie.data.local.entity.SurveyResponseEntity
import com.charlie.data.local.entity.SurveyTemplateEntity

@Database(
    entities = [
        FarmerEntity::class,
        SurveyTemplateEntity::class,
        SurveyResponseEntity::class,
        SurveyAnswerEntity::class,
        SurveyAnswerSectionEntity::class,
        AttachmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SyncStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {}