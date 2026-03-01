package com.charlie.domain.model

data class SurveyTemplate(
    val remoteId: Int,
    val title: String,
    val version: Int,
    val questionsJson: String
)