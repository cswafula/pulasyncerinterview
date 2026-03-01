package com.charlie.data.repository

import com.charlie.data.local.dao.AttachmentDao
import com.charlie.data.local.dao.SurveyAnswerDao
import com.charlie.data.local.dao.SurveyAnswerSectionDao
import com.charlie.data.local.dao.SurveyResponseDao
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

    override suspend fun saveSurveyResponse(
        response: SurveyResponse,
        answers: List<SurveyAnswer>,
        attachments: List<Attachment>
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getPendingResponses(): List<SurveyResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getResponseById(responseId: Int): SurveyResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun updateSyncStatus(
        responseId: Int,
        state: SyncStatus
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun resetStuckResponses() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSyncedResponseAttachments(responseId: Int) {
        TODO("Not yet implemented")
    }

}