package com.charlie.data.mappers

import com.charlie.data.local.entity.AttachmentEntity
import com.charlie.data.local.entity.SurveyAnswerEntity
import com.charlie.data.local.entity.SurveyAnswerSectionEntity
import com.charlie.data.local.entity.SurveyResponseEntity
import com.charlie.domain.model.Attachment
import com.charlie.domain.model.SurveyAnswer
import com.charlie.domain.model.SurveyAnswerSection
import com.charlie.domain.model.SurveyResponse
import com.charlie.domain.model.SyncStatus

fun SurveyResponse.toEntity() = SurveyResponseEntity(
    farmerId = farmerId,
    templateId = templateId,
    status = SyncStatus.PENDING,
    createdAt = createdAt
)

fun SurveyResponseEntity.toDomain(
    answers: List<SurveyAnswer> = emptyList(),
    attachments: List<Attachment> = emptyList()
) = SurveyResponse(
    id = id,
    farmerId = farmerId,
    templateId = templateId,
    status = status,
    answers = answers,
    attachments = attachments,
    createdAt = createdAt,
    syncedAt = syncedAt
)

fun SurveyAnswer.toEntity(responseId: Int) = SurveyAnswerEntity(
    responseId = responseId,
    questionId = questionId,
    answerJson = answerJson
)

fun SurveyAnswerEntity.toDomain(sections: List<SurveyAnswerSection>) = SurveyAnswer(
    id = id,
    responseId = responseId,
    questionId = questionId,
    answerJson = answerJson,
    sections = sections
)

fun SurveyAnswerSection.toEntity(parentAnswerId: Int) = SurveyAnswerSectionEntity(
    parentAnswerId = parentAnswerId,
    sectionIndex = sectionIndex,
    questionId = questionId,
    answerJson = answerJson
)

fun SurveyAnswerSectionEntity.toDomain() = SurveyAnswerSection(
    id = id,
    parentAnswerId = parentAnswerId,
    sectionIndex = sectionIndex,
    questionId = questionId,
    answerJson = answerJson
)

fun Attachment.toEntity(responseId: Int) = AttachmentEntity(
    responseId = responseId,
    localUri = localUri,
    mimeType = mimeType,
    isSynced = false
)

fun AttachmentEntity.toDomain() = Attachment(
    id = id,
    responseId = responseId,
    localUri = localUri,
    mimeType = mimeType,
    isSynced = isSynced
)