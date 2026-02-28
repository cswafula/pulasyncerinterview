package com.charlie.data.api

import com.charlie.data.remote.model.SurveyResponseRequest
import com.charlie.data.remote.model.SurveyResponseUploadResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SurveyApiService {

    @POST("v1/survey/uploadResponse")
    suspend fun uploadSurveyResponse(
        @Body request: SurveyResponseRequest
    ): Response<SurveyResponseUploadResult>
}