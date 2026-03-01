package com.charlie.data.repository

import com.charlie.data.local.dao.AttachmentDao
import com.charlie.data.local.dao.SurveyAnswerDao
import com.charlie.data.local.dao.SurveyAnswerSectionDao
import com.charlie.data.local.dao.SurveyResponseDao
import com.charlie.data.local.entity.SurveyAnswerEntity
import com.charlie.data.local.entity.SurveyAnswerSectionEntity
import com.charlie.data.local.entity.SurveyResponseEntity
import com.charlie.domain.model.Attachment
import com.charlie.domain.model.SurveyAnswer
import com.charlie.domain.model.SurveyAnswerSection
import com.charlie.domain.model.SurveyResponse
import com.charlie.domain.model.SyncStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SurveyRepositoryTest {

    private lateinit var responseDao: SurveyResponseDao
    private lateinit var answerDao: SurveyAnswerDao
    private lateinit var sectionDao: SurveyAnswerSectionDao
    private lateinit var attachmentDao: AttachmentDao
    private lateinit var repository: SurveyRepositoryImpl

    private fun mockResponseEntity(
        id: Int = 1,
        status: SyncStatus = SyncStatus.PENDING
    ) = SurveyResponseEntity(
        id = id,
        farmerId = 10,
        templateId = 5,
        status = status,
        createdAt = 1000L
    )

    private fun mockResponseDomain(id: Int = 0) = SurveyResponse(
        id = id,
        farmerId = 10,
        templateId = 5,
        status = SyncStatus.PENDING,
        answers = listOf(
            SurveyAnswer(
                id = 0,
                responseId = id,
                questionId = 1,
                answerJson = "{\"test\":\"text}",
                sections = listOf(
                    SurveyAnswerSection(
                        id = 0,
                        parentAnswerId = 0,
                        sectionIndex = 0,
                        questionId = 2,
                        answerJson = "{\"test\":\"text}",
                    )
                )
            )
        ),
        attachments = listOf(
            Attachment(
                id = 0,
                responseId = id,
                localUri = "/storage/photo1.jpg",
                mimeType = "image/jpeg",
                isSynced = false
            )
        ),
        createdAt = 1000L
    )

    @Before
    fun setup() {
        responseDao = mockk(relaxed = true)
        answerDao = mockk(relaxed = true)
        sectionDao = mockk(relaxed = true)
        attachmentDao = mockk(relaxed = true)

        repository = SurveyRepositoryImpl(
            responseDao, answerDao, sectionDao, attachmentDao
        )
    }

    @Test
    fun `saveSurveyResponse inserts response and returns local id`() = runTest {
        coEvery { responseDao.insertResponse(any()) } returns 4L
        coEvery { answerDao.getAnswersForResponse(4) } returns listOf(
            SurveyAnswerEntity(id = 100, responseId = 4, questionId = 1, answerJson = "\"test: {}\"")
        )

        val response = mockResponseDomain()
        val localId = repository.saveSurveyResponse(
            response = response,
            answers = response.answers,
            attachments = response.attachments
        )

        assertEquals(4, localId)
        coVerify { responseDao.insertResponse(any()) }
        coVerify { answerDao.insertAnswers(any()) }
        coVerify { attachmentDao.insertAttachment(any()) }
    }

    @Test
    fun `getPendingResponses returns only PENDING and FAILED responses`() = runTest {
        val pendingEntities = listOf(
            mockResponseEntity(id = 1, status = SyncStatus.PENDING),
            mockResponseEntity(id = 2, status = SyncStatus.FAILED)
        )

        coEvery { responseDao.getPendingResponses() } returns pendingEntities
        coEvery { answerDao.getAnswersForResponse(any()) } returns emptyList()
        coEvery { attachmentDao.getAttachmentsByResponseId(any()) } returns emptyList()

        val result = repository.getPendingResponses()

        assertEquals(2, result.size)
        assertTrue(result.all {
            it.status == SyncStatus.PENDING || it.status == SyncStatus.FAILED
        })
    }

    @Test
    fun `getPendingResponses returns empty list when all synced`() = runTest {
        coEvery { responseDao.getPendingResponses() } returns emptyList()
        val result = repository.getPendingResponses()
        assertTrue(result.isEmpty())
    }


    @Test
    fun `deleteSyncedResponseAttachments invokes work to dao`() = runTest {
        repository.deleteSyncedResponseAttachments(1)
        coVerify { attachmentDao.purgeSyncedAttachmentsByResponseId(1) }
    }
}