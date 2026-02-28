package com.charlie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charlie.data.local.entity.SurveyAnswerEntity

@Dao
interface SurveyAnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<SurveyAnswerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: SurveyAnswerEntity): Long

    @Query("SELECT * FROM survey_answers WHERE responseId = :responseId")
    suspend fun getAnswersForResponse(responseId: Int): List<SurveyAnswerEntity>

    @Query("DELETE FROM survey_answers WHERE responseId = :responseId")
    suspend fun deleteAnswersForResponse(responseId: Int)
}