package com.charlie.domain.repository

import com.charlie.domain.model.SurveyTemplate

interface SurveyTemplateRepository {
    suspend fun syncTemplates(templates: List<SurveyTemplate>)
    suspend fun getTemplateById(templateId: Int): SurveyTemplate?
    suspend fun getAllTemplates(): List<SurveyTemplate>
}