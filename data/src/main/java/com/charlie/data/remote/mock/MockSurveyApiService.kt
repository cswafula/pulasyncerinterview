package com.charlie.data.remote.mock

import com.charlie.data.api.SurveyApiService
import com.charlie.data.remote.model.SurveyResponseRequest
import com.charlie.data.remote.model.SurveyResponseUploadResult
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Dummy implementation of SurveyApiService for testing and scenario simulation.
 * No real network calls are made.
 * @param failOnCallNumber If set, the Nth call will fail with [failureMode]
 * @param failureMode The type of failure to simulate on the Nth call
 */
class MockSurveyApiService(
    private val failOnCallNumber: Int? = null,
    private val failureMode: FailureMode = FailureMode.ServerError(500)
) : SurveyApiService {

    private var callCount = 0

    sealed class FailureMode {
        data object NoConnectivity : FailureMode()
        data object Timeout : FailureMode()
        data class ServerError(val code: Int) : FailureMode()
        data object UnknownError : FailureMode()
    }

    override suspend fun uploadSurveyResponse(
        request: SurveyResponseRequest
    ): Response<SurveyResponseUploadResult> {
        callCount++

        if (failOnCallNumber != null && callCount == failOnCallNumber) {
            return when (failureMode) {
                is FailureMode.NoConnectivity -> throw UnknownHostException("No network")
                is FailureMode.Timeout -> throw SocketTimeoutException("Connection timed out")
                is FailureMode.ServerError -> Response.error(
                    failureMode.code,
                    okhttp3.ResponseBody.create(null, "Server error")
                )
                is FailureMode.UnknownError -> throw RuntimeException("Unexpected error")
            }
        }

        return Response.success(
            SurveyResponseUploadResult(
                responseId = request.responseId,
                success = true,
                responseMessage = "Uploaded successfully"
            )
        )
    }

    fun reset() { callCount = 0 }
    fun getCallCount() = callCount
}
