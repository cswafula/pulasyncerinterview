package com.charlie.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Constraint - We can purge the answers incase the response is deleted.
 * @param answerJson - Store the JSON blob of the answer. Safer to accommodate dynamic structure
 * */
@Entity(
    tableName = "survey_answers",
    foreignKeys = [
        ForeignKey(
            entity = SurveyResponseEntity::class,
            parentColumns = ["id"],
            childColumns = ["responseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("responseId")]
)
data class SurveyAnswerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val responseId: Int,
    val questionId: Int,
    val answerJson: String
)