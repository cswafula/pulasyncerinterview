package com.charlie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charlie.data.local.entity.FarmerEntity

/**
 * FarmersDao guiding flow
 * My foundation is, Farmers are retrieved from remote and inflated to the local db
 * */
@Dao
interface FarmerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(farmers: List<FarmerEntity>)

    @Query("SELECT * FROM farmers WHERE remoteId = :farmerId")
    suspend fun getFarmerById(farmerId: Int): FarmerEntity?

    @Query("SELECT * FROM farmers")
    suspend fun getAllFarmers(): List<FarmerEntity>

}