package com.charlie.data.network

import com.charlie.domain.syncer.SyncError
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException

/**
 * Network error mapper supporting the syncer machine
 * @see fromException -> Maps a thrown network exception to a [SyncError]
 * @see fromHttpResponse -> Maps a non-2xx HTTP response to a [SyncError], Proofing logic.
 * @see shouldAbortQueue -> Determines if the queue should stop after this error.
 * Working logic: 4xx = problem with this specific request(user input or json structure), the queue can continue
 * Everything else = network or server is unhealthy so abort queue
 * Assumption: Treating IOExceptions as network degradations
 * */
object NetworkErrorMapper {

    fun fromException(throwable: Throwable): SyncError {
        return when (throwable) {
            is UnknownHostException -> SyncError.NoConnectivity
            is SocketTimeoutException -> SyncError.ConnectionTimeout
            is IOException -> SyncError.ConnectionTimeout
            else -> SyncError.Unknown(throwable)
        }
    }
    fun fromHttpResponse(response: Response<*>): SyncError {
        return SyncError.ServerError(response.code())
    }

    fun shouldAbortQueue(error: SyncError): Boolean {
        return when (error) {
            is SyncError.ServerError -> error.httpCode >= 500
            is SyncError.NoConnectivity -> true
            is SyncError.ConnectionTimeout -> true
            is SyncError.Unknown -> true
        }
    }
}