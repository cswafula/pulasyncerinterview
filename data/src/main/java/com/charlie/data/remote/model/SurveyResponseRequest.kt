package com.charlie.data.remote.model

data class SurveyResponseRequest(
    val responseId: Int,
    val farmerId: Int,
    val templateId: Int,
    val submittedAt: Long,
    val answers: List<AnswerDto>
)

data class AnswerDto(
    val questionId: Int,
    val answerJson: String,
    val sections: List<AnswerSectionDto> = emptyList()
)

data class AnswerSectionDto(
    val sectionIndex: Int,
    val questionId: Int,
    val answerJson: String
)

