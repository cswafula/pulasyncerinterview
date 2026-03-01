package com.charlie.domain.syncer

/**
 * This sealed class manages the Sync Error handling behaviour
 * No connectivity at all -> don't attempt any further uploads
 * Request timed out -> network is likely degraded, stop the queue
 * Server error -> assume specific to one response, others queued can continue
 * Unknown or unhandled -> stop entire queue to be safe
 * */
sealed class SyncError {
    data object NoConnectivity : SyncError()
    data object ConnectionTimeout : SyncError()
    data class ServerError(val httpCode: Int) : SyncError()
    data class Unknown(val cause: Throwable) : SyncError()
}