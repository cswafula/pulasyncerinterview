package com.charlie.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * AttachmentEntity
 * Constraints - We can purge the attachments incase the response is deleted.
 * @param isSynced allows for image deletion after successful upload. Memory control.
 * */
@Entity(
    tableName = "attachments",
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
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val responseId: Int,
    val localUri: String,
    val mimeType: String,
    val isSynced: Boolean = false
)