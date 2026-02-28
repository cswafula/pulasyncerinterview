package com.charlie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charlie.data.local.entity.SurveyTemplateEntity

/**
 * SurveyTemplateDao guide flow
 * My foundation is, Templates are equally retrieved from remote and inflated to the local db
 * */
@Dao
interface SurveyTemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<SurveyTemplateEntity>)

    @Query("SELECT * FROM survey_templates WHERE remoteId = :templateId")
    suspend fun getTemplateById(templateId: Int): SurveyTemplateEntity?

    @Query("SELECT * FROM survey_templates")
    suspend fun getAllTemplates(): List<SurveyTemplateEntity>
}