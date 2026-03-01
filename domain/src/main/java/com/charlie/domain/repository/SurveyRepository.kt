package com.charlie.domain.repository

import com.charlie.domain.model.Attachment
import com.charlie.domain.model.SurveyAnswer
import com.charlie.domain.model.SurveyResponse
import com.charlie.domain.model.SyncStatus

interface SurveyRepository {
    suspend fun saveSurveyResponse(
        response: SurveyResponse,
        answers: List<SurveyAnswer>,
        attachments: List<Attachment>
    ): Int

    suspend fun getPendingResponses(): List<SurveyResponse>
    suspend fun getResponseById(responseId: Int): SurveyResponse?
    suspend fun updateSyncStatus(responseId: Int, state: SyncStatus)
    suspend fun resetStuckResponses()
    suspend fun deleteSyncedResponseAttachments(responseId: Int)

}