package com.charlie.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * SurveyResponseEntity - Survey Response.
 * Constraints - Can't delete a farmer if survey responses exist. Proofing, incase we set data purging to observe device storage.
 * Constraints - Cant delete a survey template if survey responses exist. Proofing too.
 * To form the base of responses & syncing state.
 * */
@Entity(
    tableName = "survey_responses",
    foreignKeys = [
        ForeignKey(
            entity = FarmerEntity::class,
            parentColumns = ["remoteId"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SurveyTemplateEntity::class,
            parentColumns = ["remoteId"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("farmerId"),
        Index("templateId"),
        Index("status")
    ]
)
data class SurveyResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val farmerId: Int,
    val templateId: Int,
    val status: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val syncedAt: Long? = null
)
