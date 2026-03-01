package com.charlie.data.syncer

import com.charlie.data.network.SyncEngine
import com.charlie.data.remote.mock.MockSurveyApiService
import com.charlie.domain.model.SurveyAnswer
import com.charlie.domain.model.SurveyResponse
import com.charlie.domain.model.SyncStatus
import com.charlie.domain.repository.SurveyRepository
import com.charlie.domain.syncer.SyncError
import com.charlie.domain.syncer.SyncResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SyncEngineTest {

    private lateinit var surveyRepository: SurveyRepository
    private lateinit var syncEngine: SyncEngine
    private lateinit var fakeApi: MockSurveyApiService

    fun mockResponse(id: Int) = SurveyResponse(
        id = id,
        farmerId = 1,
        templateId = 1,
        status = SyncStatus.PENDING,
        answers = listOf(
            SurveyAnswer(
                id = id * 10,
                responseId = id,
                questionId = 1,
                answerJson = "{\"test\":\"text}",
                sections = emptyList()
            )
        ),
        attachments = emptyList(),
        createdAt = System.currentTimeMillis()
    )

    fun mockResponseList(ids: List<Int>): List<SurveyResponse> {
        return ids.map { mockResponse(it) }
    }

    @Before
    fun setup() {
        surveyRepository = mockk(relaxed = true)
        fakeApi = MockSurveyApiService()
        syncEngine = SyncEngine(surveyRepository, fakeApi)
    }


    @Test
    fun `sync returns NothingToSync when queue is empty`() = runTest {
        coEvery { surveyRepository.getPendingResponses() } returns emptyList()
        val result = syncEngine.sync()

        assertTrue(result is SyncResult.NothingToSync)
        coVerify(exactly = 0) {
            surveyRepository.updateSyncStatus(any(), any())
        }
    }

    @Test
    fun `sync returns Success when all responses upload successfully`() = runTest {
        val responses = mockResponseList(listOf(1,2,3))
        coEvery { surveyRepository.getPendingResponses() } returns responses
        val result = syncEngine.sync()

        assertTrue(result is SyncResult.Success)
        val success = result as SyncResult.Success
        assertEquals(listOf(1, 2, 3), success.syncedIds)

        // All three mocks are marked SYNCING then SYNCED
        coVerify(exactly = 3) {
            surveyRepository.updateSyncStatus(any(), SyncStatus.SYNCING)
        }
        coVerify(exactly = 3) {
            surveyRepository.updateSyncStatus(any(), SyncStatus.SYNCED)
        }

        // Ensure Attachments purged for each
        coVerify(exactly = 3) {
            surveyRepository.deleteSyncedResponseAttachments(any())
        }
    }

    @Test
    fun `sync aborts queue when no connectivity`() = runTest {
        val responses = mockResponseList(listOf(1,2,3,4,5))
        coEvery { surveyRepository.getPendingResponses() } returns responses

        val fakeApi = MockSurveyApiService(
            failOnCallNumber = 2,
            failureMode = MockSurveyApiService.FailureMode.NoConnectivity
        )
        syncEngine = SyncEngine(surveyRepository, fakeApi)

        val result = syncEngine.sync()

        assertTrue(result is SyncResult.NetworkAborted)
        val aborted = result as SyncResult.NetworkAborted
        assertTrue(aborted.reason is SyncError.NoConnectivity)
        assertEquals(listOf(1), aborted.syncedIds)

        assertEquals(2, fakeApi.getCallCount())
    }

    @Test
    fun `sync aborts queue on 5xx server error`() = runTest {
        val responses = mockResponseList(listOf(1,2,3,4,5))
        coEvery { surveyRepository.getPendingResponses() } returns responses

        val fakeApi = MockSurveyApiService(
            failOnCallNumber = 3,
            failureMode = MockSurveyApiService.FailureMode.ServerError(500)
        )
        syncEngine = SyncEngine(surveyRepository, fakeApi)

        val result = syncEngine.sync()

        assertTrue(result is SyncResult.NetworkAborted)
        val aborted = result as SyncResult.NetworkAborted
        assertTrue(aborted.reason is SyncError.ServerError)
        assertEquals(500, (aborted.reason as SyncError.ServerError).httpCode)
        assertEquals(3, fakeApi.getCallCount())
    }

    @Test
    fun `resetStuckResponses is called at start of every sync`() = runTest {
        coEvery { surveyRepository.getPendingResponses() } returns emptyList()

        syncEngine.sync()

        coVerify(exactly = 1) { surveyRepository.resetStuckResponses() }
    }
}