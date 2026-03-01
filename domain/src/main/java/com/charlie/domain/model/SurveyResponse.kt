package com.charlie.domain.model

data class SurveyResponse(
    val id: Int,
    val farmerId: Int,
    val templateId: Int,
    val status: SyncStatus,
    val answers: List<SurveyAnswer> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val createdAt: Long,
    val syncedAt: Long? = null
)

data class SurveyAnswer(
    val id: Int,
    val responseId: Int,
    val questionId: Int,
    val answerJson: String,
    val sections: List<SurveyAnswerSection> = emptyList()
)

data class SurveyAnswerSection(
    val id: Int,
    val parentAnswerId: Int,
    val sectionIndex: Int,
    val questionId: Int,
    val answerJson: String
)

data class Attachment(
    val id: Int,
    val responseId: Int,
    val localUri: String,
    val mimeType: String,
    val isSynced: Boolean
)