package com.charlie.data.network

import com.charlie.data.api.SurveyApiService
import com.charlie.data.remote.model.toRequest
import com.charlie.domain.model.SurveyResponse
import com.charlie.domain.model.SyncStatus
import com.charlie.domain.repository.SurveyRepository
import com.charlie.domain.syncer.ResponseFailure
import com.charlie.domain.syncer.SyncError
import com.charlie.domain.syncer.SyncResult
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncEngine @Inject constructor(
    private val surveyRepository: SurveyRepository,
    private val apiService: SurveyApiService
) {

    private val syncMutex = Mutex()

    /**
     * Entry point for all future sync operations.
     * This is to be called from background WorkManager job
     * Ensures only one sync will run at a time. Concurrent syncs prevention
     */
    suspend fun sync(): SyncResult {
        if (!syncMutex.tryLock()) return SyncResult.AlreadyRunning
        return try {
            executSync()
        } finally {
            syncMutex.unlock()
        }
    }

    private suspend fun executSync(): SyncResult {
        surveyRepository.resetStuckResponses()

        val pendingResponses = surveyRepository.getPendingResponses()
        if (pendingResponses.isEmpty()) return SyncResult.NothingToSync

        val syncedIds = mutableListOf<Int>()
        val failures = mutableListOf<ResponseFailure>()

        for (response in pendingResponses) {

            surveyRepository.updateSyncStatus(response.id, SyncStatus.SYNCING)
            val uploadResult = uploadResponse(response)

            when {
                uploadResult.isSuccess -> {
                    surveyRepository.updateSyncStatus(response.id, SyncStatus.SYNCED)
                    surveyRepository.deleteSyncedResponseAttachments(response.id)
                    syncedIds.add(response.id)
                }

                else -> {
                    val error = when (val err = uploadResult.exceptionOrNull()) {
                        is HttpErrorException -> err.syncError
                        is Exception -> NetworkErrorMapper.fromException(err)
                        else -> SyncError.Unknown(RuntimeException("Unknown failure"))
                    }

                    if (NetworkErrorMapper.shouldAbortQueue(error)) {
                        surveyRepository.updateSyncStatus(response.id, SyncStatus.PENDING)

                        val remainingIds = pendingResponses.map { it.id }
                            .filter { it != response.id && it !in syncedIds }

                        return SyncResult.NetworkAborted(
                            syncedIds = syncedIds,
                            pendingIds = remainingIds,
                            reason = error
                        )
                    }

                    surveyRepository.updateSyncStatus(response.id, SyncStatus.FAILED)
                    failures.add(ResponseFailure(responseId = response.id, error = error))
                }
            }
        }

        return if (failures.isEmpty()) {
            SyncResult.Success(syncedIds = syncedIds)
        } else {
            SyncResult.PartialSuccess(syncedIds = syncedIds, failures = failures)
        }
    }

    private suspend fun uploadResponse(response: SurveyResponse): Result<Unit> {
        return try {
            val request = response.toRequest()
            val httpResponse = apiService.uploadSurveyResponse(request)

            if (httpResponse.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = NetworkErrorMapper.fromHttpResponse(httpResponse)
                Result.failure(HttpErrorException(httpResponse.code(), error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * My custom exception used to carry HTTP error codes through Result.failure
 * so the sync engine can distinguish HTTP errors from network exceptions.
 */
class HttpErrorException(
    val code: Int,
    val syncError: SyncError
) : Exception("HTTP error $code")