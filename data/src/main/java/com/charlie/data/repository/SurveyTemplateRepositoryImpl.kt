package com.charlie.data.repository

import com.charlie.data.local.dao.SurveyTemplateDao
import com.charlie.domain.model.SurveyTemplate
import com.charlie.domain.repository.SurveyTemplateRepository
import javax.inject.Inject

class SurveyTemplateRepositoryImpl @Inject constructor(
    private val surveyTemplateDao: SurveyTemplateDao
) : SurveyTemplateRepository {

    override suspend fun syncTemplates(templates: List<SurveyTemplate>) {
        TODO("Not yet implemented")
    }

    override suspend fun getTemplateById(templateId: Int): SurveyTemplate? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTemplates(): List<SurveyTemplate> {
        TODO("Not yet implemented")
    }

}