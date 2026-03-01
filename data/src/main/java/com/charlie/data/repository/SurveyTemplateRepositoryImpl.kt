package com.charlie.data.repository

import com.charlie.data.local.dao.SurveyTemplateDao
import com.charlie.data.mappers.toDomain
import com.charlie.data.mappers.toEntity
import com.charlie.domain.model.SurveyTemplate
import com.charlie.domain.repository.SurveyTemplateRepository
import javax.inject.Inject

class SurveyTemplateRepositoryImpl @Inject constructor(
    private val surveyTemplateDao: SurveyTemplateDao
) : SurveyTemplateRepository {

    override suspend fun syncTemplates(templates: List<SurveyTemplate>) {
        surveyTemplateDao.insertAll(templates.map { it.toEntity() })
    }

    override suspend fun getTemplateById(templateId: Int): SurveyTemplate? {
        return surveyTemplateDao.getTemplateById(templateId)?.toDomain()
    }

    override suspend fun getAllTemplates(): List<SurveyTemplate> {
        return surveyTemplateDao.getAllTemplates().map { it.toDomain() }
    }

}