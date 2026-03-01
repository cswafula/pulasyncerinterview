package com.charlie.data.repository

import com.charlie.data.local.dao.AttachmentDao
import com.charlie.data.local.dao.SurveyAnswerDao
import com.charlie.data.local.dao.SurveyAnswerSectionDao
import com.charlie.data.local.dao.SurveyResponseDao
import com.charlie.data.mappers.toDomain
import com.charlie.data.mappers.toEntity
import com.charlie.domain.model.Attachment
import com.charlie.domain.model.SurveyAnswer
import com.charlie.domain.model.SurveyResponse
import com.charlie.domain.model.SyncStatus
import com.charlie.domain.repository.SurveyRepository
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(
    private val responseDao: SurveyResponseDao,
    private val answerDao: SurveyAnswerDao,
    private val sectionDao: SurveyAnswerSectionDao,
    private val attachmentDao: AttachmentDao
) : SurveyRepository {

    /**
     * Saving Surveys logic
     * Based on my architectural approach
     * Save the SurveyResponse
     * Using localResponseId to map relationship, save the SurveyAnswers
     * Retrieve the savedAnswers for indexing(using their localIds), then save the SurveyAnswerSections
     * Finally, save the attachments
     * */
    override suspend fun saveSurveyResponse(
        response: SurveyResponse,
        answers: List<SurveyAnswer>,
        attachments: List<Attachment>
    ): Int {
        val localId = responseDao.insertResponse(response.toEntity()).toInt()
        val answerEntities = answers.map { it.toEntity(responseId = localId) }
        answerDao.insertAnswers(answerEntities)

        val savedAnswers = answerDao.getAnswersForResponse(localId)

        val sectionEntities = answers.flatMapIndexed { index, answer ->
            val savedAnswerId = savedAnswers[index].id
            answer.sections.map { it.toEntity(parentAnswerId = savedAnswerId) }
        }

        if (sectionEntities.isNotEmpty()) {
            sectionDao.insertSections(sectionEntities)
        }

        attachments.forEach {
            attachmentDao.insertAttachment(it.toEntity(responseId = localId))
        }

        return localId
    }

    override suspend fun getPendingResponses(): List<SurveyResponse> {
        return responseDao.getPendingResponses().map { entity ->
            val answers = buildAnswersForResponse(entity.id)
            val attachments = attachmentDao.getAttachmentsByResponseId(entity.id)
                .map { it.toDomain() }
            entity.toDomain(answers = answers, attachments = attachments)
        }
    }

    private suspend fun buildAnswersForResponse(responseId: Int): List<SurveyAnswer> {
        val answerEntities = answerDao.getAnswersForResponse(responseId)
        return answerEntities.map { answerEntity ->
            val sections = sectionDao.getSectionsForAnswer(answerEntity.id)
                .map { it.toDomain() }
            answerEntity.toDomain(sections = sections)
        }
    }

    override suspend fun getResponseById(responseId: Int): SurveyResponse? {
        val entity = responseDao.getResponseById(responseId) ?: return null
        val answers = buildAnswersForResponse(responseId)
        val attachments = attachmentDao.getAttachmentsByResponseId(responseId)
            .map { it.toDomain() }
        return entity.toDomain(answers = answers, attachments = attachments)
    }

    override suspend fun updateSyncStatus(
        responseId: Int,
        state: SyncStatus
    ) {
        when(state){
            SyncStatus.SYNCED -> responseDao.markSurveyAsSynced(responseId, System.currentTimeMillis())
            SyncStatus.FAILED -> responseDao.markSurveyAsFailed(responseId)
            SyncStatus.SYNCING -> responseDao.markSurveyAsSyncing(responseId)
            else -> {}
        }
    }

    override suspend fun resetStuckResponses() {
        responseDao.resetIncompleteSyncingResponses()
    }

    override suspend fun deleteSyncedResponseAttachments(responseId: Int) {
        attachmentDao.purgeSyncedAttachmentsByResponseId(responseId)
    }

}