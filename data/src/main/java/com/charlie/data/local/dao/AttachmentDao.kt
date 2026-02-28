package com.charlie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charlie.data.local.entity.AttachmentEntity

@Dao
interface AttachmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Query("SELECT * FROM attachments WHERE responseId = :responseId")
    suspend fun getAttachmentsByResponseId(responseId: Int): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE isSynced = 0")
    suspend fun getPendingAttachments(): List<AttachmentEntity>

    @Query("UPDATE attachments SET isSynced = 1 WHERE responseId = :responseId")
    suspend fun markResponseAttachmentsSynced(responseId: Int)

    @Query("DELETE FROM attachments WHERE responseId = :responseId AND isSynced = 1")
    suspend fun purgeSyncedAttachmentsByResponseId(responseId: Int)
}