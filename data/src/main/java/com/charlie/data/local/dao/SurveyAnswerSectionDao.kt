package com.charlie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charlie.data.local.entity.SurveyAnswerSectionEntity

/**
 *
 * */
@Dao
interface SurveyAnswerSectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<SurveyAnswerSectionEntity>)

    @Query("SELECT * FROM survey_answer_sections WHERE parentAnswerId = :parentAnswerId ORDER BY sectionIndex ASC")
    suspend fun getSectionsForAnswer(parentAnswerId: Int): List<SurveyAnswerSectionEntity>

    @Query("SELECT s.* FROM survey_answer_sections s INNER JOIN survey_answers a ON s.parentAnswerId = a.id WHERE a.responseId = :responseId ORDER BY s.sectionIndex ASC")
    suspend fun getSectionsByResponse(responseId: Int): List<SurveyAnswerSectionEntity>
}