package com.charlie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charlie.data.local.entity.SurveyResponseEntity

/**
 * SurveyResponseDao thought flow - Holds core sync state machine
 * PENDING & FAILED responses need to be synced
 * queued responses are to be marked as SYNCING
 * synced responses are to be marked as SYNCED
 * failed responses are to be marked as FAILED
 * crash proofing; incomplete SYNCING responses are reverted back to PENDING
 * */
@Dao
interface SurveyResponseDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertResponse(response: SurveyResponseEntity): Long

    @Query("SELECT * FROM survey_responses WHERE id = :responseId")
    suspend fun getResponseById(responseId: Int): SurveyResponseEntity?

    @Query("SELECT * FROM survey_responses WHERE status IN ('PENDING', 'FAILED') ORDER BY createdAt ASC")
    suspend fun getPendingResponses(): List<SurveyResponseEntity>

    @Query("UPDATE survey_responses SET status = 'SYNCING' WHERE id = :responseId")
    suspend fun markSurveyAsSyncing(responseId: Int)

    @Query("UPDATE survey_responses SET status = 'SYNCED', syncedAt = :syncedAt WHERE id = :responseId")
    suspend fun markSurveyAsSynced(responseId: Int, syncedAt: Long)

    @Query("UPDATE survey_responses SET status = 'FAILED' WHERE id = :responseId")
    suspend fun markSurveyAsFailed(responseId: Int)

    @Query("UPDATE survey_responses SET status = 'PENDING' WHERE status = 'SYNCING'")
    suspend fun resetIncompleteSyncingResponses()

    @Query("UPDATE survey_responses SET status = 'PENDING' WHERE status = 'SYNCING' AND id IN (:responseIds)")
    suspend fun resetIncompleteSyncingResponsesByIds(responseIds: List<Int>)

}