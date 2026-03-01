package com.charlie.domain.syncer

/**
 * This logic manages the SyncResult handling behaviour
 * @see Success Event-> All responses in the queue uploaded successfully
 * @see PartialSuccess Event-> Some uploaded, n failed, rest continued
 * @see NetworkAborted Event-> Network dropped mid-sync, so sync stopped
 * @see NothingToSync Event-> Queue was empty
 * @see AlreadyRunning Event-> A sync was already running, no duplication
 * */
sealed class SyncResult {
    data class Success(val syncedIds: List<Int>) : SyncResult()
    data class PartialSuccess(val syncedIds: List<Int>, val failures: List<ResponseFailure>) : SyncResult()
    data class NetworkAborted(val syncedIds: List<Int>, val pendingIds: List<Int>, val reason: SyncError) : SyncResult()
    data object NothingToSync : SyncResult()
    data object AlreadyRunning : SyncResult()
}

data class ResponseFailure(
    val responseId: Int,
    val error: SyncError
)