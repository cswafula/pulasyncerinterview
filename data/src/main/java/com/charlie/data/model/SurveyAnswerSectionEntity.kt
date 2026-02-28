package com.charlie.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Survery SubSection Answer.This supports the subsection concept based on parentAnswer
 * @param parentAnswerId relate to first question, denoted as parent
 * @param sectionIndex create a way to preserve the sequence order of questions
 * Allows reconstruction of a fully nested JSON before upload to remote based on desired model.
 * */
@Entity(
    tableName = "survey_answer_sections",
    foreignKeys = [
        ForeignKey(
            entity = SurveyAnswerEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentAnswerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("parentAnswerId")]
)
data class SurveyAnswerSectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val parentAnswerId: Int,
    val sectionIndex: Int,
    val questionId: Int,
    val answerJson: String
)