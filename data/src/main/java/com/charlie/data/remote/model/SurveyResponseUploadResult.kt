package com.charlie.data.remote.model

data class SurveyResponseUploadResult(
    val responseId: Int,
    val success: Boolean,
    val responseMessage: String
)