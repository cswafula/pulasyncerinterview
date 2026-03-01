package com.charlie.data.remote.model

import com.charlie.domain.model.SurveyResponse

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

fun SurveyResponse.toRequest() = SurveyResponseRequest(
    responseId = id,
    farmerId = farmerId,
    templateId = templateId,
    submittedAt = createdAt,
    answers = answers.map { answer ->
        AnswerDto(
            questionId = answer.questionId,
            answerJson = answer.answerJson,
            sections = answer.sections.map { section ->
                AnswerSectionDto(
                    sectionIndex = section.sectionIndex,
                    questionId = section.questionId,
                    answerJson = section.answerJson
                )
            }
        )
    }
)

