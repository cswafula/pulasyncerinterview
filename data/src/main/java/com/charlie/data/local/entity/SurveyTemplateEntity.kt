package com.charlie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SurveyTemplateEntity - Dynamic designed surveys
 * Assumption Surveys will be fetched during remote sync on Login.
 * @param version holds base for version control.
 * @param questionsJson holds the survey questions in JSON blob, basee of the dynamic structure.
 * */
@Entity(tableName = "survey_templates")
data class SurveyTemplateEntity(
    @PrimaryKey
    val remoteId: Int,
    val title: String,
    val version: Int,
    val questionsJson: String,
)
